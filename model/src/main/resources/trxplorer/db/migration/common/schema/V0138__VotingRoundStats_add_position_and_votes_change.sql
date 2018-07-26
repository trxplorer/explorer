ALTER TABLE `voting_round_stats` 
ADD COLUMN `position_change` INT NULL AFTER `vote_lost_count`,
ADD COLUMN `votes_change` BIGINT(20) NULL AFTER `position_change`;
