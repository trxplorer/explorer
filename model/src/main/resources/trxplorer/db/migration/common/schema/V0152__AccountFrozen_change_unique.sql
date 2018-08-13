ALTER TABLE `account_frozen` 
DROP INDEX `unique_balance_expire_time` ,
ADD UNIQUE INDEX `unique_balance_expire_time` (`balance` ASC, `expire_time` ASC, `account_id` ASC);
