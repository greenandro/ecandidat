UPDATE `candidat_cursus_post_bac` set `lib_cursus`='lib formation' where `lib_cursus` is null;
ALTER TABLE `candidat_cursus_post_bac` MODIFY `lib_cursus` VARCHAR(255) NOT NULL;