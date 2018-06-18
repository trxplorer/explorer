update block set confirmed = 0 where confirmed is null;

ALTER TABLE `block` 
CHANGE COLUMN `confirmed` `confirmed` TINYINT(1) NOT NULL DEFAULT 0 ;
