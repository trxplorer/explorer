ALTER TABLE `contract_freeze_balance` 
DROP FOREIGN KEY `fk_contract_freeze_balance_tx_id`;
ALTER TABLE `contract_freeze_balance` 
ADD CONSTRAINT `fk_contract_freeze_balance_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;
