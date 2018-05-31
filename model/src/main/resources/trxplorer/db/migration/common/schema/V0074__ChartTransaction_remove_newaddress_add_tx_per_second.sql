ALTER TABLE `chart_transaction` 
DROP COLUMN `new_address`,
ADD COLUMN `tx_per_second` INT UNSIGNED NOT NULL AFTER `total_tx`;
