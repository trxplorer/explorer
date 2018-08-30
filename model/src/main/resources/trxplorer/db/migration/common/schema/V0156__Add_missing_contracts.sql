CREATE TABLE `contract_update_asset` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_update_asset_from_index` (`owner_address` ASC));

CREATE TABLE `contract_unfreeze_asset` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_unfreeze_asset_from_index` (`owner_address` ASC));
  
CREATE TABLE `contract_update_setting` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_update_setting_from_index` (`owner_address` ASC));
    
CREATE TABLE `contract_set_account_id` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_set_account_id_from_index` (`owner_address` ASC));
  
CREATE TABLE `contract_proposal_create` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_proposal_create_from_index` (`owner_address` ASC));
  
CREATE TABLE `contract_proposal_approve` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_proposal_approve_from_index` (`owner_address` ASC));
  
CREATE TABLE `contract_proposal_delete` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_proposal_delete_from_index` (`owner_address` ASC));
  
CREATE TABLE `contract_create_smartcontract` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_create_smartcontract_from_index` (`owner_address` ASC));
  
CREATE TABLE `contract_trigger_smartcontract` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_trigger_smartcontract` (`owner_address` ASC));  
  
CREATE TABLE `contract_buy_storage` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_buy_storage_from_index` (`owner_address` ASC));

CREATE TABLE `contract_buy_storage_bytes` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_buy_storage_bytes` (`owner_address` ASC));

CREATE TABLE `contract_sell_storage` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_sell_storage_from_index` (`owner_address` ASC));
  
CREATE TABLE `contract_exchange_create` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_exchange_create` (`owner_address` ASC));

CREATE TABLE `contract_exchange_inject` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_exchange_inject_from_index` (`owner_address` ASC));
  
CREATE TABLE `contract_exchange_withdraw_contract` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_exchange_withdraw_contract_from_index` (`owner_address` ASC));
  
  
CREATE TABLE `contract_exchange_transaction_contract` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `contract_exchange_transaction_contract_from_index` (`owner_address` ASC));
  




  