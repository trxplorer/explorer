ALTER TABLE `block` 
CHANGE COLUMN `parent_hash` `parent_hash` VARCHAR(64) NOT NULL ,
CHANGE COLUMN `txTrieRoot` `txTrieRoot` VARCHAR(64) NULL DEFAULT NULL ;
