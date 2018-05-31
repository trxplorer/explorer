ALTER TABLE `contract_account_create` 
DROP FOREIGN KEY `fk_contract_account_create_owner_id`,
DROP FOREIGN KEY `fk_contract_account_create_tx_id`;
ALTER TABLE `contract_account_create` 
CHANGE COLUMN `owner_id` `owner_id` BIGINT(20) UNSIGNED NULL ,
CHANGE COLUMN `transaction_id` `transaction_id` BIGINT(20) UNSIGNED NULL ,
ADD COLUMN `owner_address` VARCHAR(45) NOT NULL AFTER `transaction_id`;
ALTER TABLE `contract_account_create` 
ADD CONSTRAINT `fk_contract_account_create_owner_id`
  FOREIGN KEY (`owner_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_contract_account_create_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
