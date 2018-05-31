ALTER TABLE `sync_account` 
CHANGE COLUMN `date_start` `last_updated` DATETIME NULL DEFAULT NULL ,
ADD COLUMN `last_sync` DATETIME NULL AFTER `last_updated`;
