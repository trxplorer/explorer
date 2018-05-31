CREATE TABLE `sync_node` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `node_id` INT NOT NULL,
  `sync_start` BIGINT NULL,
  `sync_end` BIGINT NULL,
  `is_validating` VARCHAR(45) NOT NULL DEFAULT 0,
  `ping` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `node_id_UNIQUE` (`node_id` ASC));
