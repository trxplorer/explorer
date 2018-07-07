ALTER TABLE `voting_round_stats` 
DROP FOREIGN KEY `fk_voting_round_stats_witness_id`;
ALTER TABLE `voting_round_stats` 
CHANGE COLUMN `witness_id` `address` VARCHAR(45) NOT NULL ,
DROP INDEX `fk_voting_round_stats_witness_id_idx` ;
