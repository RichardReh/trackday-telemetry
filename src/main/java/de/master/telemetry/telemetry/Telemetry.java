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

    @Column
    private Integer rpm;

    @Column
    private Double speed;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    protected Telemetry() {
    }

    public Telemetry(UUID vehicleId, Instant timestamp,
                     Integer rpm, Double speed,
                     Double latitude, Double longitude) {

        this.id = new TelemetryId(UUID.randomUUID(), timestamp);
        this.vehicleId = vehicleId;
        this.rpm = rpm;
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public TelemetryId getId() {
        return id;
    }

    public UUID getVehicleId() {
        return vehicleId;
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
}
