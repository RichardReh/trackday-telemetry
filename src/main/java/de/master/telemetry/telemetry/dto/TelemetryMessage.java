
package de.master.telemetry.telemetry.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.AssertTrue;

import java.time.Instant;
import java.util.UUID;

public record TelemetryMessage(

        // Required fields
        @NotNull
        UUID vehicleId,

        @NotNull
        Instant timestamp,

        @NotNull
        @PositiveOrZero
        Long sequenceNumber,

        @NotNull
        @PositiveOrZero
        Integer rpm,

        @NotNull
        @PositiveOrZero
        Double speed,

        // Optional GPS fields
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Double latitude,

        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Double longitude,

        // Optional engine / OBD-II fields
        Double coolantTemperature,

        Double oilTemperature,

        @DecimalMin("0.0")
        @DecimalMax("100.0")
        Double throttlePosition,

        @DecimalMin("0.0")
        @DecimalMax("100.0")
        Double engineLoad,

        Double intakeAirTemperature,

        @DecimalMin("0.0")
        @DecimalMax("100.0")
        Double fuelLevel,

        Integer gear

) {
    @AssertTrue(message = "Latitude and longitude must either both be present or both be null")
    public boolean isGpsPairValid() {
        return (latitude == null && longitude == null)
                || (latitude != null && longitude != null);
    }
}
