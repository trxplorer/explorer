ALTER TABLE `contract_vote_asset` 
DROP FOREIGN KEY `fk_contract_vote_asset_owner_id`;
ALTER TABLE `contract_vote_asset` 
CHANGE COLUMN `transaction_id` `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_id`,
CHANGE COLUMN `owner_id` `owner_id` BIGINT(20) UNSIGNED NULL ,
ADD COLUMN `owner_address` VARCHAR(45) NOT NULL AFTER `count`;
ALTER TABLE `contract_vote_asset` 
ADD CONSTRAINT `fk_contract_vote_asset_owner_id`
  FOREIGN KEY (`owner_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
