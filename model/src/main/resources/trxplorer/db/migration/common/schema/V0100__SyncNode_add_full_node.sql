ALTER TABLE `sync_node` 
CHANGE COLUMN `is_validating` `is_validating` VARCHAR(45) NOT NULL DEFAULT '0' AFTER `node_id`,
CHANGE COLUMN `ping` `ping` DATETIME NULL DEFAULT NULL AFTER `is_validating`,
CHANGE COLUMN `sync_start` `sync_start_full` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `sync_end` `sync_end_full` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `start_date` `start_full_date` DATETIME NULL DEFAULT NULL ,
CHANGE COLUMN `end_date` `end_full_date` DATETIME NULL DEFAULT NULL ,
ADD COLUMN `sync_start_solidity` BIGINT(20) NULL DEFAULT NULL AFTER `end_full_date`,
ADD COLUMN `sync_end_solidity` BIGINT(20) NULL DEFAULT NULL AFTER `sync_start_solidity`,
ADD COLUMN `start_solidity_date` DATETIME NULL DEFAULT NULL AFTER `sync_end_solidity`,
ADD COLUMN `end_solidity_date` DATETIME NULL DEFAULT NULL AFTER `start_solidity_date`;
