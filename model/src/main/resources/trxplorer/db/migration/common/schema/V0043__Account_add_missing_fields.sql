ALTER TABLE `account` 
ADD COLUMN `balance` BIGINT UNSIGNED NOT NULL DEFAULT 0 AFTER `address`,
ADD COLUMN `create_time` DATETIME(3) NOT NULL AFTER `balance`,
ADD COLUMN `allowance` BIGINT UNSIGNED NULL AFTER `create_time`,
ADD COLUMN `latest_withdaw_time` DATETIME(3) NULL AFTER `allowance`;
