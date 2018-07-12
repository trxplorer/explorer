ALTER TABLE `voting_round_stats` 
DROP COLUMN `global_position`,
DROP COLUMN `global_votes`,
CHANGE COLUMN `round_votes` `vote_count` BIGINT(20) UNSIGNED NOT NULL ,
CHANGE COLUMN `round_position` `position` INT(10) UNSIGNED NOT NULL ,
ADD COLUMN `vote_lost_count` BIGINT(20) UNSIGNED NOT NULL AFTER `position`;
