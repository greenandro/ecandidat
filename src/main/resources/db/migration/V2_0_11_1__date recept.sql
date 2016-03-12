ALTER TABLE `candidature` CHANGE COLUMN `dat_recept_dossier_cand` `dat_trans_dossier_cand` TIMESTAMP NULL DEFAULT NULL COMMENT 'date de transmission du dossier';
ALTER TABLE `candidature` ADD COLUMN `dat_recept_dossier_cand` DATE NULL DEFAULT NULL  COMMENT 'date de réception du dossier';
update candidature set dat_recept_dossier_cand = dat_trans_dossier_cand;