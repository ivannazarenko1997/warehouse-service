-- Explicit index for primary key (optional, PostgreSQL auto creates one)
CREATE INDEX IF NOT EXISTS idx_sensor_id_pk
    ON sensor (id);

-- Index for created_at (useful for queries, ordering, pagination)
CREATE INDEX IF NOT EXISTS idx_sensor_created_at
    ON sensor (created_at);