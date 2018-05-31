CREATE TABLE `account_vote` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `vote_address` VARCHAR(45) NOT NULL,
  `vote_account_id` VARCHAR(45) NULL,
  `vote_count` BIGINT UNSIGNED NOT NULL,
  `account_id` BIGINT NULL,
  PRIMARY KEY (`id`));
