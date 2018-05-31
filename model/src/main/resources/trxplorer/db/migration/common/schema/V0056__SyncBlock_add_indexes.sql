ALTER TABLE `sync_block` 
ADD INDEX `block_num_index` (`block_num` ASC),
ADD INDEX `date_start_index` (`date_start` ASC),
ADD INDEX `date_end_index` (`date_end` ASC),
ADD INDEX `date_locked_index` (`date_locked` ASC),
ADD INDEX `node_id_index` (`node_id` ASC);
