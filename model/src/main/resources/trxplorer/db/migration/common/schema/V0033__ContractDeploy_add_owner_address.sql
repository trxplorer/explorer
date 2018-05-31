ALTER TABLE `contract_deploy` 
DROP FOREIGN KEY `fk_contract_deploy_owner_id`;
ALTER TABLE `contract_deploy` 
CHANGE COLUMN `owner_id` `owner_id` BIGINT(20) UNSIGNED NULL ,
ADD COLUMN `owner_address` VARCHAR(45) NOT NULL AFTER `owner_id`;
ALTER TABLE `contract_deploy` 
ADD CONSTRAINT `fk_contract_deploy_owner_id`
  FOREIGN KEY (`owner_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
