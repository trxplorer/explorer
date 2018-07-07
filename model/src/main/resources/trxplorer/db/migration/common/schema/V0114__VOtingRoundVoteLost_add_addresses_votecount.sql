ALTER TABLE `voting_round_vote_lost` 
DROP FOREIGN KEY `fk_voting_round_vote_lost_contract_vote_id`;
ALTER TABLE `voting_round_vote_lost` 
DROP COLUMN `contract_vote_id`,
ADD COLUMN `owner_address` VARCHAR(45) NOT NULL AFTER `voting_round_id`,
ADD COLUMN `vote_address` VARCHAR(45) NOT NULL AFTER `owner_address`,
ADD COLUMN `vote_count` BIGINT(20) UNSIGNED NOT NULL AFTER `vote_address`,
ADD COLUMN `timestamp` DATETIME NOT NULL AFTER `vote_count`,
DROP INDEX `fk_voting_round_vote_lost_contract_vote_id_idx` ;