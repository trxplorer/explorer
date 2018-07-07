ALTER TABLE `account_vote` 
CHANGE COLUMN `vote_count` `vote_count` BIGINT(20) NOT NULL ,
ADD COLUMN `timestamp` DATETIME NULL AFTER `account_id`;
