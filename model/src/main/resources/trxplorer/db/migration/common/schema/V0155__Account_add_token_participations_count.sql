ALTER TABLE `account` 
ADD COLUMN `participations_count` INT(11) NULL DEFAULT NULL AFTER `tokens_count`;
