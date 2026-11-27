
package de.master.telemetry.telemetry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ProcessedMessageRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProcessedMessageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean registerIfNew(UUID vehicleId, Long sequenceNumber) {

        String sql = """
                INSERT INTO processed_messages (vehicle_id, sequence_number)
                VALUES (?, ?)
                ON CONFLICT (vehicle_id, sequence_number) DO NOTHING
                """;

        int rowsAffected = jdbcTemplate.update(
                sql,
                vehicleId,
                sequenceNumber
        );

        return rowsAffected == 1;
    }
}
