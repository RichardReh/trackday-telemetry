
CREATE EXTENSION IF NOT EXISTS timescaledb;

SELECT create_hypertable(
               'public.telemetry',
               'timestamp',
               migrate_data => TRUE
       );

CREATE INDEX IF NOT EXISTS idx_telemetry_vehicle_timestamp
    ON public.telemetry (vehicle_id, timestamp DESC);
