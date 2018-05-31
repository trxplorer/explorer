ALTER TABLE `transaction_result` 
ADD INDEX `fk_transaction_result_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `transaction_result` 
ADD CONSTRAINT `fk_transaction_result_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
