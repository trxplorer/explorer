ALTER TABLE`contract_participate_asset_issue` 
DROP FOREIGN KEY `fk_contract_participate_asset_issue_owner_id`,
DROP FOREIGN KEY `fk_contract_participate_asset_issue_to_id`;
ALTER TABLE`contract_participate_asset_issue` 
CHANGE COLUMN `owner_id` `owner_id` BIGINT(20) UNSIGNED NULL ,
CHANGE COLUMN `to_id` `to_id` BIGINT(20) UNSIGNED NULL ,
ADD COLUMN `owner_address` VARCHAR(45) NOT NULL AFTER `owner_id`,
ADD COLUMN `to_address` VARCHAR(45) NOT NULL AFTER `to_id`;
