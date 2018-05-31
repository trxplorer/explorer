ALTER TABLE `sync_node` 
ADD COLUMN `start_date` DATETIME NULL AFTER `ping`,
ADD COLUMN `end_date` DATETIME NULL AFTER `start_date`;
