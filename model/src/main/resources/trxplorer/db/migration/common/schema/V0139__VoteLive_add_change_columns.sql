ALTER TABLE `vote_live` 
ADD COLUMN `position_change` INT NULL AFTER `position`,
ADD COLUMN `vote_change` BIGINT NULL AFTER `position_change`;
