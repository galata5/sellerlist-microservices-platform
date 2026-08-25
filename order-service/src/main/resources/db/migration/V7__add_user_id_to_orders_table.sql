ALTER TABLE orders
  ADD COLUMN user_id INT(11) NULL;

UPDATE orders o
LEFT JOIN carts c ON c.cart_id = o.cart_id
SET o.user_id = c.user_id
WHERE o.user_id IS NULL;

ALTER TABLE orders
  ADD INDEX idx_orders_user_id (user_id);
