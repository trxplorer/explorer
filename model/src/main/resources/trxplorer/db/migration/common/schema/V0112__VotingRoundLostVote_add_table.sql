CREATE TABLE `voting_round_vote_lost` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `voting_round_id` INT(10) UNSIGNED NOT NULL,
  `contract_vote_id` BIGINT(20) UNSIGNED NOT NULL,
  INDEX `fk_voting_round_vote_lost_voting_round_id_idx` (`voting_round_id` ASC),
  INDEX `fk_voting_round_vote_lost_contract_vote_id_idx` (`contract_vote_id` ASC),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_voting_round_vote_lost_voting_round_id`
    FOREIGN KEY (`voting_round_id`)
    REFERENCES `voting_round` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_voting_round_vote_lost_contract_vote_id`
    FOREIGN KEY (`contract_vote_id`)
    REFERENCES `contract_vote_witness` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
