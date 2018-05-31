ALTER TABLE `contract_participate_asset_issue` 
ADD CONSTRAINT `fk_contract_participate_asset_issue_owner_id`
  FOREIGN KEY (`owner_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_contract_participate_asset_issue_to_id`
  FOREIGN KEY (`to_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
