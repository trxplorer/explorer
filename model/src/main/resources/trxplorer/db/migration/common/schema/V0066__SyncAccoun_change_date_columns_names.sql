ALTER TABLE `sync_account` 
CHANGE COLUMN `last_updated` `date_created` DATETIME NULL DEFAULT NULL ,
CHANGE COLUMN `last_sync` `date_locked` DATETIME NULL DEFAULT NULL ;
