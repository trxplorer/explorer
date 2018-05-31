ALTER TABLE `sync_account` 
ADD COLUMN `origin` VARCHAR(45) NULL AFTER `date_locked`;
