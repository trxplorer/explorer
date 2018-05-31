ALTER TABLE `account_vote` 
CHANGE COLUMN `vote_account_id` `vote_account_id` BIGINT UNSIGNED NULL DEFAULT NULL ,
CHANGE COLUMN `account_id` `account_id` BIGINT(20) UNSIGNED NULL DEFAULT NULL ,
ADD INDEX `fk_account_vote_account_id_idx` (`account_id` ASC),
ADD INDEX `fk_account_vote_vote_account_id_idx` (`vote_account_id` ASC);
ALTER TABLE `account_vote` 
ADD CONSTRAINT `fk_account_vote_account_id`
  FOREIGN KEY (`account_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_account_vote_vote_account_id`
  FOREIGN KEY (`vote_account_id`)
  REFERENCES `account` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
