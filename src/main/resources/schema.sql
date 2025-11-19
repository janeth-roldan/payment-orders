-- Payment Orders Table
CREATE TABLE IF NOT EXISTS payment_orders (
    id UUID PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    payment_transaction_initiator_reference VARCHAR(50) NOT NULL,
    
    -- Payer fields
    payer_reference VARCHAR(100),
    payer_bank_reference VARCHAR(50),
    payer_product_instance_reference VARCHAR(34) NOT NULL,
    
    -- Payee fields
    payee_reference VARCHAR(100),
    payee_bank_reference VARCHAR(50),
    payee_product_instance_reference VARCHAR(34) NOT NULL,
    
    -- Payment Details fields
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_mechanism_type VARCHAR(50),
    
    -- Date Information fields
    date_type VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    
    remittance_information VARCHAR(140),
    created_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    last_update_date_time TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_payment_orders_status ON payment_orders(status);
CREATE INDEX IF NOT EXISTS idx_payment_orders_created ON payment_orders(created_date_time);
CREATE INDEX IF NOT EXISTS idx_payment_orders_payer_iban ON payment_orders(payer_product_instance_reference);
CREATE INDEX IF NOT EXISTS idx_payment_orders_payee_iban ON payment_orders(payee_product_instance_reference);
