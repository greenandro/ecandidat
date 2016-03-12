ALTER TABLE `candidature` CHANGE COLUMN `dat_mod_typ_statut_cand` `dat_mod_typ_statut_cand` TIMESTAMP NULL DEFAULT NULL COMMENT 'date de modif du statut du dossier';
ALTER TABLE `siscol_commune` DROP INDEX `COM_FK_DEP_01`, ADD INDEX `fk_siscol_departement_commune_cod_dep` (`cod_dep`);
ALTER TABLE `siscol_utilisateur` DROP FOREIGN KEY `fk_siscol_centre_gestion_utilisateur_cod_cge`;
ALTER TABLE `siscol_utilisateur` ADD CONSTRAINT `fk_siscol_centre_gestion_utilisateur_cod_cge` FOREIGN KEY (`cod_cge`) REFERENCES `siscol_centre_gestion` (`cod_cge`) ON UPDATE CASCADE;