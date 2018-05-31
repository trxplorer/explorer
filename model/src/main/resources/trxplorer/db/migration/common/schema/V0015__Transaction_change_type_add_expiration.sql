ALTER TABLE `transaction` 
DROP COLUMN `transaction_type`,
CHANGE COLUMN `contract_type` `type` TINYINT(2) UNSIGNED NOT NULL ,
ADD COLUMN `expiration` DATETIME NULL AFTER `timestamp`;
