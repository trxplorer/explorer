ALTER TABLE `contract_vote_witness` 
ADD COLUMN `vote_address` VARCHAR(45) NOT NULL AFTER `owner_address`,
ADD COLUMN `vote_count` BIGINT(20) UNSIGNED NOT NULL AFTER `vote_address`;
