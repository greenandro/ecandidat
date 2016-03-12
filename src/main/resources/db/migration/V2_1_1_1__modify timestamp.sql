ALTER TABLE `batch`	CHANGE COLUMN `last_dat_execution_batch` `last_dat_execution_batch` DATETIME NULL DEFAULT NULL COMMENT 'date de la deniere execution';
ALTER TABLE `batch_histo` CHANGE COLUMN `date_deb_batch_histo` `date_deb_batch_histo` DATETIME NULL DEFAULT NULL COMMENT 'date de début du batch', CHANGE COLUMN `date_fin_batch_histo` `date_fin_batch_histo` DATETIME NULL DEFAULT NULL COMMENT 'date de fin de lancement';
ALTER TABLE `batch_run`	ALTER `dat_last_check_run` DROP DEFAULT;
ALTER TABLE `batch_run`	CHANGE COLUMN `dat_last_check_run` `dat_last_check_run` DATETIME NOT NULL COMMENT 'valeur du dernier run de batch';
ALTER TABLE `campagne` CHANGE COLUMN `dat_activat_prev_camp` `dat_activat_prev_camp` DATETIME NULL DEFAULT NULL COMMENT 'date time d''activation prévisionnel';
ALTER TABLE `campagne` CHANGE COLUMN `dat_activat_effec_camp` `dat_activat_effec_camp` DATETIME NULL DEFAULT NULL COMMENT 'date time d''activation effectif' AFTER `dat_activat_prev_camp`;
ALTER TABLE `campagne`	CHANGE COLUMN `dat_archiv_camp` `dat_archiv_camp` DATETIME NULL DEFAULT NULL COMMENT 'date d''archivage effectif' AFTER `dat_activat_effec_camp`;
ALTER TABLE `campagne`	CHANGE COLUMN `dat_destruct_effec_camp` `dat_destruct_effec_camp` DATETIME NULL DEFAULT NULL COMMENT 'date time de destruction des dossier effectif' AFTER `dat_archiv_camp`;
ALTER TABLE `candidature` ALTER `dat_cre_cand` DROP DEFAULT, ALTER `dat_mod_cand` DROP DEFAULT;
ALTER TABLE `candidature` CHANGE COLUMN `dat_cre_cand` `dat_cre_cand` DATETIME NOT NULL COMMENT 'date de création' AFTER `tem_proposition_cand`;
ALTER TABLE `candidature` CHANGE COLUMN `dat_mod_cand` `dat_mod_cand` DATETIME NOT NULL COMMENT 'date de modification' AFTER `user_cre_cand`;
ALTER TABLE `candidature` CHANGE COLUMN `dat_annul_cand` `dat_annul_cand` DATETIME NULL DEFAULT NULL COMMENT 'date d''annulation de la candidature' AFTER `user_mod_cand`;
ALTER TABLE `candidature` CHANGE COLUMN `dat_accept_cand` `dat_accept_cand` DATETIME NULL DEFAULT NULL COMMENT 'date d''acceptation ou de refus de la candidature' AFTER `tem_accept_cand`;
ALTER TABLE `candidature` CHANGE COLUMN `dat_opi_cand` `dat_opi_cand` DATETIME NULL DEFAULT NULL COMMENT 'date de l''opi pour cette candidature' AFTER `user_accept_cand`;
ALTER TABLE `candidature` CHANGE COLUMN `dat_mod_typ_statut_cand` `dat_mod_typ_statut_cand` DATETIME NULL DEFAULT NULL COMMENT 'date de modif du statut du dossier' AFTER `dat_opi_cand`;
ALTER TABLE `candidature` CHANGE COLUMN `dat_trans_dossier_cand` `dat_trans_dossier_cand` DATETIME NULL DEFAULT NULL COMMENT 'date de transmission du dossier' AFTER `dat_mod_typ_statut_cand`;
ALTER TABLE `centre_candidature` ALTER `dat_cre_ctr_cand` DROP DEFAULT, ALTER `dat_mod_ctr_cand` DROP DEFAULT;
ALTER TABLE `centre_candidature` CHANGE COLUMN `dat_cre_ctr_cand` `dat_cre_ctr_cand` TIMESTAMP NOT NULL COMMENT 'date de création' AFTER `tes_ctr_cand`, CHANGE COLUMN `dat_mod_ctr_cand` `dat_mod_ctr_cand` TIMESTAMP NOT NULL COMMENT 'date de modification' AFTER `user_cre_ctr_cand`;
ALTER TABLE `commission` ALTER `dat_cre_comm` DROP DEFAULT,	ALTER `dat_mod_comm` DROP DEFAULT;
ALTER TABLE `commission` CHANGE COLUMN `dat_cre_comm` `dat_cre_comm` DATETIME NOT NULL COMMENT 'date de création' AFTER `tes_comm`,	CHANGE COLUMN `dat_mod_comm` `dat_mod_comm` DATETIME NOT NULL COMMENT 'date de modification' AFTER `user_cre_comm`;
ALTER TABLE `compte_minima`	ALTER `dat_fin_valid_cpt_min` DROP DEFAULT,	ALTER `dat_cre_cpt_min` DROP DEFAULT;
ALTER TABLE `compte_minima`	CHANGE COLUMN `dat_fin_valid_cpt_min` `dat_fin_valid_cpt_min` DATETIME NOT NULL COMMENT 'date de fin de validite du compte a minima' AFTER `tem_valid_mail_cpt_min`, CHANGE COLUMN `dat_cre_cpt_min` `dat_cre_cpt_min` DATETIME NOT NULL COMMENT 'date de création' AFTER `dat_fin_valid_cpt_min`;
ALTER TABLE `droit_profil`	ALTER `dat_cre_profil` DROP DEFAULT, ALTER `dat_mod_profil` DROP DEFAULT;
ALTER TABLE `droit_profil` CHANGE COLUMN `dat_cre_profil` `dat_cre_profil` DATETIME NOT NULL COMMENT 'date de création' AFTER `tes_profil`,	CHANGE COLUMN `dat_mod_profil` `dat_mod_profil` DATETIME NOT NULL COMMENT 'date de modification' AFTER `user_cre_profil`;
ALTER TABLE `fichier` ALTER `dat_cre_fichier` DROP DEFAULT;
ALTER TABLE `fichier` CHANGE COLUMN `dat_cre_fichier` `dat_cre_fichier` DATETIME NOT NULL COMMENT 'date de création' AFTER `auteur_fichier`;
ALTER TABLE `formation`	ALTER `dat_cre_form` DROP DEFAULT, ALTER `dat_mod_form` DROP DEFAULT;
ALTER TABLE `formation`	CHANGE COLUMN `dat_cre_form` `dat_cre_form` DATETIME NOT NULL COMMENT 'date de création' AFTER `tes_form`, CHANGE COLUMN `dat_mod_form` `dat_mod_form` DATETIME NOT NULL COMMENT 'date de modification' AFTER `user_cre_form`;
ALTER TABLE `formulaire` ALTER `dat_cre_formulaire` DROP DEFAULT, ALTER `dat_mod_formulaire` DROP DEFAULT;
ALTER TABLE `formulaire` CHANGE COLUMN `dat_cre_formulaire` `dat_cre_formulaire` DATETIME NOT NULL COMMENT 'date de création' AFTER `id_ctr_cand`, CHANGE COLUMN `dat_mod_formulaire` `dat_mod_formulaire` DATETIME NOT NULL COMMENT 'date de modification' AFTER `user_cre_formulaire`;
ALTER TABLE `formulaire_cand` ALTER `dat_cre_formulaire_cand` DROP DEFAULT,	ALTER `dat_mod_formulaire_cand` DROP DEFAULT;
ALTER TABLE `formulaire_cand` CHANGE COLUMN `dat_cre_formulaire_cand` `dat_cre_formulaire_cand` DATETIME NOT NULL COMMENT 'date de création' AFTER `cod_typ_statut_piece`;
ALTER TABLE `formulaire_cand` CHANGE COLUMN `dat_reponse_formulaire_cand` `dat_reponse_formulaire_cand` DATETIME NULL DEFAULT NULL COMMENT 'date de réponse du formulaire' AFTER `user_cre_formulaire_cand`;
ALTER TABLE `formulaire_cand` CHANGE COLUMN `dat_mod_formulaire_cand` `dat_mod_formulaire_cand` DATETIME NOT NULL COMMENT 'date de modification' AFTER `dat_reponse_formulaire_cand`;
ALTER TABLE `load_balancing_reload`	ALTER `dat_cre_lb_reload` DROP DEFAULT;
ALTER TABLE `load_balancing_reload`	CHANGE COLUMN `dat_cre_lb_reload` `dat_cre_lb_reload` DATETIME NOT NULL COMMENT 'date de création du chargement' AFTER `cod_data_lb_reload`;
ALTER TABLE `load_balancing_reload_run`	ALTER `dat_last_check_lb_reload_run` DROP DEFAULT;
ALTER TABLE `load_balancing_reload_run`	CHANGE COLUMN `dat_last_check_lb_reload_run` `dat_last_check_lb_reload_run` DATETIME NOT NULL COMMENT 'deniere verification par instance';
ALTER TABLE `mail` ALTER `dat_cre_mail` DROP DEFAULT, ALTER `dat_mod_mail` DROP DEFAULT;
ALTER TABLE `mail` CHANGE COLUMN `dat_cre_mail` `dat_cre_mail` DATETIME NOT NULL COMMENT 'date de création' AFTER `id_i18n_corps_mail`,	CHANGE COLUMN `dat_mod_mail` `dat_mod_mail` DATETIME NOT NULL COMMENT 'date de modification' AFTER `user_cre_mail`;
ALTER TABLE `motivation_avis` ALTER `dat_cre_motiv` DROP DEFAULT, ALTER `dat_mod_motiv` DROP DEFAULT;
ALTER TABLE `motivation_avis` CHANGE COLUMN `dat_cre_motiv` `dat_cre_motiv` DATETIME NOT NULL COMMENT 'date de création' AFTER `tes_motiv`,	CHANGE COLUMN `dat_mod_motiv` `dat_mod_motiv` DATETIME NOT NULL COMMENT 'date de modification' AFTER `user_cre_motiv`;
ALTER TABLE `opi` ALTER `dat_cre_opi` DROP DEFAULT;
ALTER TABLE `opi` CHANGE COLUMN `dat_cre_opi` `dat_cre_opi` DATETIME NOT NULL COMMENT 'date de création' AFTER `id_cand`, CHANGE COLUMN `dat_passage_opi` `dat_passage_opi` DATETIME NULL DEFAULT NULL COMMENT 'date de création de l''opi' AFTER `dat_cre_opi`;
ALTER TABLE `piece_justif`	ALTER `dat_cre_pj` DROP DEFAULT, ALTER `dat_mod_pj` DROP DEFAULT;
ALTER TABLE `piece_justif` CHANGE COLUMN `dat_cre_pj` `dat_cre_pj` DATETIME NOT NULL COMMENT 'date de création' AFTER `id_ctr_cand`, CHANGE COLUMN `dat_mod_pj` `dat_mod_pj` DATETIME NOT NULL COMMENT 'date de modification' AFTER `user_cre_pj`;
ALTER TABLE `pj_cand` ALTER `dat_cre_pj_cand` DROP DEFAULT, ALTER `dat_mod_pj_cand` DROP DEFAULT;
ALTER TABLE `pj_cand` CHANGE COLUMN `dat_cre_pj_cand` `dat_cre_pj_cand` DATETIME NOT NULL COMMENT 'date de création' AFTER `comment_pj_cand`, CHANGE COLUMN `dat_mod_pj_cand` `dat_mod_pj_cand` DATETIME NOT NULL COMMENT 'date de modification' AFTER `user_cre_pj_cand`,	CHANGE COLUMN `dat_mod_statut_pj_cand` `dat_mod_statut_pj_cand` DATETIME NULL DEFAULT NULL COMMENT 'date de modif du statut de la piece' AFTER `user_mod_pj_cand`;
ALTER TABLE `type_decision`	ALTER `dat_cre_typ_dec` DROP DEFAULT, ALTER `dat_mod_typ_dec` DROP DEFAULT;
ALTER TABLE `type_decision`	CHANGE COLUMN `dat_cre_typ_dec` `dat_cre_typ_dec` DATETIME NOT NULL COMMENT 'date de création' AFTER `tem_model_typ_dec`, CHANGE COLUMN `dat_mod_typ_dec` `dat_mod_typ_dec` DATETIME NOT NULL COMMENT 'date de modification' AFTER `user_cre_typ_dec`;
ALTER TABLE `type_decision_candidature`	ALTER `dat_cre_type_dec_cand` DROP DEFAULT;
ALTER TABLE `type_decision_candidature`	CHANGE COLUMN `dat_cre_type_dec_cand` `dat_cre_type_dec_cand` DATETIME NOT NULL COMMENT 'date de création' AFTER `tem_appel_type_dec_cand`,	CHANGE COLUMN `dat_valid_type_dec_cand` `dat_valid_type_dec_cand` DATETIME NULL DEFAULT NULL COMMENT 'date de validation' AFTER `tem_valid_type_dec_cand`;
ALTER TABLE `type_statut` CHANGE COLUMN `dat_mod_typ_statut` `dat_mod_typ_statut` DATETIME NULL DEFAULT NULL COMMENT 'date de modification' AFTER `id_i18n_lib_typ_statut`;
ALTER TABLE `type_statut_piece` CHANGE COLUMN `dat_mod_typ_statut_piece` `dat_mod_typ_statut_piece` DATETIME NULL DEFAULT NULL COMMENT 'date de modification' AFTER `id_i18n_lib_typ_statut_piece`;
ALTER TABLE `type_traitement` CHANGE COLUMN `dat_mod_typ_trait` `dat_mod_typ_trait` DATETIME NULL DEFAULT NULL COMMENT 'date de modification' AFTER `id_i18n_lib_typ_trait`;
ALTER TABLE `version` ALTER `dat_version` DROP DEFAULT;
ALTER TABLE `version`CHANGE COLUMN `dat_version` `dat_version` DATETIME NOT NULL COMMENT 'date de la version' AFTER `val_version`;
ALTER TABLE `centre_candidature` ALTER `dat_cre_ctr_cand` DROP DEFAULT,	ALTER `dat_mod_ctr_cand` DROP DEFAULT;
ALTER TABLE `centre_candidature` CHANGE COLUMN `dat_cre_ctr_cand` `dat_cre_ctr_cand` DATETIME NOT NULL COMMENT 'date de création' AFTER `tes_ctr_cand`, CHANGE COLUMN `dat_mod_ctr_cand` `dat_mod_ctr_cand` DATETIME NOT NULL COMMENT 'date de modification' AFTER `user_cre_ctr_cand`;