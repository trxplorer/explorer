ALTER TABLE `contract_update_asset` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_update_asset_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_update_asset` 
ADD CONSTRAINT `fk_contract_update_asset_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
  
ALTER TABLE `contract_unfreeze_asset` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_unfreeze_asset_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_unfreeze_asset` 
ADD CONSTRAINT `fk_contract_unfreeze_asset_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;


ALTER TABLE `contract_update_setting` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_update_setting_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_update_setting` 
ADD CONSTRAINT `fk_contract_update_setting_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
  
ALTER TABLE `contract_set_account_id` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_set_account_id_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_set_account_id` 
ADD CONSTRAINT `fk_contract_set_account_id_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
  
ALTER TABLE `contract_proposal_create` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_proposal_create_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_proposal_create` 
ADD CONSTRAINT `fk_contract_proposal_create_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_proposal_approve` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_proposal_approve_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_proposal_approve` 
ADD CONSTRAINT `fk_contract_proposal_approve_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_proposal_delete` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_proposal_delete_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_proposal_delete` 
ADD CONSTRAINT `fk_contract_proposal_delete_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_create_smartcontract` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_create_smartcontract_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_create_smartcontract` 
ADD CONSTRAINT `fk_contract_create_smartcontract_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_trigger_smartcontract` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_trigger_smartcontract_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_trigger_smartcontract` 
ADD CONSTRAINT `fk_contract_trigger_smartcontract_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_buy_storage` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_buy_storage_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_buy_storage` 
ADD CONSTRAINT `fk_contract_buy_storage_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_buy_storage_bytes` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_buy_storage_bytes_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_buy_storage_bytes` 
ADD CONSTRAINT `fk_contract_buy_storage_bytes_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_sell_storage` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_sell_storage_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_sell_storage` 
ADD CONSTRAINT `fk_contract_sell_storage_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_exchange_create` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_exchange_create_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_exchange_create` 
ADD CONSTRAINT `fk_contract_exchange_create_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_exchange_inject` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_exchange_inject_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_exchange_inject` 
ADD CONSTRAINT `fk_contract_exchange_inject_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_exchange_withdraw_contract` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_exchange_withdraw_contract_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_exchange_withdraw_contract` 
ADD CONSTRAINT `fk_contract_exchange_withdraw_contract_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `contract_exchange_transaction_contract` 
ADD COLUMN `transaction_id` BIGINT(20) UNSIGNED NOT NULL AFTER `owner_address`,
ADD INDEX `fk_contract_exchange_transaction_contract_tx_id_idx` (`transaction_id` ASC);
ALTER TABLE `contract_exchange_transaction_contract` 
ADD CONSTRAINT `fk_contract_exchange_transaction_contract_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES `transaction` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;




