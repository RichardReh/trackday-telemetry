
CREATE TABLE telemetry_sequence_gaps (
                                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                                         vehicle_id UUID NOT NULL
                                             REFERENCES vehicles(id),

                                         missing_from BIGINT NOT NULL,
                                         missing_to BIGINT NOT NULL,

                                         detected_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                         expires_at TIMESTAMPTZ NOT NULL,

                                         status VARCHAR(20) NOT NULL DEFAULT 'OPEN',

                                         resolved_at TIMESTAMPTZ,

                                         CONSTRAINT chk_gap_range
                                             CHECK (missing_from > 0 AND missing_to >= missing_from),

                                         CONSTRAINT chk_gap_status
                                             CHECK (status IN ('OPEN', 'CLOSED', 'EXPIRED')),

                                         CONSTRAINT chk_gap_expiry
                                             CHECK (expires_at > detected_at)
);

CREATE INDEX idx_telemetry_gaps_vehicle_open
    ON telemetry_sequence_gaps (vehicle_id, missing_from)
    WHERE status = 'OPEN';

CREATE INDEX idx_telemetry_gaps_expiration
    ON telemetry_sequence_gaps (expires_at)
    WHERE status = 'OPEN';