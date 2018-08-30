DELIMITER $$

DROP TRIGGER IF EXISTS contract_proposal_delete_AFTER_INSERT$$

CREATE DEFINER = CURRENT_USER TRIGGER `contract_proposal_delete_AFTER_INSERT` AFTER INSERT ON `contract_proposal_delete` FOR EACH ROW
BEGIN
insert ignore into sync_account(address,date_created,origin,tx_timestamp) values(new.owner_address,now(),'contract_proposal_delete',(select block.timestamp from transaction,block where block.id=transaction.block_id and transaction.id=new.transaction_id));
update `transaction` SET `from`=new.owner_address, `type`=18 where `id`=new.transaction_id;
END$$

DELIMITER ;
