package de.master.telemetry.telemetry;

import de.master.telemetry.telemetry.dto.TelemetryMessage;
import de.master.telemetry.vehicle.VehicleRepository;
import de.master.telemetry.vehicle.VehicleNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;
    private final VehicleRepository vehicleRepository;

    public TelemetryService(
            TelemetryRepository telemetryRepository,
            VehicleRepository vehicleRepository) {

        this.telemetryRepository = telemetryRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public Telemetry saveTelemetry(TelemetryMessage message) {

        // Check if vehicle exists
        if (!vehicleRepository.existsById(message.vehicleId())) {
            throw new VehicleNotFoundException(message.vehicleId());
        }

        // Convert DTO into database entity
        Telemetry telemetry = new Telemetry(
                message.vehicleId(),
                message.timestamp(),
                message.rpm(),
                message.speed(),
                message.latitude(),
                message.longitude()
        );

        // Persist telemetry
        return telemetryRepository.save(telemetry);
    }
}
