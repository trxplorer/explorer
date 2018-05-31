ALTER TABLE `contract_asset_issue` 
CHANGE COLUMN `total_supply` `total_supply` BIGINT UNSIGNED NOT NULL ,
CHANGE COLUMN `trx_num` `trx_num` INT(11) UNSIGNED NOT NULL ,
ADD COLUMN `num` INT UNSIGNED NOT NULL AFTER `trx_num`;
