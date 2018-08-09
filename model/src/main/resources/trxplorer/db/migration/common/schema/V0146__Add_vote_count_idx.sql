ALTER TABLE `account_vote` 
ADD INDEX `fk_account_vote_vote_count_idx` (`vote_count` ASC);

ALTER TABLE `contract_vote_witness` 
ADD INDEX `contract_vote_witness_vote_count_idx` (`vote_count` ASC);

