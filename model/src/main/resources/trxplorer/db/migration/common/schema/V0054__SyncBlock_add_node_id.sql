ALTER TABLE `sync_block` 
ADD COLUMN `node_id` INT NOT NULL AFTER `date_locked`;
