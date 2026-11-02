CREATE TABLE stock_items (
                             sku      varchar(64) PRIMARY KEY,
                             quantity integer NOT NULL CHECK (quantity >= 0)
);

CREATE TABLE reservations (
                              id       uuid PRIMARY KEY,
                              order_id uuid        NOT NULL,
                              sku      varchar(64) NOT NULL REFERENCES stock_items (sku),
                              quantity integer     NOT NULL CHECK (quantity > 0),
                              status   varchar(16) NOT NULL CHECK (status IN ('RESERVED', 'RELEASED')),
                              UNIQUE (order_id, sku)
);