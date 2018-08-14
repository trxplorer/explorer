ALTER TABLE `voting_round` 
ADD COLUMN `valid` TINYINT NOT NULL DEFAULT 1 AFTER `vote_count`;
