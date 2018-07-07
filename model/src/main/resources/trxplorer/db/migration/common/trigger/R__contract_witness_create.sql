DELIMITER $$

DROP TRIGGER IF EXISTS contract_witness_create_AFTER_INSERT$$

CREATE DEFINER = CURRENT_USER TRIGGER `contract_witness_create_AFTER_INSERT` AFTER INSERT ON `contract_witness_create` FOR EACH ROW
BEGIN
insert ignore into sync_account(address,date_created,origin,tx_timestamp) values(new.owner_address,now(),'contract_witness_create',(select block.timestamp from transaction,block where block.id=transaction.block_id and transaction.id=new.transaction_id));
END$$

DELIMITER ;
