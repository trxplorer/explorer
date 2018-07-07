DELIMITER $$

DROP TRIGGER IF EXISTS contract_vote_witness_AFTER_INSERT$$

CREATE DEFINER = CURRENT_USER TRIGGER `contract_vote_witness_AFTER_INSERT` AFTER INSERT ON `contract_vote_witness` FOR EACH ROW
BEGIN
insert ignore into sync_account(address,date_created,origin,tx_timestamp) values(new.owner_address,now(),'contract_vote_witness',(select block.timestamp from transaction,block where block.id=transaction.block_id and transaction.id=new.transaction_id)),(new.vote_address,now(),'contract_vote_witness',(select block.timestamp from transaction,block where block.id=transaction.block_id and transaction.id=new.transaction_id));
END$$

DELIMITER ;
