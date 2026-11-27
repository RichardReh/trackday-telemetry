
package de.master.telemetry.telemetry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TelemetrySequenceGapRepository {

    private final JdbcTemplate jdbcTemplate;

    public TelemetrySequenceGapRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Gap> findOpenGapContaining(
            UUID vehicleId,
            long sequenceNumber) {

        String sql = """
            SELECT id, missing_from, missing_to, detected_at, expires_at
            FROM telemetry_sequence_gaps
            WHERE vehicle_id = ?
              AND status = 'OPEN'
              AND missing_from <= ?
              AND missing_to >= ?
            FOR UPDATE
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapGap(rs),
                vehicleId,
                sequenceNumber,
                sequenceNumber
        ).stream().findFirst();
    }

    private Gap mapGap(ResultSet rs) throws SQLException {
        return new Gap(
                rs.getLong("id"),
                rs.getLong("missing_from"),
                rs.getLong("missing_to"),
                rs.getTimestamp("detected_at").toInstant(),
                rs.getTimestamp("expires_at").toInstant()
        );
    }

    public record Gap(
            long id,
            long missingFrom,
            long missingTo,
            Instant detectedAt,
            Instant expiresAt
    ) {}


    public void createGap(
            UUID vehicleId,
            long missingFrom,
            long missingTo,
            Instant expiresAt) {

        String sql = """
        INSERT INTO telemetry_sequence_gaps (
            vehicle_id,
            missing_from,
            missing_to,
            detected_at,
            expires_at,
            status
        )
        VALUES (?, ?, ?, NOW(), ?, 'OPEN')
        """;

        jdbcTemplate.update(
                sql,
                vehicleId,
                missingFrom,
                missingTo,
                Timestamp.from(expiresAt)
        );
    }


    public void resolveSequenceNumber(Gap gap, long sequenceNumber) {

        // Case 1: The gap contains only one missing sequence number
        if (gap.missingFrom() == gap.missingTo()) {

            String sql = """
            UPDATE telemetry_sequence_gaps
            SET status = 'CLOSED',
                resolved_at = NOW()
            WHERE id = ?
            """;

            jdbcTemplate.update(sql, gap.id());
            return;
        }

        // Case 2: The first missing sequence number arrived
        if (sequenceNumber == gap.missingFrom()) {

            String sql = """
            UPDATE telemetry_sequence_gaps
            SET missing_from = ?
            WHERE id = ?
            """;

            jdbcTemplate.update(
                    sql,
                    sequenceNumber + 1,
                    gap.id()
            );
            return;
        }

        // Case 3: The last missing sequence number arrived
        if (sequenceNumber == gap.missingTo()) {

            String sql = """
            UPDATE telemetry_sequence_gaps
            SET missing_to = ?
            WHERE id = ?
            """;

            jdbcTemplate.update(
                    sql,
                    sequenceNumber - 1,
                    gap.id()
            );
            return;
        }

        // Case 4: The received sequence number is inside the gap
        // Split the original gap into two smaller gaps

        String updateSql = """
        UPDATE telemetry_sequence_gaps
        SET missing_to = ?
        WHERE id = ?
        """;

        jdbcTemplate.update(
                updateSql,
                sequenceNumber - 1,
                gap.id()
        );

        String insertSql = """
        INSERT INTO telemetry_sequence_gaps (
            vehicle_id,
            missing_from,
            missing_to,
            detected_at,
            expires_at,
            status
        )
        SELECT
            vehicle_id,
            ?,
            ?,
            detected_at,
            expires_at,
            'OPEN'
        FROM telemetry_sequence_gaps
        WHERE id = ?
        """;

        jdbcTemplate.update(
                insertSql,
                sequenceNumber + 1,
                gap.missingTo(),
                gap.id()
        );
    }


    public int expireOldGaps() {

        String sql = """
        UPDATE telemetry_sequence_gaps
        SET status = 'EXPIRED',
            resolved_at = NOW()
        WHERE status = 'OPEN'
          AND expires_at <= NOW()
        """;

        return jdbcTemplate.update(sql);
    }

}
