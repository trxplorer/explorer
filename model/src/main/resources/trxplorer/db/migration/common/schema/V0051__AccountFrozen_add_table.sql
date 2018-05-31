CREATE TABLE `account_frozen` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `balance` BIGINT NOT NULL,
  `expire_time` DATETIME(3) NOT NULL,
  `account_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`));
