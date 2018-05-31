ALTER TABLE `contract_vote_asset` 
ADD COLUMN `vote_to_address` VARCHAR(45) NOT NULL AFTER `owner_address`;
