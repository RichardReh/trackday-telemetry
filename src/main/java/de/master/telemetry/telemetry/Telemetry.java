
package de.master.telemetry.telemetry;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "telemetry")
public class Telemetry {

    @EmbeddedId
    private TelemetryId id;

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name = "sequence_number", nullable = false)
    private Long sequenceNumber;

    // Required telemetry values
    @Column(nullable = false)
    private Integer rpm;

    @Column(nullable = false)
    private Double speed;

    // Optional GPS values
    private Double latitude;

    private Double longitude;

    // Optional engine / OBD-II values
    @Column(name = "coolant_temperature")
    private Double coolantTemperature;

    @Column(name = "oil_temperature")
    private Double oilTemperature;

    @Column(name = "throttle_position")
    private Double throttlePosition;

    @Column(name = "engine_load")
    private Double engineLoad;

    @Column(name = "intake_air_temperature")
    private Double intakeAirTemperature;

    @Column(name = "fuel_level")
    private Double fuelLevel;

    private Integer gear;

    protected Telemetry() {
    }

    public Telemetry(
            UUID vehicleId,
            Instant timestamp,
            Long sequenceNumber,
            Integer rpm,
            Double speed,
            Double latitude,
            Double longitude,
            Double coolantTemperature,
            Double oilTemperature,
            Double throttlePosition,
            Double engineLoad,
            Double intakeAirTemperature,
            Double fuelLevel,
            Integer gear
    ) {
        this.id = new TelemetryId(UUID.randomUUID(), timestamp);
        this.vehicleId = vehicleId;
        this.sequenceNumber = sequenceNumber;
        this.rpm = rpm;
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.coolantTemperature = coolantTemperature;
        this.oilTemperature = oilTemperature;
        this.throttlePosition = throttlePosition;
        this.engineLoad = engineLoad;
        this.intakeAirTemperature = intakeAirTemperature;
        this.fuelLevel = fuelLevel;
        this.gear = gear;
    }

    public TelemetryId getId() {
        return id;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public Integer getRpm() {
        return rpm;
    }

    public Double getSpeed() {
        return speed;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getCoolantTemperature() {
        return coolantTemperature;
    }

    public Double getOilTemperature() {
        return oilTemperature;
    }

    public Double getThrottlePosition() {
        return throttlePosition;
    }

    public Double getEngineLoad() {
        return engineLoad;
    }

    public Double getIntakeAirTemperature() {
        return intakeAirTemperature;
    }

    public Double getFuelLevel() {
        return fuelLevel;
    }

    public Integer getGear() {
        return gear;
    }
}
