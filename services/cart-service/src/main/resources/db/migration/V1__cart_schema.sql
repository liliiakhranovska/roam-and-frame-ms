CREATE TABLE cart_items (
                            id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                            customer_id uuid        NOT NULL,
                            sku         varchar(64) NOT NULL,
                            quantity    integer     NOT NULL CHECK (quantity > 0),
                            created_at  timestamptz NOT NULL DEFAULT now(),
                            UNIQUE (customer_id, sku)
);