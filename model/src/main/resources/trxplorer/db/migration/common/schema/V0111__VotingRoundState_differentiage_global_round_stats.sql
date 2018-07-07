ALTER TABLE `voting_round_stats` 
CHANGE COLUMN `total_votes` `global_votes` BIGINT(20) UNSIGNED NOT NULL ,
CHANGE COLUMN `position` `global_position` INT(10) UNSIGNED NOT NULL ,
ADD COLUMN `round_votes` BIGINT(20) UNSIGNED NOT NULL AFTER `global_position`,
ADD COLUMN `round_position` INT(10) UNSIGNED NOT NULL AFTER `round_votes`;
