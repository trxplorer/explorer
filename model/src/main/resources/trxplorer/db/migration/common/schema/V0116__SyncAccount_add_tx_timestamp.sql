ALTER TABLE `sync_account` 
ADD COLUMN `tx_timestamp` DATETIME AFTER `origin`;
