-- 建立資料庫（如果不存在）
CREATE DATABASE IF NOT EXISTS exchange_rate_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE exchange_rate_db;

-- 建立匯率資料表
CREATE TABLE IF NOT EXISTS exchange_rates (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              currency VARCHAR(10) NOT NULL,
    rate DECIMAL(10, 4) NOT NULL,
    date DATE NOT NULL,
    created_at DATE,
    INDEX idx_currency (currency),
    INDEX idx_date (date),
    INDEX idx_currency_date (currency, date),
    UNIQUE KEY uk_currency_date (currency, date)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
