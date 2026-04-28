CREATE TABLE IF NOT EXISTS users
(
    id
    VARCHAR
(
    36
) NOT NULL,
    first_name VARCHAR
(
    100
) NOT NULL,
    last_name VARCHAR
(
    100
) NOT NULL,
    email VARCHAR
(
    255
) NOT NULL UNIQUE,
    password VARCHAR
(
    255
) NOT NULL,
    phone_number VARCHAR
(
    20
) UNIQUE,
    status VARCHAR
(
    30
) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    role VARCHAR
(
    20
) NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY
(
    id
)
    );

-- Index for fast email lookups (used during login)
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);