CREATE TABLE sensor (
                        id BIGSERIAL PRIMARY KEY,

                        sensor_id VARCHAR(100) NOT NULL UNIQUE,
                        sensor_type VARCHAR(100) NOT NULL,
                        value DOUBLE PRECISION NOT NULL,
                        description VARCHAR(255),

                        created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
