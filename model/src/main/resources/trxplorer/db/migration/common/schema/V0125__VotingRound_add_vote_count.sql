ALTER TABLE `voting_round` 
ADD COLUMN `vote_count` BIGINT UNSIGNED NULL AFTER `sync_end`;
