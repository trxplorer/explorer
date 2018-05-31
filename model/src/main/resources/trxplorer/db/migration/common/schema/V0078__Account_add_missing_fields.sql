ALTER TABLE `account` 
ADD COLUMN `is_witness` TINYINT UNSIGNED NULL AFTER `bandwitdth`,
ADD COLUMN `is_committee` TINYINT UNSIGNED NULL AFTER `is_witness`;
