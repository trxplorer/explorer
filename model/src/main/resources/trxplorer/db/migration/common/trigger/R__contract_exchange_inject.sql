DELIMITER $$

DROP TRIGGER IF EXISTS contract_exchange_inject_AFTER_INSERT$$

CREATE DEFINER = CURRENT_USER TRIGGER `contract_exchange_inject_AFTER_INSERT` AFTER INSERT ON `contract_exchange_inject` FOR EACH ROW
BEGIN
insert ignore into sync_account(address,date_created,origin,tx_timestamp) values(new.owner_address,now(),'contract_exchange_inject',(select block.timestamp from transaction,block where block.id=transaction.block_id and transaction.id=new.transaction_id));
update `transaction` SET `from`=new.owner_address, `type`=42 where `id`=new.transaction_id;
END$$

DELIMITER ;
