CREATE SEQUENCE IF NOT EXISTS offerings_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS orders_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS order_items_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS cart_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS users_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS offerings
(
    id bigint NOT NULL DEFAULT nextval('offerings_id_seq'::regclass),
    title character varying NOT NULL,
    description character varying NOT NULL,
    img_path character varying NOT NULL,
    price bigint NOT NULL,
    CONSTRAINT offerings_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS orders
(
    id bigint NOT NULL DEFAULT nextval('orders_id_seq'::regclass),
    created_date timestamp without time zone NOT NULL DEFAULT now(),
    total_price bigint NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS order_items
(
    id bigint NOT NULL DEFAULT nextval('order_items_id_seq'::regclass),
    order_id bigint NOT NULL,
    offering_id bigint NOT NULL,
    amount integer NOT NULL,
    unit_price bigint NOT NULL,
    CONSTRAINT order_items_pkey PRIMARY KEY (id),
    CONSTRAINT order_items_offering_id_fkey FOREIGN KEY (offering_id)
    REFERENCES offerings (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID,
    CONSTRAINT order_items_order_id_fkey FOREIGN KEY (order_id)
    REFERENCES orders (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS cart
(
    id bigint NOT NULL DEFAULT nextval('cart_id_seq'::regclass),
    offering_id bigint NOT NULL,
    amount integer NOT NULL,
    CONSTRAINT cart_pkey PRIMARY KEY (id),
    CONSTRAINT cart_offering_id_fkey FOREIGN KEY (offering_id)
    REFERENCES offerings (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS users
(
    id bigint NOT NULL DEFAULT nextval('users_id_seq'::regclass),
    username character varying COLLATE pg_catalog."default" NOT NULL,
    password character varying COLLATE pg_catalog."default" NOT NULL,
    is_admin boolean DEFAULT false,
    enabled boolean DEFAULT true,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS cart
    ADD COLUMN user_id bigint;
ALTER TABLE IF EXISTS cart
    ADD FOREIGN KEY (user_id)
    REFERENCES users (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
    NOT VALID;

ALTER TABLE IF EXISTS orders
    ADD COLUMN user_id bigint;
ALTER TABLE IF EXISTS orders
    ADD FOREIGN KEY (user_id)
    REFERENCES users (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
    NOT VALID;