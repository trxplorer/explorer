ALTER TABLE `sync_account` 
ADD UNIQUE INDEX `address_unique` (`address` ASC),
ADD INDEX `address_index` (`address` ASC);
