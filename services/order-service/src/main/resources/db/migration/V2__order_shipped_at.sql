ALTER TABLE orders ADD COLUMN shipped_at timestamptz;

-- Orders shipped before this migration have no real timestamp; created_at is the closest approximation.
UPDATE orders SET shipped_at = created_at WHERE status = 'SHIPPED';

CREATE INDEX orders_customer_shipped_idx ON orders (customer_id, shipped_at);