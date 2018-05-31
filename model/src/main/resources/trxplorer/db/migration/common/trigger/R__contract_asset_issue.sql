DELIMITER $$

DROP TRIGGER IF EXISTS contract_asset_issue_AFTER_INSERT$$

CREATE DEFINER = CURRENT_USER TRIGGER `contract_asset_issue_AFTER_INSERT` AFTER INSERT ON `contract_asset_issue` FOR EACH ROW
BEGIN
insert ignore into sync_account(address,date_created,origin) values(new.owner_address,now(),'contract_asset_issue');
END$$

DELIMITER ;
