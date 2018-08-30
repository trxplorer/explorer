DELIMITER $$

DROP TRIGGER IF EXISTS contract_buy_storage_bytes_AFTER_INSERT$$

CREATE DEFINER = CURRENT_USER TRIGGER `contract_buy_storage_bytes_AFTER_INSERT` AFTER INSERT ON `contract_buy_storage_bytes` FOR EACH ROW
BEGIN
insert ignore into sync_account(address,date_created,origin,tx_timestamp) values(new.owner_address,now(),'contract_buy_storage_bytes',(select block.timestamp from transaction,block where block.id=transaction.block_id and transaction.id=new.transaction_id));
update `transaction` SET `from`=new.owner_address, `type`=22 where `id`=new.transaction_id;
END$$

DELIMITER ;
