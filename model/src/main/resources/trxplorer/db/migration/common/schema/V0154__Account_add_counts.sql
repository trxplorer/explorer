ALTER TABLE `account`
ADD COLUMN `transfer_from_count` INT NULL AFTER `is_committee`,
ADD COLUMN `transfer_to_count` INT NULL AFTER `transfer_from_count`,
ADD COLUMN `tokens_count` INT NULL AFTER `transfer_to_count`;
