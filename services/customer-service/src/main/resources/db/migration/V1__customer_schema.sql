CREATE TABLE customers (
                           id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                           email           varchar(255) NOT NULL UNIQUE,
                           password_hash   varchar(255) NOT NULL,
                           first_name      varchar(100) NOT NULL,
                           last_name       varchar(100) NOT NULL,
                           phone           varchar(30),
                           address_line1   varchar(255),
                           address_line2   varchar(255),
                           city            varchar(100),
                           region          varchar(100),
                           postal_code     varchar(20),
                           country_code    varchar(2),
                           role            varchar(20) NOT NULL DEFAULT 'CUSTOMER' CHECK (role IN ('CUSTOMER', 'ADMIN')),
                           created_at      timestamptz NOT NULL DEFAULT now(),
                           updated_at      timestamptz NOT NULL DEFAULT now()
);