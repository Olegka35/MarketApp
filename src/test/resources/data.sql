INSERT INTO offerings (title, description, img_path, price) VALUES
                                                                ('Термокружка 500 мл', 'Удобная металлическая термокружка, сохраняет тепло до 6 часов', '/thermocup.jpg', 1290),
                                                                ('Беспроводная мышь', 'Эргономичная мышь с Bluetooth-подключением и регулируемым DPI', '/wireless_mouse.jpg', 990),
                                                                ('Рюкзак городской', 'Лёгкий водоотталкивающий рюкзак с отделением для ноутбука 15.6"', '/backpack.jpg', 2790),
                                                                ('Настольная лампа', 'Светодиодная лампа с регулировкой яркости и тёплого/холодного света', '/desk_lamp.jpg', 1590),
                                                                ('Зонт складной', 'Компактный автоматический зонт, устойчивый к ветру', '/umbrella.jpg', 1190);

INSERT INTO cart (offering_id, amount) VALUES (2, 2),
                                              (5, 1);

INSERT INTO orders (total_price) VALUES (1000);

INSERT INTO order_items (order_id, offering_id, amount, unit_price)
VALUES (1, 2, 2, 400),
       (1, 2, 1, 200);