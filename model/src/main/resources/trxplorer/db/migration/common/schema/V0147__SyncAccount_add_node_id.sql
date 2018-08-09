ALTER TABLE `sync_account` 
ADD COLUMN `node_id` INT UNSIGNED NULL AFTER `tx_timestamp`;
