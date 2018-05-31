CREATE TABLE `contract_withdraw_balance` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  `transaction_id` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`));
