ALTER TABLE `transaction` 
ADD COLUMN `from` VARCHAR(45) NULL AFTER `block_id`,
ADD COLUMN `type` INT NULL AFTER `from`;
