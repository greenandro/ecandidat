ALTER TABLE `centre_candidature` MODIFY `lib_ctr_cand` VARCHAR(200) NOT NULL COMMENT 'libellé du centre de candidature';
ALTER TABLE `individu` MODIFY `login_ind` VARCHAR(20) NOT NULL COMMENT 'login de l''individu';
ALTER TABLE `droit_profil_ind` MODIFY `login_ind` VARCHAR(20) NOT NULL COMMENT 'login de l''individu';
ALTER TABLE `formation` MODIFY `lib_form` VARCHAR(200) NOT NULL comment 'libellé eCandidat de la formation';
ALTER TABLE `compte_minima` MODIFY `nom_cpt_min` VARCHAR(50) NOT NULL comment 'nom du compte à minima';
ALTER TABLE `compte_minima` MODIFY `prenom_cpt_min` VARCHAR(50) NOT NULL comment 'prénom du compte à minima';
ALTER TABLE `commission` MODIFY `comment_retour_comm` VARCHAR(500) comment 'commentaire lors du retour de dossier pour la commission';
ALTER TABLE `commission` MODIFY `lib_comm` VARCHAR(200) NOT NULL comment 'libellé de la commission';
ALTER TABLE `candidat_cursus_post_bac` MODIFY `lib_cursus` VARCHAR(255) NOT NULL comment 'libellé du cursus';