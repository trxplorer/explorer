DELIMITER $$

DROP TRIGGER IF EXISTS contract_transfer_AFTER_INSERT$$

CREATE DEFINER = CURRENT_USER TRIGGER `contract_transfer_AFTER_INSERT` AFTER INSERT ON `contract_transfer` FOR EACH ROW
BEGIN
insert ignore into sync_account(address,date_created,origin,tx_timestamp) values(new.owner_address,now(),'contract_transfer',(select block.timestamp from transaction,block where block.id=transaction.block_id and transaction.id=new.transaction_id)),(new.to_address,now(),'contract_transfer',(select block.timestamp from transaction,block where block.id=transaction.block_id and transaction.id=new.transaction_id));
update `transaction` SET `from`=new.owner_address, `type`=1 where `id`=new.transaction_id;
insert into transfer(`from`,`to`,`amount`,`timestamp`,`transaction_id`) values(new.owner_address,new.to_address,new.amount,(select(transaction.timestamp) from transaction where id = new.transaction_id),new.transaction_id);
END$$

DELIMITER ;
