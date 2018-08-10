ALTER TABLE `sync_account` 
DROP INDEX `address_unique` ,
ADD UNIQUE INDEX `address_unique` (`address` ASC, `origin` ASC);
