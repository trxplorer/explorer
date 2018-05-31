CREATE TABLE `chart_transaction` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `date` DATETIME NOT NULL,
  `total_tx` INT UNSIGNED NOT NULL,
  `total_block` INT UNSIGNED NOT NULL,
  `avg_block_size` INT UNSIGNED NOT NULL,
  `avg_block_time` INT UNSIGNED NOT NULL,
  `new_address` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`));
