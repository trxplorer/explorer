CREATE TABLE `transfer` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `from` VARCHAR(45) NOT NULL,
  `to` VARCHAR(45) NOT NULL,
  `amount` BIGINT UNSIGNED NOT NULL,
  `token` VARCHAR(150) NULL,
  `timestamp` TIMESTAMP NOT NULL,
  `transaction_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `transfer_from_index` (`from` ASC),
  INDEX `transfer_to_index` (`to` ASC),
  INDEX `transfer_amount_index` (`amount` ASC),
  INDEX `transfer_token_index` (`token` ASC),
  INDEX `transfer_timestamp_index` (`timestamp` ASC));
