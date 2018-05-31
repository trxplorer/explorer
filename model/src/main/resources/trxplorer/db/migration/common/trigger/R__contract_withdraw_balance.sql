DELIMITER $$

DROP TRIGGER IF EXISTS contract_withdraw_balance_AFTER_INSERT$$

CREATE DEFINER = CURRENT_USER TRIGGER `contract_withdraw_balance_AFTER_INSERT` AFTER INSERT ON `contract_withdraw_balance` FOR EACH ROW
BEGIN
insert ignore into sync_account(address,date_created,origin) values(new.owner_address,now(),'contract_withdraw_balance');
END$$

DELIMITER ;
