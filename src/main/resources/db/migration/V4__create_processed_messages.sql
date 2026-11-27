CREATE TABLE processed_messages (
    vehicle_id UUID NOT NULL,
    sequence_number BIGINT NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_processed_messages
        PRIMARY KEY (vehicle_id, sequence_number),

    CONSTRAINT fk_processed_messages_vehicle
        FOREIGN KEY (vehicle_id)
            REFERENCES vehicles(id),

    CONSTRAINT chk_processed_messages_sequence
        CHECK (sequence_number >= 0)
);