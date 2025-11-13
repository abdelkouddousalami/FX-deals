-- Sample data for testing FX Deals Warehouse
-- This script will be executed when the MySQL container starts

-- Use the database
USE fxdeals_db;

-- Create table if not exists (Hibernate will handle this, but included for reference)
CREATE TABLE IF NOT EXISTS fx_deals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    deal_unique_id VARCHAR(100) NOT NULL UNIQUE,
    from_currency_iso_code VARCHAR(3) NOT NULL,
    to_currency_iso_code VARCHAR(3) NOT NULL,
    deal_timestamp TIMESTAMP NOT NULL,
    deal_amount DECIMAL(19, 4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_deal_unique_id (deal_unique_id),
    INDEX idx_deal_timestamp (deal_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample FX deals
INSERT INTO fx_deals (deal_unique_id, from_currency_iso_code, to_currency_iso_code, deal_timestamp, deal_amount, created_at)
VALUES 
    ('SAMPLE-001', 'USD', 'EUR', '2024-01-15 10:30:00', 10000.0000, CURRENT_TIMESTAMP),
    ('SAMPLE-002', 'GBP', 'USD', '2024-01-15 11:45:00', 5000.0000, CURRENT_TIMESTAMP),
    ('SAMPLE-003', 'EUR', 'JPY', '2024-01-15 14:20:00', 7500.5000, CURRENT_TIMESTAMP),
    ('SAMPLE-004', 'USD', 'CAD', '2024-01-16 09:15:00', 12000.0000, CURRENT_TIMESTAMP),
    ('SAMPLE-005', 'JPY', 'USD', '2024-01-16 13:30:00', 1000000.0000, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE deal_unique_id=deal_unique_id;

-- Output confirmation
SELECT 'Sample data inserted successfully' AS status;
