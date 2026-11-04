-- Fixed customer ids: seeds of other services refer to them.
-- c0000000-0000-0000-0000-000000000001  lily.swan@gmail.com
-- c0000000-0000-0000-0000-000000000002  marco.rossi@example.com
-- c0000000-0000-0000-0000-000000000003  sofia.keller@example.com
-- c0000000-0000-0000-0000-000000000004  david.okafor@example.com
-- c0000000-0000-0000-0000-000000000005  anna.novak@example.com
-- c0000000-0000-0000-0000-000000000006  admin@roamandframe.local
-- Every customer's password is 'password'.

INSERT INTO customers (id, email, password_hash, first_name, last_name, phone,
                       address_line1, address_line2, city, region, postal_code, country_code, role)
VALUES
    ('c0000000-0000-0000-0000-000000000001', 'lily.swan@gmail.com', '$2a$10$r6QW8hSzuSziOgi9rSgjteOsbqvj0Gay3.5kMW6D2iILwvYmnADOK', 'Lily', 'Swan', NULL, '12 Nezalezhnosti', NULL, 'Odesa', 'Odesa', '00678', 'UA', 'CUSTOMER'),
    ('c0000000-0000-0000-0000-000000000002', 'marco.rossi@example.com', '$2a$10$pCDwnYpLCfDTC9LcKrx6oOjU1cLZUufpbn5QZKtjQ9evGYAg2DDVy', 'Marco', 'Rossi', '+39 02 5550 1234', 'Via Roma 10', NULL, 'Milan', 'Lombardy', '20121', 'IT', 'CUSTOMER'),
    ('c0000000-0000-0000-0000-000000000003', 'sofia.keller@example.com', '$2a$10$8VImk0t0/QjhV/2XOOQwse2hn9Edbalcbj9hguYcqmzQx/zDVX/7W', 'Sofia', 'Keller', '+49 30 5550 7788', 'Hauptstrasse 5', 'Apt 3', 'Berlin', 'Berlin', '10115', 'DE', 'CUSTOMER'),
    ('c0000000-0000-0000-0000-000000000004', 'david.okafor@example.com', '$2a$10$/kJrf3Rp0iIVQ3p1HfxFpOihDlA7oIdcFZuOwfUam5Nhg4s7zL60C', 'David', 'Okafor', '+44 20 5550 4321', '22 Baker Street', NULL, 'London', 'England', 'NW1 6XE', 'GB', 'CUSTOMER'),
    ('c0000000-0000-0000-0000-000000000005', 'anna.novak@example.com', '$2a$10$8JskcjOXs3ZGCOdTLrxY2uiHtIg5e3q9I3yJHyxJ76.JCYJpPK.b6', 'Anna', 'Novak', NULL, 'Vinohradska 88', NULL, 'Prague', 'Prague', '12000', 'CZ', 'CUSTOMER'),
    ('c0000000-0000-0000-0000-000000000006', 'admin@roamandframe.local', '$2a$10$PEsUpMZ2ns7wMjzODlqOH.BmWOwapnowAwS.Pudc//CzZsqt4jQPS', 'Ada', 'Admin', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ADMIN')
ON CONFLICT (email) DO NOTHING;
