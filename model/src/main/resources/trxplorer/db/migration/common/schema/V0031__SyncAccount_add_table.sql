CREATE TABLE `sync_account` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `address` VARCHAR(45) NOT NULL,
  `date_start` DATETIME(3) NULL,
  PRIMARY KEY (`id`));
