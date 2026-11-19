package de.master.telemetry.telemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;


public interface TelemetryRepository
        extends JpaRepository<Telemetry, TelemetryId> {

}
