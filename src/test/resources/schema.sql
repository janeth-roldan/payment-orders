-- Schema para tests de integración
-- PostgreSQL 15

CREATE TABLE IF NOT EXISTS payment_orders (
    id UUID PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    payment_transaction_initiator_reference VARCHAR(255) NOT NULL,
    
    -- Payer information
    payer_reference VARCHAR(255) NOT NULL,
    payer_bank_reference VARCHAR(255) NOT NULL,
    payer_product_instance_reference VARCHAR(255) NOT NULL,
    
    -- Payee information
    payee_reference VARCHAR(255) NOT NULL,
    payee_bank_reference VARCHAR(255) NOT NULL,
    payee_product_instance_reference VARCHAR(255) NOT NULL,
    
    -- Payment details
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_mechanism_type VARCHAR(50),
    
    -- Date information
    date_type VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    
    -- Additional information
    remittance_information TEXT,
    
    -- Audit fields
    created_date_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_update_date_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster lookups
CREATE INDEX IF NOT EXISTS idx_payment_orders_status ON payment_orders(status);
CREATE INDEX IF NOT EXISTS idx_payment_orders_created ON payment_orders(created_date_time);
