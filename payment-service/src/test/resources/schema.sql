CREATE SEQUENCE IF NOT EXISTS accounts_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS accounts
(
    id bigint NOT NULL DEFAULT nextval('accounts_id_seq'::regclass),
    amount double precision DEFAULT 0,
    CONSTRAINT accounts_pkey PRIMARY KEY (id),
    CONSTRAINT accounts_amount_check CHECK (amount >= 0.0)
);

INSERT INTO accounts (amount) VALUES (5000);
