ALTER TABLE `candidat_cursus_post_bac`	ADD COLUMN `obtenu_cursus` VARCHAR(1) NULL DEFAULT NULL COMMENT 'si le cursus a été obtenu' AFTER `tem_obtenu_cursus`;
UPDATE candidat_cursus_post_bac set obtenu_cursus = 'O';
ALTER TABLE `candidat_cursus_post_bac` MODIFY `obtenu_cursus` VARCHAR(1) NOT NULL COMMENT 'si le cursus a été obtenu';
ALTER TABLE `candidat_cursus_post_bac` DROP COLUMN `tem_obtenu_cursus`;