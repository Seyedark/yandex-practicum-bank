CREATE TABLE exchange (
    id BIGSERIAL PRIMARY KEY,
    currency VARCHAR(255) NOT NULL,
    purchase_rate DECIMAL(15, 2) NOT NULL,
    selling_rate DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);