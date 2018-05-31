ALTER TABLE `contract_freeze_balance` 
ADD INDEX `fk_contract_freeze_balance_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_freeze_balance` 
ADD CONSTRAINT `fk_contract_freeze_balance_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_unfreeze_balance` 
ADD INDEX `fk_contract_unfreeze_balance_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_unfreeze_balance` 
ADD CONSTRAINT `fk_contract_unfreeze_balance_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_withdraw_balance` 
ADD INDEX `fk_contract_withdraw_balance_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_withdraw_balance` 
ADD CONSTRAINT `fk_contract_withdraw_balance_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
