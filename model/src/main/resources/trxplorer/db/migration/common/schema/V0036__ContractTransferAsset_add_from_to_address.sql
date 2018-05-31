ALTER TABLE `contract_transfer_asset` 
DROP FOREIGN KEY `fk_contract_transfer_asset_owner_id`,
DROP FOREIGN KEY `fk_contract_transfer_asset_to_id`;
ALTER TABLE `contract_transfer_asset` 
CHANGE COLUMN `owner_id` `owner_id` BIGINT(20) UNSIGNED NULL ,
CHANGE COLUMN `to_id` `to_id` BIGINT(20) UNSIGNED NULL ,
ADD COLUMN `owner_address` VARCHAR(45) NOT NULL AFTER `to_id`,
ADD COLUMN `to_address` VARCHAR(45) NOT NULL AFTER `owner_address`;
ALTER TABLE `contract_transfer_asset` 
ADD CONSTRAINT `fk_contract_transfer_asset_owner_id`
  FOREIGN KEY (`owner_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_contract_transfer_asset_to_id`
  FOREIGN KEY (`to_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
