ALTER TABLE `account_asset` 
ADD UNIQUE INDEX `account_asset_unique` (`asset_name` ASC, `account_id` ASC, `balance` ASC);
