package de.master.telemetry.telemetry;

import de.master.telemetry.telemetry.dto.TelemetryMessage;
import de.master.telemetry.vehicle.VehicleRepository;
import de.master.telemetry.vehicle.VehicleNotFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.List;
import java.time.Duration;
import java.time.Instant;


@Service
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;
    private final VehicleRepository vehicleRepository;
    private final Validator validator;
    private final ProcessedMessageRepository processedMessageRepository;
    private final TelemetrySequenceGapRepository sequenceGapRepository;
    private final Duration sequenceGapTimeout;

    public TelemetryService(
            TelemetryRepository telemetryRepository,
            VehicleRepository vehicleRepository,
            ProcessedMessageRepository processedMessageRepository,
            TelemetrySequenceGapRepository sequenceGapRepository,
            Validator validator,
            @Value("${telemetry.sequence-gap.timeout}") Duration sequenceGapTimeout) {

        this.telemetryRepository = telemetryRepository;
        this.vehicleRepository = vehicleRepository;
        this.processedMessageRepository = processedMessageRepository;
        this.sequenceGapRepository = sequenceGapRepository;
        this.validator = validator;
        this.sequenceGapTimeout = sequenceGapTimeout;
    }

    private static final Logger log =
            LoggerFactory.getLogger(TelemetryService.class);


    @Transactional
    public Telemetry saveTelemetry(TelemetryMessage message) {

        // Validate telemetry message
        Set<ConstraintViolation<TelemetryMessage>> violations =
                validator.validate(message);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Check if vehicle exists
        if (!vehicleRepository.existsById(message.vehicleId())) {
            throw new VehicleNotFoundException(message.vehicleId());
        }

        // Register message if it has not been processed before
        boolean isNewMessage = processedMessageRepository.registerIfNew(
                message.vehicleId(),
                message.sequenceNumber()
        );

        if (!isNewMessage) {
            log.info(
                    "Duplicate telemetry message ignored for vehicle {} with sequence number {}",
                    message.vehicleId(),
                    message.sequenceNumber()
            );
            return null;
        }

        // Retrieve the previously highest sequence number
        Optional<Telemetry> lastTelemetry =
                telemetryRepository.findTopByVehicleIdOrderBySequenceNumberDesc(
                        message.vehicleId()
                );

        // Convert DTO into database entity
        Telemetry telemetry = new Telemetry(
                message.vehicleId(),
                message.timestamp(),
                message.sequenceNumber(),
                message.rpm(),
                message.speed(),
                message.latitude(),
                message.longitude(),
                message.coolantTemperature(),
                message.oilTemperature(),
                message.throttlePosition(),
                message.engineLoad(),
                message.intakeAirTemperature(),
                message.fuelLevel(),
                message.gear()
        );

        // Persist telemetry
        Telemetry savedTelemetry = telemetryRepository.saveAndFlush(telemetry);

        long currentSequence = message.sequenceNumber();

        // Handle sequence gaps
        if (lastTelemetry.isEmpty()) {

            // First message: sequence numbers before it are missing
            if (currentSequence > 1) {
                sequenceGapRepository.createGap(
                        message.vehicleId(),
                        1,
                        currentSequence - 1,
                        Instant.now().plus(sequenceGapTimeout)
                );
            }

        } else {

            long previousHighest =
                    lastTelemetry.get().getSequenceNumber();

            if (currentSequence > previousHighest + 1) {

                // A new gap was detected
                try {
                    sequenceGapRepository.createGap(
                            message.vehicleId(),
                            previousHighest + 1,
                            currentSequence - 1,
                            Instant.now().plus(sequenceGapTimeout)
                    );
                } catch (RuntimeException e) {
                    log.error("Failed to create telemetry sequence gap", e);
                    throw e;
                }

                log.warn(
                        "Telemetry sequence gap detected for vehicle {}. Missing sequence numbers: {} to {}",
                        message.vehicleId(),
                        previousHighest + 1,
                        currentSequence - 1
                );

            } else if (currentSequence <= previousHighest) {

                // A late message may fill an existing gap
                Optional<TelemetrySequenceGapRepository.Gap> gap =
                        sequenceGapRepository.findOpenGapContaining(
                                message.vehicleId(),
                                currentSequence
                        );

                gap.ifPresent(found ->
                        sequenceGapRepository.resolveSequenceNumber(
                                found,
                                currentSequence
                        )
                );
            }
        }

        return savedTelemetry;
    }

}
