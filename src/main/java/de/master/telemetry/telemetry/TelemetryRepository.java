
package de.master.telemetry.telemetry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TelemetryRepository
        extends JpaRepository<Telemetry, TelemetryId> {

    Optional<Telemetry> findTopByVehicleIdOrderBySequenceNumberDesc(UUID vehicleId);

    @Query("""
        SELECT t.sequenceNumber
        FROM Telemetry t
        WHERE t.vehicleId = :vehicleId
        ORDER BY t.sequenceNumber ASC
        """)
    List<Long> findSequenceNumbersByVehicleId(
            @Param("vehicleId") UUID vehicleId
    );
}
