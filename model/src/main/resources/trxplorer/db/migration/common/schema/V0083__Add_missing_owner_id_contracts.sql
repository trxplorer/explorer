ALTER TABLE `contract_freeze_balance` 
ADD COLUMN `owner_id` BIGINT(20) UNSIGNED NULL DEFAULT NULL AFTER `transaction_id`;

ALTER TABLE `contract_unfreeze_balance` 
ADD COLUMN `owner_id` BIGINT(20) UNSIGNED NULL DEFAULT NULL AFTER `transaction_id`;

ALTER TABLE `contract_withdraw_balance` 
ADD COLUMN `owner_id` BIGINT(20) UNSIGNED NULL DEFAULT NULL AFTER `transaction_id`;
