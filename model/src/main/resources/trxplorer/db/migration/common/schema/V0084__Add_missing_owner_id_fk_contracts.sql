ALTER TABLE `contract_freeze_balance` 
ADD INDEX `fk_contract_freeze_balance_owner_id_idx` (`owner_id` ASC);
ALTER TABLE `contract_freeze_balance` 
ADD CONSTRAINT `fk_contract_freeze_balance_owner_id`
  FOREIGN KEY (`owner_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_unfreeze_balance` 
ADD INDEX `fk_contract_unfreeze_balance_owner_id_idx` (`owner_id` ASC);
ALTER TABLE `contract_unfreeze_balance` 
ADD CONSTRAINT `fk_contract_unfreeze_balance_owner_id`
  FOREIGN KEY (`owner_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_withdraw_balance` 
ADD INDEX `fk_contract_withdraw_balance_owner_id_idx` (`owner_id` ASC);
ALTER TABLE `contract_withdraw_balance` 
ADD CONSTRAINT `fk_contract_withdraw_balance_owner_id`
  FOREIGN KEY (`owner_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
