ALTER TABLE `siscol_etablissement` DROP INDEX `ETB_FK_DEP_01`, ADD INDEX `fk_siscol_departement_etab_cod_dep` (`cod_dep`);