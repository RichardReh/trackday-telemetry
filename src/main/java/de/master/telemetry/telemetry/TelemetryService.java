
package de.master.telemetry.telemetry;

import de.master.telemetry.telemetry.dto.TelemetryMessage;
import de.master.telemetry.vehicle.VehicleRepository;
import de.master.telemetry.vehicle.VehicleNotFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;


@Service
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;
    private final VehicleRepository vehicleRepository;
    private final Validator validator;

    public TelemetryService(
            TelemetryRepository telemetryRepository,
            VehicleRepository vehicleRepository,
            Validator validator) {

        this.telemetryRepository = telemetryRepository;
        this.vehicleRepository = vehicleRepository;
        this.validator = validator;
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

        Optional<Telemetry> lastTelemetry =
                telemetryRepository.findTopByVehicleIdOrderBySequenceNumberDesc(
                        message.vehicleId()
                );

        if (lastTelemetry.isPresent()) {
            long lastSequenceNumber = lastTelemetry.get().getSequenceNumber();

            if (message.sequenceNumber() <= lastSequenceNumber) {
                throw new IllegalArgumentException(
                        "Duplicate or outdated telemetry message. Received sequence number: "
                                + message.sequenceNumber()
                                + ", last sequence number: "
                                + lastSequenceNumber
                );
            }

            if (message.sequenceNumber() > lastSequenceNumber + 1) {
                log.warn(
                        "Telemetry sequence gap for vehicle {}. Expected: {}, received: {}",
                        message.vehicleId(),
                        lastSequenceNumber + 1,
                        message.sequenceNumber()
                );
            }
        }

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
        return telemetryRepository.save(telemetry);
    }
}
