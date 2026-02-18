CREATE TABLE IF NOT EXISTS service_tax_rules (
    id              BIGSERIAL PRIMARY KEY,
    service_code    VARCHAR(10)    NOT NULL,
    municipality_code VARCHAR(10)  NOT NULL,
    description     VARCHAR(255)   NOT NULL,
    aliquot         NUMERIC(5, 2)  NOT NULL,
    min_aliquot     NUMERIC(5, 2)  NOT NULL DEFAULT 2.00,
    max_aliquot     NUMERIC(5, 2)  NOT NULL DEFAULT 5.00,
    active          BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_service_municipality UNIQUE (service_code, municipality_code),
    CONSTRAINT chk_aliquot_range CHECK (aliquot >= 0 AND aliquot <= 100),
    CONSTRAINT chk_min_aliquot CHECK (min_aliquot >= 0),
    CONSTRAINT chk_max_aliquot CHECK (max_aliquot <= 100)
);

CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER',
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_rule_lookup ON service_tax_rules (municipality_code, service_code, active);
