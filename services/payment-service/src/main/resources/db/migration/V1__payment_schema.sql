CREATE TABLE payments (
                          id          uuid PRIMARY KEY,
                          order_id    uuid           NOT NULL UNIQUE,
                          customer_id uuid           NOT NULL,
                          amount      numeric(12, 2) NOT NULL CHECK (amount >= 0),
                          status      varchar(20)    NOT NULL CHECK (status IN ('AUTHORIZED', 'VOIDED')),
                          reference   varchar(100)   NOT NULL,
                          created_at  timestamptz    NOT NULL DEFAULT now()
);