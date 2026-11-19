package de.master.telemetry.vehicle.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateVehicleRequest(

        @NotBlank
        String name,

        @NotBlank
        String manufacturer,

        @NotBlank
        String model,

        Integer year

) {}