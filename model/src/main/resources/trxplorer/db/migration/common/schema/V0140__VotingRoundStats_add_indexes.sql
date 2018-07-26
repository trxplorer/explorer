ALTER TABLE `voting_round_stats` 
ADD INDEX `fk_voting_round_stats_address` (`address` ASC);

ALTER TABLE `vote_live` 
ADD INDEX `vote_live_address_index` (`address` ASC);

ALTER TABLE `voting_round` 
ADD INDEX `voting_round_round_index` (`round` ASC);
