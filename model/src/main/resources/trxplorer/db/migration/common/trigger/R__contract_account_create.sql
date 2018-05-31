DELIMITER $$

DROP TRIGGER IF EXISTS contract_account_create_AFTER_INSERT$$

CREATE DEFINER = CURRENT_USER TRIGGER `contract_account_create_AFTER_INSERT` AFTER INSERT ON `contract_account_create` FOR EACH ROW
BEGIN
insert ignore into sync_account(address,date_created,origin) values(new.owner_address,now(),'contract_create_account');
END$$

DELIMITER ;
