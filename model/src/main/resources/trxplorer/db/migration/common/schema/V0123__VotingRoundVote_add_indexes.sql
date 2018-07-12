ALTER TABLE `voting_round_vote` 
ADD INDEX `vrv_vote_count_index` (`vote_count` ASC),
ADD INDEX `vrv_timestamp_index` (`timestamp` ASC),
ADD INDEX `vrv_owner_address_index` (`owner_address` ASC),
ADD INDEX `vrv_vote_address_index` (`vote_address` ASC);
