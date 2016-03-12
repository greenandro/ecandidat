ALTER TABLE `parametre`	CHANGE COLUMN `cod_param` `cod_param` VARCHAR(30) NOT NULL COMMENT 'code du paramètre';
ALTER TABLE `candidature` DROP COLUMN `dat_opi_cand`;
ALTER TABLE `siscol_typ_resultat` COMMENT='Rérérentiel SiScol : Types de résultats';