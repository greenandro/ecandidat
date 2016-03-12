ALTER TABLE `formulaire_cand` DROP COLUMN `tem_concern_formulaire_cand`;
update formulaire_cand set cod_typ_statut_piece = 'TR' where cod_typ_statut_piece is null;
ALTER TABLE `formulaire_cand` MODIFY `cod_typ_statut_piece` VARCHAR(2) NOT NULL COMMENT 'statut de la pièce';
ALTER TABLE `pj_cand` DROP COLUMN `tem_concern_pj_cand`;
update pj_cand set cod_typ_statut_piece = 'TR' where cod_typ_statut_piece is null;
ALTER TABLE `pj_cand` MODIFY `cod_typ_statut_piece` VARCHAR(2) NOT NULL COMMENT 'statut de la pièce';