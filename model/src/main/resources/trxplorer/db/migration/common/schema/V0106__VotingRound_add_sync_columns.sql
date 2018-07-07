ALTER TABLE `voting_round` 
CHANGE COLUMN `round` `round` VARCHAR(4) NULL DEFAULT NULL ,
ADD COLUMN `sync_start` DATETIME NULL AFTER `end_date`,
ADD COLUMN `sync_end` DATETIME NULL AFTER `sync_start`;
