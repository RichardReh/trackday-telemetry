package de.master.telemetry.vehicle.dto;

import java.util.UUID;

public record VehicleResponse(
        UUID id,
        String name,
        String manufacturer,
        String model,
        Integer year
) {}