ALTER TABLE `candidature` ADD COLUMN `dat_recept_dossier_cand` TIMESTAMP NULL DEFAULT NULL COMMENT 'date de réception du dossier' AFTER `dat_mod_typ_statut_cand`;