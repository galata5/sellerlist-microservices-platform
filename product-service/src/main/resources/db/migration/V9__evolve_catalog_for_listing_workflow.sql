-- Forward-only evolution: existing Flyway environments must retain their recorded migrations.
ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS is_visible TINYINT(1) NOT NULL DEFAULT 1;

ALTER TABLE categories
    MODIFY COLUMN category_title VARCHAR(120) NOT NULL;

ALTER TABLE products
    ADD COLUMN IF NOT EXISTS description VARCHAR(1200) NULL,
    ADD COLUMN IF NOT EXISTS listing_status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE products
    MODIFY COLUMN product_title VARCHAR(180) NOT NULL,
    MODIFY COLUMN image_url VARCHAR(500) NULL,
    MODIFY COLUMN sku VARCHAR(80) NOT NULL,
    MODIFY COLUMN price_unit DECIMAL(12, 2) NOT NULL,
    MODIFY COLUMN quantity INT NOT NULL;

CREATE UNIQUE INDEX ux_products_sku ON products (sku);
