CREATE TABLE `contract_freeze_balance` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_address` VARCHAR(45) NOT NULL,
  `frozen_balance` BIGINT UNSIGNED NOT NULL,
  `frozen_duration` BIGINT UNSIGNED NOT NULL,
  `transaction_id` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`));
