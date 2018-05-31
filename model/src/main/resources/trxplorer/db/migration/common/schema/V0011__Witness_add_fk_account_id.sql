ALTER TABLE `witness` 
ADD INDEX `fk_witness_account_id_idx` (`account_id` ASC);
ALTER TABLE `witness` 
ADD CONSTRAINT `fk_witness_account_id`
  FOREIGN KEY (`account_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
