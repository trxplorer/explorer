ALTER TABLE `contract_unfreeze_balance` 
DROP FOREIGN KEY `fk_contract_unfreeze_balance_tx_id`;
ALTER TABLE `contract_unfreeze_balance` 
ADD CONSTRAINT `fk_contract_unfreeze_balance_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;
