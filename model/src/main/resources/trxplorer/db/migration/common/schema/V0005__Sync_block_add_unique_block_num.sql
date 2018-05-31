ALTER TABLE `sync_block` 
CHANGE COLUMN `block_num` `block_num` BIGINT(20) UNSIGNED NULL DEFAULT NULL ,
ADD UNIQUE INDEX `block_num_UNIQUE` (`block_num` ASC);
