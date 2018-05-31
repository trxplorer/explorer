ALTER TABLE `witness` 
CHANGE COLUMN `total_produced` `total_produced` BIGINT UNSIGNED NOT NULL DEFAULT '0' ,
CHANGE COLUMN `total_missed` `total_missed` BIGINT UNSIGNED NOT NULL DEFAULT '0' ;
