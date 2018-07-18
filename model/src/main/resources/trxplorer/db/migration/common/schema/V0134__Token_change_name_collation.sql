ALTER TABLE `contract_asset_issue` 
CHANGE COLUMN `name` `name` VARCHAR(45) CHARACTER SET 'utf8'  COLLATE 'utf8_bin' NOT NULL ;
ALTER TABLE `account_asset` 
CHANGE COLUMN `asset_name` `asset_name` VARCHAR(45) CHARACTER SET 'utf8'  COLLATE 'utf8_bin' NOT NULL ;
ALTER TABLE `contract_participate_asset_issue` 
CHANGE COLUMN `asset_name` `asset_name` VARCHAR(45) CHARACTER SET 'utf8'  COLLATE 'utf8_bin' NOT NULL ;
ALTER TABLE `contract_transfer_asset` 
CHANGE COLUMN `asset_name` `asset_name` VARCHAR(45) CHARACTER SET 'utf8'  COLLATE 'utf8_bin' NOT NULL ;
ALTER TABLE `transfer` 
CHANGE COLUMN `token` `token` VARCHAR(45) CHARACTER SET 'utf8'  COLLATE 'utf8_bin' NULL ;