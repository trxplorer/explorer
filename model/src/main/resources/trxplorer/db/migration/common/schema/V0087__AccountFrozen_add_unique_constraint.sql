ALTER TABLE `account_frozen` 
ADD UNIQUE INDEX `unique_balance_expire_time` (`balance` ASC, `expire_time` ASC);
