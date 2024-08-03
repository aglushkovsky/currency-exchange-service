INSERT INTO currencies(code, full_name, sign)
VALUES
    ('USD', 'United States Dollar', '$'),
    ('EUR', 'Euro', '€'),
    ('JPY', 'Japanese Yen', '¥'),
    ('GBP', 'British Pound Sterling', '£'),
    ('AUD', 'Australian Dollar', 'A$'),
    ('CAD', 'Canadian Dollar', 'C$'),
    ('CHF', 'Swiss Franc', 'CHF'),
    ('CNY', 'Chinese Yuan', '¥'),
    ('SEK', 'Swedish Krona', 'kr'),
    ('NZD', 'New Zealand Dollar', 'NZ$');

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
VALUES
    (1, 2, 0.85), -- USD to EUR
    (1, 3, 110.50), -- USD to JPY
    (1, 4, 0.75), -- USD to GBP
    (1, 5, 1.35), -- USD to AUD
    (1, 6, 1.25), -- USD to CAD
    (1, 7, 0.92), -- USD to CHF
    (1, 8, 6.47), -- USD to CNY
    (1, 9, 8.62), -- USD to SEK
    (1, 10, 1.45), -- USD to NZD

    (2, 1, 1.18), -- EUR to USD
    (2, 3, 130.00), -- EUR to JPY
    (2, 4, 0.88), -- EUR to GBP
    (2, 5, 1.58), -- EUR to AUD
    (2, 6, 1.47), -- EUR to CAD
    (2, 7, 1.08), -- EUR to CHF
    (2, 8, 7.60), -- EUR to CNY
    (2, 9, 10.13), -- EUR to SEK
    (2, 10, 1.71); -- EUR to NZD