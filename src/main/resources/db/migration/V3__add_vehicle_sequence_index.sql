CREATE INDEX IF NOT EXISTS idx_telemetry_vehicle_sequence
    ON public.telemetry (vehicle_id, sequence_number DESC);