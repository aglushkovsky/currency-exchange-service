CREATE TABLE IF NOT EXISTS currencies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code VARCHAR NOT NULL UNIQUE,
    full_name VARCHAR NOT NULL,
    sign VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS exchange_rates (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    base_currency_id INT NOT NULL REFERENCES currencies(id),
    target_currency_id INT NOT NULL REFERENCES currencies(id),
    rate DECIMAL(6) NOT NULL,
    UNIQUE(base_currency_id, target_currency_id)
);