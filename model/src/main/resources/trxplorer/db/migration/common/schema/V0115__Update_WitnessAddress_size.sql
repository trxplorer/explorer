ALTER TABLE `block` 
CHANGE COLUMN `witness_address` `witness_address` VARCHAR(164) NOT NULL ;

ALTER TABLE `sync_account` 
CHANGE COLUMN `address` `address` VARCHAR(164) NOT NULL ;
