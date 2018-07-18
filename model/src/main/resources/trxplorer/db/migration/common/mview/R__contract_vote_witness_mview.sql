drop table if exists contract_vote_witness_mview;
create table contract_vote_witness_mview as select * from contract_vote_witness;
truncate contract_vote_witness_mview;
ALTER TABLE `contract_vote_witness_mview` 
ADD INDEX `cvrv_mview_owner_address_index` (`owner_address` ASC),
ADD INDEX `cvrv_mview_vote_address_index` (`vote_address` ASC),
ADD INDEX `cvrv_mview_vote_count_index` (`vote_count` ASC),
ADD INDEX `cvrv_mview_tx_id_index` (`transaction_id` ASC);
