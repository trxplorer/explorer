ALTER TABLE `account_asset` 
ADD INDEX `fk_account_asset_account_id_idx` (`account_id` ASC);
ALTER TABLE `account_asset` 
ADD CONSTRAINT `fk_account_asset_account_id`
  FOREIGN KEY (`account_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
