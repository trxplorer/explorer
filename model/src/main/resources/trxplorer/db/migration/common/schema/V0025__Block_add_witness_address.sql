ALTER TABLE `block` 
DROP FOREIGN KEY `fk_block_witness_id`;
ALTER TABLE `block` 
CHANGE COLUMN `witness_id` `witness_id` BIGINT(20) UNSIGNED NULL ,
ADD COLUMN `witness_address` VARCHAR(45) NOT NULL AFTER `witness_id`;
ALTER TABLE `block` 
ADD CONSTRAINT `fk_block_witness_id`
  FOREIGN KEY (`witness_id`)
  REFERENCES `witness` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
