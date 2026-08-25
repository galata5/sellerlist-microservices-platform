ALTER TABLE payments
  ADD COLUMN user_id INT(11) NULL;

ALTER TABLE payments
  ADD INDEX idx_payments_user_id (user_id);
