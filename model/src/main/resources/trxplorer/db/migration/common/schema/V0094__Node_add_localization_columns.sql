ALTER TABLE `node` 
ADD COLUMN `longitude` DECIMAL NULL AFTER `up`,
ADD COLUMN `latitude` DECIMAL NULL AFTER `longitude`,
ADD COLUMN `country_code` VARCHAR(10) NULL AFTER `latitude`,
ADD COLUMN `country` VARCHAR(45) NULL AFTER `country_code`,
ADD COLUMN `city` VARCHAR(150) NULL AFTER `country`,
ADD COLUMN `last_updated` DATETIME NULL AFTER `city`;
