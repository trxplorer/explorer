ALTER TABLE `witness` 
DROP FOREIGN KEY `fk_witness_account_id`;
ALTER TABLE `witness` 
CHANGE COLUMN `account_id` `account_id` BIGINT(20) UNSIGNED NULL ,
ADD COLUMN `address` VARCHAR(45) NOT NULL AFTER `account_id`;
ALTER TABLE `witness` 
ADD CONSTRAINT `fk_witness_account_id`
  FOREIGN KEY (`account_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
