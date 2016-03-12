ALTER TABLE `batch_run` ALTER `dat_last_check_run` DROP DEFAULT;
ALTER TABLE `candidature` ALTER `dat_cre_cand` DROP DEFAULT;
ALTER TABLE `candidature` ALTER `dat_mod_cand` DROP DEFAULT;

ALTER TABLE `centre_candidature` ALTER `dat_cre_ctr_cand` DROP DEFAULT;
ALTER TABLE `centre_candidature` ALTER `dat_mod_ctr_cand` DROP DEFAULT;

ALTER TABLE `commission` ALTER `dat_cre_comm` DROP DEFAULT;
ALTER TABLE `commission` ALTER `dat_mod_comm` DROP DEFAULT;

ALTER TABLE `compte_minima` ALTER `dat_fin_valid_cpt_min` DROP DEFAULT;
ALTER TABLE `compte_minima` ALTER `dat_cre_cpt_min` DROP DEFAULT;

ALTER TABLE `droit_profil` ALTER `dat_cre_profil` DROP DEFAULT;
ALTER TABLE `droit_profil` ALTER `dat_mod_profil` DROP DEFAULT;

ALTER TABLE `fichier` ALTER `dat_cre_fichier` DROP DEFAULT;

ALTER TABLE `formation` ALTER `dat_cre_form` DROP DEFAULT;
ALTER TABLE `formation` ALTER `dat_mod_form` DROP DEFAULT;

ALTER TABLE `formulaire` ALTER `dat_cre_formulaire` DROP DEFAULT;
ALTER TABLE `formulaire` ALTER `dat_mod_formulaire` DROP DEFAULT;

ALTER TABLE `formulaire_cand` ALTER `dat_cre_formulaire_cand` DROP DEFAULT;
ALTER TABLE `formulaire_cand` ALTER `dat_mod_formulaire_cand` DROP DEFAULT;

ALTER TABLE `load_balancing_reload` ALTER `dat_cre_lb_reload` DROP DEFAULT;
ALTER TABLE `load_balancing_reload_run` ALTER `dat_last_check_lb_reload_run` DROP DEFAULT;

ALTER TABLE `mail` ALTER `dat_cre_mail` DROP DEFAULT;
ALTER TABLE `mail` ALTER `dat_mod_mail` DROP DEFAULT;

ALTER TABLE `motivation_avis` ALTER `dat_cre_motiv` DROP DEFAULT;
ALTER TABLE `motivation_avis` ALTER `dat_mod_motiv` DROP DEFAULT;

ALTER TABLE `opi` ALTER `dat_cre_opi` DROP DEFAULT;

ALTER TABLE `piece_justif` ALTER `dat_cre_pj` DROP DEFAULT;
ALTER TABLE `piece_justif` ALTER `dat_mod_pj` DROP DEFAULT;

ALTER TABLE `pj_cand` ALTER `dat_cre_pj_cand` DROP DEFAULT;
ALTER TABLE `pj_cand` ALTER `dat_mod_pj_cand` DROP DEFAULT;

ALTER TABLE `type_decision` ALTER `dat_cre_typ_dec` DROP DEFAULT;
ALTER TABLE `type_decision` ALTER `dat_mod_typ_dec` DROP DEFAULT;

ALTER TABLE `type_decision_candidature` ALTER `dat_cre_type_dec_cand` DROP DEFAULT;

ALTER TABLE `motivation_avis` ALTER `dat_cre_motiv` DROP DEFAULT;
ALTER TABLE `motivation_avis` ALTER `dat_mod_motiv` DROP DEFAULT;

ALTER TABLE `version` ALTER `dat_version` DROP DEFAULT;