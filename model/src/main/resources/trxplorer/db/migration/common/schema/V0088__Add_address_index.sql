ALTER TABLE `account_vote` 
ADD INDEX `account_vote_vote_address_index` (`vote_address` ASC);

ALTER TABLE `block` 
ADD INDEX `block_witness_address_index` (`witness_address` ASC);

ALTER TABLE `contract_account_create` 
ADD INDEX `contract_account_create_owner_address_index` (`owner_address` ASC);

ALTER TABLE `contract_account_update` 
ADD INDEX `contract_account_update_owner_address_index` (`owner_address` ASC);

ALTER TABLE `contract_asset_issue` 
ADD INDEX `contract_asset_issue_owner_address` (`owner_address` ASC);

ALTER TABLE `contract_deploy` 
ADD INDEX `contract_deploy_owner_address_index` (`owner_address` ASC);

ALTER TABLE `contract_freeze_balance` 
ADD INDEX `contract_freeze_balance_owner_address_index` (`owner_address` ASC);

ALTER TABLE `contract_participate_asset_issue` 
ADD INDEX `contract_participate_asset_issue_owner_address_index` (`owner_address` ASC),
ADD INDEX `contract_participate_asset_issue_to_address_index` (`to_address` ASC);

ALTER TABLE `contract_transfer` 
ADD INDEX `contract_transfer_owner_address_index` (`owner_address` ASC),
ADD INDEX `contract_transfer_to_address_index` (`to_address` ASC);

ALTER TABLE `contract_transfer_asset` 
ADD INDEX `contract_transfer_asset_owner_address_index` (`owner_address` ASC),
ADD INDEX `contract_transfer_asset_to_address_index` (`to_address` ASC);

ALTER TABLE `contract_unfreeze_balance` 
ADD INDEX `contract_unfreeze_balance_owner_address_index` (`owner_address` ASC);

ALTER TABLE `contract_vote_asset` 
ADD INDEX `contract_vote_asset_owner_index_index` (`owner_address` ASC);

ALTER TABLE `contract_vote_asset_vote` 
ADD INDEX `contract_vote_asset_vote_owner_address_index` (`owner_address` ASC);

ALTER TABLE `contract_vote_witness` 
ADD INDEX `contract_vote_witness_owner_address_index` (`owner_address` ASC),
ADD INDEX `contract_vote_witness_vote_address_index` (`vote_address` ASC);

ALTER TABLE `contract_vote_witness_vote` 
ADD INDEX `contract_vote_witness_vote_owner_address_index` (`owner_address` ASC);

ALTER TABLE `contract_withdraw_balance` 
ADD INDEX `contract_withdraw_balance_owner_address_index` (`owner_address` ASC);

ALTER TABLE `contract_witness_create` 
ADD INDEX `contract_witness_create_owner_address_index` (`owner_address` ASC);

ALTER TABLE `contract_witness_update` 
ADD INDEX `contract_witness_update_owner_address_index` (`owner_address` ASC);

ALTER TABLE `witness` 
ADD INDEX `witness_address_index` (`address` ASC);

