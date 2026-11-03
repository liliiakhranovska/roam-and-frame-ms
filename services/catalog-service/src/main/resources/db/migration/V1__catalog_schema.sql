CREATE TABLE categories (
                            id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                            code        varchar(100) NOT NULL UNIQUE,
                            name        varchar(150) NOT NULL,
                            description text
);

CREATE TABLE products (
                          id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                          sku         varchar(64)  NOT NULL UNIQUE,
                          name        varchar(255) NOT NULL,
                          description text,
                          category_id uuid NOT NULL REFERENCES categories (id),
                          brand       varchar(100),
                          price       numeric(10, 2) NOT NULL CHECK (price >= 0),
                          attributes  jsonb NOT NULL DEFAULT '{}',
                          active      boolean NOT NULL DEFAULT true,
                          created_at  timestamptz NOT NULL DEFAULT now(),
                          updated_at  timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX products_category_id_idx ON products (category_id);