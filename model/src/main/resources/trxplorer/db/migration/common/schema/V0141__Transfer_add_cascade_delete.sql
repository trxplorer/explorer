ALTER TABLE `transfer` 
DROP FOREIGN KEY `fk_transfer_tx_id`;
ALTER TABLE `transfer` 
ADD CONSTRAINT `fk_transfer_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;
