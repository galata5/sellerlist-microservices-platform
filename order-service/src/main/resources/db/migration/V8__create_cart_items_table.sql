CREATE TABLE cart_items (
	cart_item_id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
	cart_id INT(11) NOT NULL,
	product_id INT(11) NOT NULL,
	product_title VARCHAR(255) NOT NULL,
	sku VARCHAR(255),
	image_url VARCHAR(500),
	category_id INT(11),
	category_title VARCHAR(255),
	price_unit DECIMAL(10, 2) NOT NULL,
	quantity INT(11) NOT NULL,
	created_at TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL,
	updated_at TIMESTAMP,
	CONSTRAINT fk_cart_items_cart_id FOREIGN KEY (cart_id) REFERENCES carts (cart_id) ON DELETE CASCADE,
	CONSTRAINT uq_cart_items_cart_product UNIQUE (cart_id, product_id)
);

CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
