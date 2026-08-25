
CREATE TABLE favourites (
	user_id INT(11) NOT NULL,
	product_id INT(11) NOT NULL,
	like_date TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL,
	created_at TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL,
	updated_at TIMESTAMP,
	PRIMARY KEY (user_id, product_id, like_date)
);
