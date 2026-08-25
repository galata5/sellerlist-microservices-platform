UPDATE products
SET product_title = 'Asus ZenBook 14',
    image_url = 'https://images.unsplash.com/photo-1517336714739-489689fd1ca8?auto=format&fit=crop&w=1200&q=80',
    sku = 'ASUS-ZEN14',
    price_unit = 899.00,
    quantity = 18
WHERE product_id = 1;

UPDATE products
SET product_title = 'HP Pavilion Plus',
    image_url = 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=1200&q=80',
    sku = 'HP-PAV-PLUS',
    price_unit = 799.00,
    quantity = 14
WHERE product_id = 2;

UPDATE products
SET product_title = 'Armani Leather Jacket',
    image_url = 'https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=1200&q=80',
    sku = 'ARMANI-JACKET',
    price_unit = 349.00,
    quantity = 11
WHERE product_id = 3;

UPDATE products
SET product_title = 'Grand Theft Auto Collection',
    image_url = 'https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&w=1200&q=80',
    sku = 'GTA-COLLECTION',
    price_unit = 59.00,
    quantity = 32
WHERE product_id = 4;

INSERT INTO products (category_id, product_title, image_url, sku, price_unit, quantity)
SELECT 1, 'Lenovo Legion 5', 'https://images.unsplash.com/photo-1593642702821-c8da6771f0c6?auto=format&fit=crop&w=1200&q=80', 'LENOVO-LEGION5', 1129.00, 9
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'LENOVO-LEGION5');

INSERT INTO products (category_id, product_title, image_url, sku, price_unit, quantity)
SELECT 1, 'Apple MacBook Air M3', 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=1200&q=80', 'APPLE-MBA-M3', 1299.00, 12
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'APPLE-MBA-M3');

INSERT INTO products (category_id, product_title, image_url, sku, price_unit, quantity)
SELECT 2, 'Nike Air Max Pulse', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=1200&q=80', 'NIKE-AIR-PULSE', 149.00, 27
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'NIKE-AIR-PULSE');

INSERT INTO products (category_id, product_title, image_url, sku, price_unit, quantity)
SELECT 2, 'Classic Leather Tote', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1200&q=80', 'CLASSIC-TOTE', 179.00, 20
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CLASSIC-TOTE');

INSERT INTO products (category_id, product_title, image_url, sku, price_unit, quantity)
SELECT 2, 'Botanical Skin Ritual', 'https://images.unsplash.com/photo-1526045478516-99145907023c?auto=format&fit=crop&w=1200&q=80', 'BOTANICAL-RITUAL', 48.00, 36
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'BOTANICAL-RITUAL');

INSERT INTO products (category_id, product_title, image_url, sku, price_unit, quantity)
SELECT 3, 'PlayStation 5 Slim', 'https://images.unsplash.com/photo-1606813907291-d86efa9b94db?auto=format&fit=crop&w=1200&q=80', 'PS5-SLIM', 499.00, 8
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'PS5-SLIM');

INSERT INTO products (category_id, product_title, image_url, sku, price_unit, quantity)
SELECT 3, 'Nintendo Switch OLED', 'https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?auto=format&fit=crop&w=1200&q=80', 'SWITCH-OLED', 349.00, 17
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SWITCH-OLED');
