package de.master.telemetry.telemetry.dto;

import java.time.Instant;
import java.util.UUID;

public record TelemetryMessage(
        UUID vehicleId,
        Instant timestamp,
        Integer rpm,
        Double speed,
        Double latitude,
        Double longitude
) {}
