package de.master.telemetry.telemetry;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Embeddable
public class TelemetryId implements Serializable {

    private UUID id;
    private Instant timestamp;

    protected TelemetryId() {
    }

    public TelemetryId(UUID id, Instant timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TelemetryId that)) return false;

        return id.equals(that.id)
                && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, timestamp);
    }
}
