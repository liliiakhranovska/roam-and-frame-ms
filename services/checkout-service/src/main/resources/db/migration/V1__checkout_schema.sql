CREATE TABLE checkouts (
                           id             uuid PRIMARY KEY,
                           customer_id    uuid        NOT NULL,
                           status         varchar(20) NOT NULL
                               CHECK (status IN ('RUNNING', 'COMPENSATING', 'FINISHING', 'COMPLETED', 'FAILED')),
                           last_step      integer     NOT NULL DEFAULT 0,
                           failure_reason varchar(500),
                           created_at     timestamptz NOT NULL DEFAULT now(),
                           updated_at     timestamptz NOT NULL DEFAULT now()
);

-- One unfinished checkout per customer; replaces the monolith's cart row lock.
CREATE UNIQUE INDEX checkouts_one_active_per_customer
    ON checkouts (customer_id)
    WHERE status IN ('RUNNING', 'COMPENSATING', 'FINISHING');

-- For the recovery job: unfinished checkouts that have not progressed lately.
CREATE INDEX checkouts_active_updated_idx
    ON checkouts (updated_at)
    WHERE status IN ('RUNNING', 'COMPENSATING', 'FINISHING');