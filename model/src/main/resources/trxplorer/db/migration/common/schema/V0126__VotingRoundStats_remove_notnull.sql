ALTER TABLE `voting_round_stats` 
CHANGE COLUMN `vote_count` `vote_count` BIGINT(20) UNSIGNED NULL ,
CHANGE COLUMN `position` `position` INT(10) UNSIGNED NULL ,
CHANGE COLUMN `vote_lost_count` `vote_lost_count` BIGINT(20) UNSIGNED NULL ;
