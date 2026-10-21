CREATE TABLE idempotency_records (
                                     id UUID PRIMARY KEY,
                                     idempotency_key VARCHAR(255) NOT NULL UNIQUE,
                                     order_id UUID NOT NULL,
                                     created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                     CONSTRAINT fk_idempotency_order
                                         FOREIGN KEY (order_id)
                                             REFERENCES orders(id)
);