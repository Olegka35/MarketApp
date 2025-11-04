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
    order_id bigint NOT NULL,
    offering_id bigint NOT NULL,
    amount integer NOT NULL,
    unit_price bigint NOT NULL,
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
    offering_id bigint NOT NULL,
    amount integer NOT NULL,
    CONSTRAINT cart_pkey PRIMARY KEY (offering_id),
    CONSTRAINT cart_offering_id_fkey FOREIGN KEY (offering_id)
    REFERENCES offerings (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);

INSERT INTO offerings (title, description, img_path, price) VALUES
    ('Термокружка 500 мл', 'Удобная металлическая термокружка, сохраняет тепло до 6 часов', '/thermocup.jpg', 1290),
    ('Беспроводная мышь', 'Эргономичная мышь с Bluetooth-подключением и регулируемым DPI', '/wireless_mouse.jpg', 990),
    ('Рюкзак городской', 'Лёгкий водоотталкивающий рюкзак с отделением для ноутбука 15.6"', '/backpack.jpg', 2790),
    ('Настольная лампа', 'Светодиодная лампа с регулировкой яркости и тёплого/холодного света', '/desk_lamp.jpg', 1590),
    ('Зонт складной', 'Компактный автоматический зонт, устойчивый к ветру', '/umbrella.jpg', 1190);
