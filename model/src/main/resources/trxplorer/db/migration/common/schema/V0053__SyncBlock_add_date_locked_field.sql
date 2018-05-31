ALTER TABLE `sync_block` 
ADD COLUMN `date_locked` DATETIME(3) NULL AFTER `date_end`;
