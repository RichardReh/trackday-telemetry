
-- Add sequence number
ALTER TABLE telemetry
    ADD COLUMN sequence_number BIGINT;

-- Backfill existing records with a sequence number per vehicle
WITH numbered_telemetry AS (
    SELECT
        id,
    timestamp,
    ROW_NUMBER() OVER (
    PARTITION BY vehicle_id
    ORDER BY timestamp ASC, id ASC
    ) AS sequence_number
FROM telemetry
    )
UPDATE telemetry t
SET sequence_number = nt.sequence_number
    FROM numbered_telemetry nt
WHERE t.id = nt.id
  AND t.timestamp = nt.timestamp;

-- Sequence number is required for all new records
ALTER TABLE telemetry
    ALTER COLUMN sequence_number SET NOT NULL;


-- Add optional engine / OBD-II fields
ALTER TABLE telemetry
    ADD COLUMN coolant_temperature DOUBLE PRECISION,
ADD COLUMN oil_temperature DOUBLE PRECISION,
ADD COLUMN throttle_position DOUBLE PRECISION,
ADD COLUMN engine_load DOUBLE PRECISION,
ADD COLUMN intake_air_temperature DOUBLE PRECISION,
ADD COLUMN fuel_level DOUBLE PRECISION,
ADD COLUMN gear INTEGER;


-- Add database-level validation constraints
ALTER TABLE telemetry
    ADD CONSTRAINT chk_telemetry_rpm_nonnegative
        CHECK (rpm >= 0),

ADD CONSTRAINT chk_telemetry_speed_nonnegative
    CHECK (speed >= 0),

ADD CONSTRAINT chk_telemetry_gps_pair
    CHECK (
        (latitude IS NULL AND longitude IS NULL)
        OR
        (latitude IS NOT NULL AND longitude IS NOT NULL)
    ),

ADD CONSTRAINT chk_telemetry_latitude
    CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90),

ADD CONSTRAINT chk_telemetry_longitude
    CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180),

ADD CONSTRAINT chk_telemetry_throttle_position
    CHECK (throttle_position IS NULL OR throttle_position BETWEEN 0 AND 100),

ADD CONSTRAINT chk_telemetry_engine_load
    CHECK (engine_load IS NULL OR engine_load BETWEEN 0 AND 100),

ADD CONSTRAINT chk_telemetry_fuel_level
    CHECK (fuel_level IS NULL OR fuel_level BETWEEN 0 AND 100);