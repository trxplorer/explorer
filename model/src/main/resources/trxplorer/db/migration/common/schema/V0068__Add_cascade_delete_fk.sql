SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `transaction` 
DROP FOREIGN KEY `fk_transaction_block`;
ALTER TABLE `transaction` 
ADD CONSTRAINT `fk_transaction_block`
  FOREIGN KEY (`block_id`)
  REFERENCES`block` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `transaction_result` 
DROP FOREIGN KEY `fk_transaction_result_tx_id`;
ALTER TABLE `transaction_result` 
ADD CONSTRAINT `fk_transaction_result_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_account_create` 
DROP FOREIGN KEY `fk_contract_account_create_tx_id`;
ALTER TABLE `contract_account_create` 
ADD CONSTRAINT `fk_contract_account_create_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_account_update` 
DROP FOREIGN KEY `fk_contract_account_update_tx_id`;
ALTER TABLE `contract_account_update` 
ADD CONSTRAINT `fk_contract_account_update_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_asset_issue` 
DROP FOREIGN KEY `fk_contract_asset_issue_tx_id`;
ALTER TABLE `contract_asset_issue` 
ADD CONSTRAINT `fk_contract_asset_issue_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_deploy` 
DROP FOREIGN KEY `fk_contract_deploy_tx_id`;
ALTER TABLE `contract_deploy` 
ADD CONSTRAINT `fk_contract_deploy_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_participate_asset_issue` 
DROP FOREIGN KEY `fk_contract_participate_asset_issue_tx_id`;
ALTER TABLE `contract_participate_asset_issue` 
ADD CONSTRAINT `fk_contract_participate_asset_issue_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_transfer` 
DROP FOREIGN KEY `fk_contract_transfer_tx_id`;
ALTER TABLE `contract_transfer` 
ADD CONSTRAINT `fk_contract_transfer_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;
 
ALTER TABLE `contract_transfer_asset` 
DROP FOREIGN KEY `fk_contract_transfer_asset_tx_id`;
ALTER TABLE `contract_transfer_asset` 
ADD CONSTRAINT `fk_contract_transfer_asset_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_vote_asset` 
DROP FOREIGN KEY `fk_contract_vote_asset_tx_id`;
ALTER TABLE `contract_vote_asset` 
ADD CONSTRAINT `fk_contract_vote_asset_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_vote_asset_vote` 
DROP FOREIGN KEY `fk_contract_vote_asset_vote_va_id`;
ALTER TABLE `contract_vote_asset_vote` 
ADD CONSTRAINT `fk_contract_vote_asset_vote_va_id`
  FOREIGN KEY (`vote_asset_id`)
  REFERENCES`contract_vote_asset` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_vote_witness` 
DROP FOREIGN KEY `fk_contract_vote_witness_tx_id`;
ALTER TABLE `contract_vote_witness` 
ADD CONSTRAINT `fk_contract_vote_witness_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_vote_witness_vote` 
DROP FOREIGN KEY `fk_contract_vote_witness_vote_vw_id`;
ALTER TABLE `contract_vote_witness_vote` 
ADD CONSTRAINT `fk_contract_vote_witness_vote_vw_id`
  FOREIGN KEY (`vote_witness_id`)
  REFERENCES`contract_vote_witness` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_witness_create` 
DROP FOREIGN KEY `fk_contract_witness_create_tx_id`;
ALTER TABLE `contract_witness_create` 
ADD CONSTRAINT `fk_contract_witness_create_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `contract_witness_update` 
DROP FOREIGN KEY `fk_contract_witness_update_tx_id`;
ALTER TABLE `contract_witness_update` 
ADD CONSTRAINT `fk_contract_witness_update_tx_id`
  FOREIGN KEY (`transaction_id`)
  REFERENCES`transaction` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `account_asset` 
DROP FOREIGN KEY `fk_account_asset_account_id`;
ALTER TABLE `account_asset` 
ADD CONSTRAINT `fk_account_asset_account_id`
  FOREIGN KEY (`account_id`)
  REFERENCES`account` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `account_frozen` 
ADD INDEX `fk_account_frozen_account_id_idx` (`account_id` ASC);
ALTER TABLE `account_frozen` 
ADD CONSTRAINT `fk_account_frozen_account_id`
  FOREIGN KEY (`account_id`)
  REFERENCES`account` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `account_vote` 
DROP FOREIGN KEY `fk_account_vote_account_id`;
ALTER TABLE `account_vote` 
ADD CONSTRAINT `fk_account_vote_account_id`
  FOREIGN KEY (`account_id`)
  REFERENCES`account` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;
  
ALTER TABLE `witness` 
DROP FOREIGN KEY `fk_witness_account_id`;
ALTER TABLE `witness` 
ADD CONSTRAINT `fk_witness_account_id`
  FOREIGN KEY (`account_id`)
  REFERENCES`account` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;
  
  
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

