ALTER TABLE `transaction` 
CHANGE COLUMN `type` `transaction_type` TINYINT(2) NOT NULL ,
ADD COLUMN `contract_type` TINYINT(2) NOT NULL AFTER `transaction_type`;
