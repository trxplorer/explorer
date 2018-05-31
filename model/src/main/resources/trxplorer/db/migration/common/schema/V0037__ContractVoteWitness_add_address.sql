ALTER TABLE `contract_vote_witness` 
DROP FOREIGN KEY `fk_contract_vote_witness_owner_id`;
ALTER TABLE `contract_vote_witness` 
CHANGE COLUMN `owner_id` `owner_id` BIGINT(20) UNSIGNED NULL ,
ADD COLUMN `owner_address` VARCHAR(45) NOT NULL AFTER `owner_id`;
ALTER TABLE `contract_vote_witness` 
ADD CONSTRAINT `fk_contract_vote_witness_owner_id`
  FOREIGN KEY (`owner_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
