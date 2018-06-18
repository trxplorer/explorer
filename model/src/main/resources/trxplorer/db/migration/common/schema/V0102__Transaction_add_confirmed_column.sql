ALTER TABLE `transaction` 
ADD COLUMN `confirmed` TINYINT(1) NOT NULL DEFAULT 0 AFTER `expiration`;
