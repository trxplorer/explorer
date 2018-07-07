ALTER TABLE `block` 
ADD INDEX `block_num_index` (`num` ASC),
ADD INDEX `block_hash_index` (`hash` ASC);
