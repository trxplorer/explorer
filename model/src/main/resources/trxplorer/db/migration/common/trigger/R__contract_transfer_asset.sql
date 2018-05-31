DELIMITER $$

DROP TRIGGER IF EXISTS contract_transfer_asset_AFTER_INSERT$$

CREATE DEFINER = CURRENT_USER TRIGGER `contract_transfer_asset_AFTER_INSERT` AFTER INSERT ON `contract_transfer_asset` FOR EACH ROW
BEGIN
insert ignore into sync_account(address,date_created,origin) values(new.owner_address,now(),'contract_transfer_asset'),(new.to_address,now(),'contract_transfer_asset');
END$$

DELIMITER ;
