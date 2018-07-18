ALTER TABLE `transfer` 
ADD INDEX `fk_transfer_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `transfer` 
ADD CONSTRAINT `fk_transfer_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
