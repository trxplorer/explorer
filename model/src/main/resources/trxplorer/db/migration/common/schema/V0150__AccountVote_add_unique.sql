ALTER TABLE `account_vote` 
ADD UNIQUE INDEX `account_vote_unique` (`account_id` ASC, `vote_address` ASC, `vote_count` ASC);
