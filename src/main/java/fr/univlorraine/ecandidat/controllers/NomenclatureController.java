package fr.univlorraine.ecandidat.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Batch;
import fr.univlorraine.ecandidat.entities.ecandidat.Civilite;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.I18nTraduction;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.entities.ecandidat.SchemaVersion;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraduction;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;
import fr.univlorraine.ecandidat.repositories.BatchRepository;
import fr.univlorraine.ecandidat.repositories.CiviliteRepository;
import fr.univlorraine.ecandidat.repositories.DroitFonctionnaliteRepository;
import fr.univlorraine.ecandidat.repositories.DroitProfilRepository;
import fr.univlorraine.ecandidat.repositories.I18nRepository;
import fr.univlorraine.ecandidat.repositories.I18nTraductionRepository;
import fr.univlorraine.ecandidat.repositories.LangueRepository;
import fr.univlorraine.ecandidat.repositories.MailRepository;
import fr.univlorraine.ecandidat.repositories.ParametreRepository;
import fr.univlorraine.ecandidat.repositories.SchemaVersionRepository;
import fr.univlorraine.ecandidat.repositories.TypeAvisRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionRepository;
import fr.univlorraine.ecandidat.repositories.TypeStatutPieceRepository;
import fr.univlorraine.ecandidat.repositories.TypeStatutRepository;
import fr.univlorraine.ecandidat.repositories.TypeTraductionRepository;
import fr.univlorraine.ecandidat.repositories.TypeTraitementRepository;
import fr.univlorraine.ecandidat.repositories.VersionRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.views.windows.AdminNomenclatureWindow;
import gouv.education.apogee.commun.client.utils.WSUtils;


/**Gestion des nomenclatures
 * @author Kevin Hergalant
 */
@Component
public class NomenclatureController {
	private Logger logger = LoggerFactory.getLogger(NomenclatureController.class);
	
	/* Injections */
	@Resource
	private transient Environment environment;
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient DemoController demoController;
	@Resource
	private transient TypeAvisRepository typeAvisRepository;
	@Resource
	private transient TypeStatutPieceRepository typeStatutPieceRepository;
	@Resource
	private transient TypeTraitementRepository typeTraitementRepository;
	@Resource
	private transient BatchRepository batchRepository;
	@Resource
	private transient LangueRepository langueRepository;
	@Resource
	private transient TypeTraductionRepository typeTraductionRepository;
	@Resource
	private transient DroitProfilRepository droitProfilRepository;
	@Resource
	private transient MailRepository mailRepository;
	@Resource
	private transient I18nRepository i18nRepository;
	@Resource
	private transient I18nTraductionRepository i18nTraductionRepository;
	@Resource
	private transient TypeDecisionRepository typeDecisionRepository;
	@Resource
	private transient TypeStatutRepository typeStatutRepository;
	@Resource
	private transient DroitFonctionnaliteRepository droitFonctionnaliteRepository;
	@Resource
	private transient ParametreRepository parametreRepository;
	@Resource
	private transient VersionRepository versionRepository;
	@Resource
	private transient SchemaVersionRepository schemaVersionRepository;
	@Resource
	private transient CiviliteRepository civiliteRepository;
	
	/** La version de la nomenclature
	 * @return la version
	 */
	public Version getNomenclatureVersionDb(){
		Version versionNomenclature = versionRepository.findOne(NomenclatureUtils.VERSION_NOMENCLATURE_COD);
		if (versionNomenclature == null){
			return new Version(NomenclatureUtils.VERSION_NOMENCLATURE_COD,NomenclatureUtils.VERSION_NO_VERSION_VAL); 
		}
		return versionNomenclature;
	}
	
	/** La version courante de nomenclature
	 * @return la version courante
	 */
	public Version getNomenclatureVersionCourante(){
		return new Version(NomenclatureUtils.VERSION_NOMENCLATURE_COD,NomenclatureUtils.VERSION_NOMENCLATURE_VAL);
	}
	
	/** Savoir si on doit recharger les paramètres
	 * @return true si la nomenclature doit etre rechargee
	 */
	public Boolean isNomenclatureToReload(){
		Version versionNomenclature = getNomenclatureVersionDb();
		if (versionNomenclature == null || !versionNomenclature.getValVersion().equals(NomenclatureUtils.VERSION_NOMENCLATURE_VAL)){
			return true;
		}
		return false;
	}
	
	/**
	 * Ouvre une fenêtre de vérification de nomenclature.
	 */
	public void checkNomenclature() {
		UI.getCurrent().addWindow(new AdminNomenclatureWindow());
	}
	
	/** Retourne un numéro de version
	 * @param codVersion
	 * @return la version
	 */
	public Version getVersion(String codVersion){
		return versionRepository.findOne(codVersion);
	}
	
	/** Retourne un numéro de version de la db
	 * @param codVersion
	 * @return la version de la bd
	 */
	public Version getDbVersion(String codVersion){
		List<SchemaVersion> list = schemaVersionRepository.findFirst1BySuccessOrderByVersionRankDesc(true);		
		Version v = new Version(NomenclatureUtils.VERSION_DB,null);
		if (list.size()>0){
			SchemaVersion s = list.get(0);
			v.setDatVersion(s.getInstalledOn());
			v.setValVersion(s.getVersion());
		}
		return v;
	}
	
	/**
	 * Met à jour la nomenclature de eCandidat
	 */
	public void majNomenclature(Boolean batchMode){
		Locale locale;
		if (batchMode){
			locale = new Locale("fr");
		}else{
			locale = UI.getCurrent().getLocale();
		}
		
		typeAvisRepository.saveAndFlush(new TypeAvis(NomenclatureUtils.TYP_AVIS_FAV,applicationContext.getMessage("nomenclature.typavis.fa", null, locale)));
		typeAvisRepository.saveAndFlush(new TypeAvis(NomenclatureUtils.TYP_AVIS_DEF,applicationContext.getMessage("nomenclature.typavis.de", null, locale)));
		typeAvisRepository.saveAndFlush(new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_COMP,applicationContext.getMessage("nomenclature.typavis.lc", null, locale)));
		typeAvisRepository.saveAndFlush(new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_ATTENTE,applicationContext.getMessage("nomenclature.typavis.la", null, locale)));
		typeAvisRepository.saveAndFlush(new TypeAvis(NomenclatureUtils.TYP_AVIS_PRESELECTION,applicationContext.getMessage("nomenclature.typavis.pr", null, locale)));		
		
		civiliteRepository.saveAndFlush(new Civilite(applicationContext.getMessage("nomenclature.civilite.monsieur.cod", null, locale),applicationContext.getMessage("nomenclature.civilite.monsieur.lib", null, locale),NomenclatureUtils.CIVILITE_APO_M));
		civiliteRepository.saveAndFlush(new Civilite(applicationContext.getMessage("nomenclature.civilite.mme.cod", null, locale),applicationContext.getMessage("nomenclature.civilite.mme.lib", null, locale),NomenclatureUtils.CIVILITE_APO_F));
		
		majLangue(new Langue(NomenclatureUtils.LANGUE_FR,applicationContext.getMessage("nomenclature.langue.fr", null, locale),true,true));
		majLangue(new Langue(NomenclatureUtils.LANGUE_EN,applicationContext.getMessage("nomenclature.langue.en", null, locale),false,false));
		majLangue(new Langue(NomenclatureUtils.LANGUE_ES,applicationContext.getMessage("nomenclature.langue.es", null, locale),false,false));
		majLangue(new Langue(NomenclatureUtils.LANGUE_DE,applicationContext.getMessage("nomenclature.langue.de", null, locale),false,false));
		
		majBatch(new Batch(NomenclatureUtils.BATCH_SI_SCOL,applicationContext.getMessage("nomenclature.batch.apo.libelle", null, locale),false,true,23,00));
		majBatch(new Batch(NomenclatureUtils.BATCH_APP_EN_MAINT,applicationContext.getMessage("nomenclature.batch.maintenance", null, locale),false,true,22,55));
		majBatch(new Batch(NomenclatureUtils.BATCH_APP_EN_SERVICE,applicationContext.getMessage("nomenclature.batch.enservice", null, locale),false,true,23,30));
		majBatch(new Batch(NomenclatureUtils.BATCH_NETTOYAGE_CPT,applicationContext.getMessage("nomenclature.batch.cptmin", null, locale),false,true,22,30));
		majBatch(new Batch(NomenclatureUtils.BATCH_NETTOYAGE,applicationContext.getMessage("nomenclature.batch.netoyage.libelle", null, locale),false,true,true,true,true,true,true,true,true,22,00));
		majBatch(new Batch(NomenclatureUtils.BATCH_ARCHIVAGE,applicationContext.getMessage("nomenclature.batch.archivage", null, locale),false,true,22,30));
		majBatch(new Batch(NomenclatureUtils.BATCH_SYNC_LIMESURVEY,applicationContext.getMessage("nomenclature.batch.limesurvey", null, locale),false,true,22,30));
		majBatch(new Batch(NomenclatureUtils.BATCH_DESTRUCT_DOSSIER,applicationContext.getMessage("nomenclature.batch.destruct", null, locale),false,true,22,30));
		majBatch(new Batch(NomenclatureUtils.BATCH_ASYNC_OPI,applicationContext.getMessage("nomenclature.batch.async.opi", null, locale),false,true,22,30));
		majBatch(new Batch(NomenclatureUtils.BATCH_DESTRUCT_HISTO,applicationContext.getMessage("nomenclature.batch.keep.histo", null, locale),false,true,true,true,true,true,true,true,true,23,00));
		
		if (demoController.getDemoMode()){
			majBatch(new Batch(NomenclatureUtils.BATCH_DEMO,applicationContext.getMessage("nomenclature.batch.demo.libelle", null, locale),false,true,true,true,true,true,true,true,true,23,55));	
		}
				
		
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_FORM_LIB,applicationContext.getMessage("nomenclature.typtrad.formLib", null, locale),500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_FORM_URL,applicationContext.getMessage("nomenclature.typtrad.formUrl", null, locale),500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_MAIL_SUJET,applicationContext.getMessage("nomenclature.typtrad.mailSujet", null, locale),500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_MAIL_CORPS,applicationContext.getMessage("nomenclature.typtrad.mailCorps", null, locale),5000));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_MOTIV_LIB,applicationContext.getMessage("nomenclature.typtrad.motivLib", null, locale),500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_PJ_LIB,applicationContext.getMessage("nomenclature.typtrad.pjLib", null, locale),500));
		/*typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_PJ_FILE,applicationContext.getMessage("nomenclature.typtrad.pjFile", null, locale),500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_PJ_LIB_FILE,applicationContext.getMessage("nomenclature.typtrad.pjLib_file", null, locale),500));*/
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_TYP_DEC_LIB,applicationContext.getMessage("nomenclature.typtrad.typDecLib", null, locale),500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_TYP_TRAIT_LIB,applicationContext.getMessage("nomenclature.typtrad.typTraitLib", null, locale),500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_TYP_STATUT,applicationContext.getMessage("nomenclature.typtrad.typStatut", null, locale),500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_TYP_STATUT_PIECE,applicationContext.getMessage("nomenclature.typtrad.typStatutPiece", null, locale),500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_FAQ_QUESTION,applicationContext.getMessage("nomenclature.typtrad.faq.question", null, locale),500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_FAQ_REPONSE,applicationContext.getMessage("nomenclature.typtrad.faq.reponse", null, locale),5000));
		
		
		majDroitProfil(new DroitProfil(NomenclatureUtils.DROIT_PROFIL_ADMIN,applicationContext.getMessage("nomenclature.droitProfil.admin", null, locale),NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,true, false,false,true));
		majDroitProfil(new DroitProfil(NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE,applicationContext.getMessage("nomenclature.droitProfil.scolcentrale", null, locale),NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,true,false,false,true));
		DroitProfil profilCtrCand = majDroitProfil(new DroitProfil(NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE,applicationContext.getMessage("nomenclature.droitProfil.centrecand", null, locale),NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,false,true,false,true));
		majDroitProfil(new DroitProfil(NomenclatureUtils.DROIT_PROFIL_COMMISSION,applicationContext.getMessage("nomenclature.droitProfil.commission", null, locale),NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,false,false,false,true));
		
		/*L'admin*/
		//droitUserProfilRepository.saveAndFlush(new DroitUserProfil(environment.getRequiredProperty("admin.technique"),environment.getRequiredProperty("admin.technique"),profilAdmin));		
		
		/*Fonctionnalites*/
		majDroitProfilFonc(profilCtrCand,new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_PARAM,applicationContext.getMessage("nomenclature.fonctionnalite.param", null, locale)));
		majDroitProfilFonc(profilCtrCand,new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FORMATION,applicationContext.getMessage("nomenclature.fonctionnalite.gestFormation", null, locale)));
		majDroitProfilFonc(profilCtrCand,new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_COMMISSION,applicationContext.getMessage("nomenclature.fonctionnalite.gestCommission", null, locale)));
		majDroitProfilFonc(profilCtrCand,new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_PJ,applicationContext.getMessage("nomenclature.fonctionnalite.gestPj", null, locale)));
		majDroitProfilFonc(profilCtrCand,new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FORMULAIRE,applicationContext.getMessage("nomenclature.fonctionnalite.gestFormulaire", null, locale)));
		majDroitProfilFonc(profilCtrCand,new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_EDIT_TYPTRAIT,applicationContext.getMessage("nomenclature.fonctionnalite.editTypTrait", null, locale)));
		majDroitProfilFonc(profilCtrCand,new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_VALID_TYPTRAIT,applicationContext.getMessage("nomenclature.fonctionnalite.validTypTrait", null, locale)));
		majDroitProfilFonc(profilCtrCand,new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_EDIT_AVIS,applicationContext.getMessage("nomenclature.fonctionnalite.editAvis", null, locale)));
		majDroitProfilFonc(profilCtrCand,new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_VALID_AVIS,applicationContext.getMessage("nomenclature.fonctionnalite.validAvis", null, locale)));
		majDroitProfilFonc(profilCtrCand,new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE,applicationContext.getMessage("nomenclature.fonctionnalite.gestCand", null, locale)));

		/*Les mail de decision*/
		Mail mailDecisionFav = majMail(new Mail(NomenclatureUtils.MAIL_DEC_FAVORABLE,applicationContext.getMessage("nomenclature.mail.decision.favorable", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,new TypeAvis(NomenclatureUtils.TYP_AVIS_FAV)),applicationContext.getMessage("nomenclature.mail.decision.favorable.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.decision.favorable.content", null, locale));
		Mail mailDecisionDef = majMail(new Mail(NomenclatureUtils.MAIL_DEC_DEFAVORABLE,applicationContext.getMessage("nomenclature.mail.decision.defavorable", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,new TypeAvis(NomenclatureUtils.TYP_AVIS_DEF)),applicationContext.getMessage("nomenclature.mail.decision.defavorable.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.decision.defavorable.content", null, locale));
		Mail mailDecisionListAtt = majMail(new Mail(NomenclatureUtils.MAIL_DEC_LISTE_ATT,applicationContext.getMessage("nomenclature.mail.decision.listeAtt", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_ATTENTE)),applicationContext.getMessage("nomenclature.mail.decision.listeAtt.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.decision.listeAtt.content", null, locale));
		Mail mailDecisionListeComp = majMail(new Mail(NomenclatureUtils.MAIL_DEC_LISTE_COMP,applicationContext.getMessage("nomenclature.mail.decision.listeComp", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_COMP)),applicationContext.getMessage("nomenclature.mail.decision.listeComp.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.decision.listeComp.content", null, locale));
		Mail mailDecisionPres = majMail(new Mail(NomenclatureUtils.MAIL_DEC_PRESELECTION,applicationContext.getMessage("nomenclature.mail.decision.preselection", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,new TypeAvis(NomenclatureUtils.TYP_AVIS_PRESELECTION)),applicationContext.getMessage("nomenclature.mail.decision.preselection.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.decision.preselection.content", null, locale));
		
		/*Type de traitement*/
		majTypeTraitement(new TypeTraitement(NomenclatureUtils.TYP_TRAIT_AC,applicationContext.getMessage("nomenclature.typtrait.ac", null, locale),true));
		majTypeTraitement(new TypeTraitement(NomenclatureUtils.TYP_TRAIT_AD,applicationContext.getMessage("nomenclature.typtrait.ad", null, locale),true));
		majTypeTraitement(new TypeTraitement(NomenclatureUtils.TYP_TRAIT_AT,applicationContext.getMessage("nomenclature.typtrait.at", null, locale),false));
		
		/*Type de decision*/
		majTypeDec(new TypeDecision(NomenclatureUtils.TYP_DEC_FAVORABLE,applicationContext.getMessage("nomenclature.typDec.favorable", null, locale),true,true,true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,new TypeAvis(NomenclatureUtils.TYP_AVIS_FAV),mailDecisionFav));
		majTypeDec(new TypeDecision(NomenclatureUtils.TYP_DEC_DEFAVORABLE,applicationContext.getMessage("nomenclature.typDec.defavorable", null, locale),true,true,false,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,new TypeAvis(NomenclatureUtils.TYP_AVIS_DEF),mailDecisionDef));
		majTypeDec(new TypeDecision(NomenclatureUtils.TYP_DEC_LISTE_ATT,applicationContext.getMessage("nomenclature.typDec.listeAtt", null, locale),true,false,false,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_ATTENTE),mailDecisionListAtt));
		majTypeDec(new TypeDecision(NomenclatureUtils.TYP_DEC_LISTE_COMP,applicationContext.getMessage("nomenclature.typDec.listeComp", null, locale),true,false,false,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_COMP),mailDecisionListeComp));
		majTypeDec(new TypeDecision(NomenclatureUtils.TYP_DEC_PRESELECTION,applicationContext.getMessage("nomenclature.typDec.preselection", null, locale),true,false,false,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,new TypeAvis(NomenclatureUtils.TYP_AVIS_PRESELECTION),mailDecisionPres));

		/*Type de statut de dossier*/
		majTypeStatut(new TypeStatut(NomenclatureUtils.TYPE_STATUT_ATT, applicationContext.getMessage("nomenclature.typeStatut.attente", null, locale)));
		majTypeStatut(new TypeStatut(NomenclatureUtils.TYPE_STATUT_REC, applicationContext.getMessage("nomenclature.typeStatut.recept", null, locale)));
		majTypeStatut(new TypeStatut(NomenclatureUtils.TYPE_STATUT_INC, applicationContext.getMessage("nomenclature.typeStatut.incomplet", null, locale)));
		majTypeStatut(new TypeStatut(NomenclatureUtils.TYPE_STATUT_COM, applicationContext.getMessage("nomenclature.typeStatut.complet", null, locale)));
		
		/*Type de statut de pice*/
		majTypeStatutPiece(new TypeStatutPiece(NomenclatureUtils.TYP_STATUT_PIECE_REFUSE,applicationContext.getMessage("nomenclature.typstatutpiece.ref", null, locale)));
		majTypeStatutPiece(new TypeStatutPiece(NomenclatureUtils.TYP_STATUT_PIECE_TRANSMIS,applicationContext.getMessage("nomenclature.typstatutpiece.tra", null, locale)));
		majTypeStatutPiece(new TypeStatutPiece(NomenclatureUtils.TYP_STATUT_PIECE_VALIDE,applicationContext.getMessage("nomenclature.typstatutpiece.val", null, locale)));
		majTypeStatutPiece(new TypeStatutPiece(NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE,applicationContext.getMessage("nomenclature.typstatutpiece.ate", null, locale)));
		majTypeStatutPiece(new TypeStatutPiece(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE,applicationContext.getMessage("nomenclature.typstatutpiece.nonconc", null, locale)));
				
		/*Les parametres*/		
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_NB_VOEUX_MAX, applicationContext.getMessage("parametrage.codParam.nbVoeuxMax", null, locale), "20",NomenclatureUtils.TYP_PARAM_INTEGER));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_NB_VOEUX_MAX_IS_ETAB, applicationContext.getMessage("parametrage.codParam.nbVoeuxMaxIsEtab", null, locale), ConstanteUtils.TYP_BOOLEAN_NO,NomenclatureUtils.TYP_PARAM_BOOLEAN));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_NB_JOUR_ARCHIVAGE, applicationContext.getMessage("parametrage.codParam.nbJourArchivage", null, locale), "365",NomenclatureUtils.TYP_PARAM_INTEGER));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_NB_JOUR_KEEP_CPT_MIN, applicationContext.getMessage("parametrage.codParam.nbJourKeepCptMin", null, locale), "5",NomenclatureUtils.TYP_PARAM_INTEGER));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_PREFIXE_NUM_DOSS_CPT, applicationContext.getMessage("parametrage.codParam.prefixeNumDossCpt", null, locale), "",NomenclatureUtils.TYP_PARAM_STRING+"(2)"));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_IS_UTILISE_OPI, applicationContext.getMessage("parametrage.codParam.utiliseOpi", null, locale), ConstanteUtils.TYP_BOOLEAN_NO,NomenclatureUtils.TYP_PARAM_BOOLEAN));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_PREFIXE_OPI, applicationContext.getMessage("parametrage.codParam.prefixeOpi", null, locale), "EC",NomenclatureUtils.TYP_PARAM_STRING+"(2)"));		
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_IS_INE_OBLIGATOIRE_FR, applicationContext.getMessage("parametrage.codParam.ineObligatoireFr", null, locale), ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_IS_FORM_COD_APO_OBLI, applicationContext.getMessage("parametrage.codParam.formCodApoOblig", null, locale), ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_IS_UTILISE_DEMAT, applicationContext.getMessage("parametrage.codParam.utiliseDemat", null, locale), ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_FILE_MAX_SIZE, applicationContext.getMessage("parametrage.codParam.file.maxsize", null, locale), "2", NomenclatureUtils.TYP_PARAM_INTEGER));		
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_IS_MAINTENANCE, applicationContext.getMessage("parametrage.codParam.maintenance", null, locale), ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_IS_APPEL, applicationContext.getMessage("parametrage.codParam.appel", null, locale), ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN));		
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_IS_OPI_IMMEDIAT, applicationContext.getMessage("parametrage.codParam.opi.fil.eau", null, locale), ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_NB_JOUR_KEEP_HISTO_BATCH, applicationContext.getMessage("parametrage.codParam.nbJourKeepHistoBatch", null, locale), "30", NomenclatureUtils.TYP_PARAM_INTEGER));
		
		
		/*Les mail de statut de dossier*/
		majMail(new Mail(NomenclatureUtils.MAIL_STATUT_AT,applicationContext.getMessage("nomenclature.mail.statut.attente", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.statut.attente.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.statut.attente.content", null, locale));
		majMail(new Mail(NomenclatureUtils.MAIL_STATUT_RE,applicationContext.getMessage("nomenclature.mail.statut.recept", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.statut.recept.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.statut.recept.content", null, locale));
		majMail(new Mail(NomenclatureUtils.MAIL_STATUT_IN,applicationContext.getMessage("nomenclature.mail.statut.incomplet", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.statut.incomplet.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.statut.incomplet.content", null, locale));
		majMail(new Mail(NomenclatureUtils.MAIL_STATUT_CO,applicationContext.getMessage("nomenclature.mail.statut.complet", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.statut.complet.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.statut.complet.content", null, locale));
		
		/*Mail compte a minima*/
		majMail(new Mail(NomenclatureUtils.MAIL_CPT_MIN,applicationContext.getMessage("nomenclature.mail.cptMin", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.cptMin.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.cptMin.content", null, locale));				
		
		/*Mail id oublie*/
		majMail(new Mail(NomenclatureUtils.MAIL_CPT_MIN_ID_OUBLIE,applicationContext.getMessage("nomenclature.mail.idOublie", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.idOublie.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.idOublie.content", null, locale));
				
		/*Mail modif du mail du cptMin*/
		majMail(new Mail(NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL,applicationContext.getMessage("nomenclature.mail.modifmail", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.modifmail.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.modifmail.content", null, locale));
		
		/*Mail de candidature*/
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE,applicationContext.getMessage("nomenclature.mail.candidature", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.candidature.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.candidature.content", null, locale));
		
		/*Mail proposition*/
		//majMail(new Mail(NomenclatureUtils.MAIL_PROP_CANDIDATURE,applicationContext.getMessage("nomenclature.mail.prop.candidature", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.prop.candidature.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.prop.candidature.content", null, locale));
		
		/*Mail proposition*/
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_COMMISSION_PROP,applicationContext.getMessage("nomenclature.mail.commission.prop.candidature", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.commission.prop.candidature.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.commission.prop.candidature.content", null, locale));
		
		/*Mail d'annulation de candidature*/
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_ANNULATION,applicationContext.getMessage("nomenclature.mail.annul.candidature", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.annul.candidature.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.annul.candidature.content", null, locale));
		
		/*Mail d'annulation de candidature pour la commission*/
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_COMMISSION_ANNUL,applicationContext.getMessage("nomenclature.mail.commission.annul.candidature", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.commission.annul.candidature.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.commission.annul.candidature.content", null, locale));
		
		/*Mail de confirmation de candidature*/
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_CONFIRM,applicationContext.getMessage("nomenclature.mail.confirm.candidature", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.confirm.candidature.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.confirm.candidature.content", null, locale));
		
		/*Mail de desistement de candidature*/
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_DESIST,applicationContext.getMessage("nomenclature.mail.desist.candidature", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.desist.candidature.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.desist.candidature.content", null, locale));
		
		/*Mail type de traitement AD*/
		majMail(new Mail(NomenclatureUtils.MAIL_TYPE_TRAIT_AD,applicationContext.getMessage("nomenclature.mail.typetrait.ad", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.typetrait.ad.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.typetrait.ad.content", null, locale));
		
		/*Mail type de traitement AC*/
		majMail(new Mail(NomenclatureUtils.MAIL_TYPE_TRAIT_AC,applicationContext.getMessage("nomenclature.mail.typetrait.ac", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.typetrait.ac.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.typetrait.ac.content", null, locale));
		
		/*Mail type de traitement AC*/
		majMail(new Mail(NomenclatureUtils.MAIL_TYPE_TRAIT_ATT,applicationContext.getMessage("nomenclature.mail.typetrait.att", null, locale),true,true,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,null),applicationContext.getMessage("nomenclature.mail.typetrait.att.sujet", null, locale),applicationContext.getMessage("nomenclature.mail.typetrait.att.content", null, locale));
		
		/*La version de la nomenclature pour finir*/
		majVersion(new Version(NomenclatureUtils.VERSION_NOMENCLATURE_COD,NomenclatureUtils.VERSION_NOMENCLATURE_VAL));
		
		if (!batchMode){
			Notification.show(applicationContext.getMessage("nomenclature.maj.sucess", null, locale), Type.TRAY_NOTIFICATION);
		}		
	}
	
	/** Mise a jour de la version
	 * @param version
	 */
	private void majVersion(Version version){
		Version v = versionRepository.findOne(version.getCodVersion());
		if (v != null){
			v.setValVersion(version.getValVersion());
			v.setDatVersion(LocalDateTime.now());
			versionRepository.save(v);
		}else{
			versionRepository.save(version);
		}
		
	}
	
	/** Met a jour un type de decision
	 * @param typeDec
	 */
	private void majParametre(Parametre param){
		//MethodUtils.validateBean(param, LoggerFactory.getLogger(MailController.class));
		Parametre paramLoad = parametreRepository.findByCodParam(param.getCodParam());
		if (paramLoad==null){			
			parametreRepository.saveAndFlush(param);
		}
	}
	
	/** Met à jour la liste des fonctionnalités
	 * @param droitProfil
	 * @param droitFonctionnalite
	 */
	private void majDroitProfilFonc(DroitProfil droitProfil, DroitFonctionnalite droitFonctionnalite) {
		droitFonctionnalite = droitFonctionnaliteRepository.saveAndFlush(droitFonctionnalite);
		droitProfil.addFonctionnalite(new DroitProfilFonc(droitFonctionnalite, droitProfil, false));
		droitProfilRepository.saveAndFlush(droitProfil);
	}
	
	/** Met a jour un type de traitement
	 * @param typeStatut
	 */
	private void majTypeTraitement(TypeTraitement typeTraitement){		
		TypeTraitement typeTraitementLoad = typeTraitementRepository.findByCodTypTrait(typeTraitement.getCodTypTrait());
		if (typeTraitementLoad==null){			
			TypeTraduction typeTrad = typeTraductionRepository.findOne(NomenclatureUtils.TYP_TRAD_TYP_TRAIT_LIB);
			I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTrad));
			I18nTraduction trad = new I18nTraduction(typeTraitement.getLibTypTrait(), i18n, tableRefController.getLangueDefault());
			i18nTraductionRepository.saveAndFlush(trad);
			typeTraitement.setI18nLibTypTrait(i18n);
			
			typeTraitementRepository.saveAndFlush(typeTraitement);
		}
	}

	/** Met a jour un type de decision
	 * @param typeDec
	 */
	private void majTypeDec(TypeDecision typeDec){
		TypeDecision typeDecLoad = typeDecisionRepository.findByCodTypDec(typeDec.getCodTypDec());
		if (typeDecLoad==null){			
			TypeTraduction typeTrad = typeTraductionRepository.findOne(NomenclatureUtils.TYP_TRAD_TYP_DEC_LIB);
			I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTrad));
			I18nTraduction trad = new I18nTraduction(typeDec.getLibTypDec(), i18n, tableRefController.getLangueDefault());
			i18nTraductionRepository.saveAndFlush(trad);
			typeDec.setI18nLibTypDec(i18n);
			
			typeDecisionRepository.saveAndFlush(typeDec);
		}
	}
	
	/** Met a jour un type de statut
	 * @param typeStatut
	 */
	private void majTypeStatut(TypeStatut typeStatut){
		TypeStatut typeStatutLoad = typeStatutRepository.findByCodTypStatut(typeStatut.getCodTypStatut());
		if (typeStatutLoad==null){			
			TypeTraduction typeTrad = typeTraductionRepository.findOne(NomenclatureUtils.TYP_TRAD_TYP_STATUT);
			I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTrad));
			I18nTraduction trad = new I18nTraduction(typeStatut.getLibTypStatut(), i18n, tableRefController.getLangueDefault());
			i18nTraductionRepository.saveAndFlush(trad);
			typeStatut.setI18nLibTypStatut(i18n);
			
			typeStatutRepository.saveAndFlush(typeStatut);
		}
	}
	
	/** Met a jour un type de statut de piece
	 * @param typeStatutPiece
	 */
	private void majTypeStatutPiece(TypeStatutPiece typeStatutPiece){
		TypeStatutPiece typeStatutPiceLoad = typeStatutPieceRepository.findByCodTypStatutPiece(typeStatutPiece.getCodTypStatutPiece());
		if (typeStatutPiceLoad==null){			
			TypeTraduction typeTrad = typeTraductionRepository.findOne(NomenclatureUtils.TYP_TRAD_TYP_STATUT_PIECE);
			I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTrad));
			I18nTraduction trad = new I18nTraduction(typeStatutPiece.getLibTypStatutPiece(), i18n, tableRefController.getLangueDefault());
			i18nTraductionRepository.saveAndFlush(trad);
			typeStatutPiece.setI18nLibTypStatutPiece(i18n);
			
			typeStatutPieceRepository.saveAndFlush(typeStatutPiece);
		}else{
			typeStatutPiceLoad.setLibTypStatutPiece(typeStatutPiece.getLibTypStatutPiece());
			typeStatutPiceLoad.getI18nLibTypStatutPiece().getI18nTraductions().forEach(e->{
				if (e.getLangue().equals(tableRefController.getLangueDefault())){
					e.setValTrad(typeStatutPiece.getLibTypStatutPiece());
					i18nTraductionRepository.saveAndFlush(e);
				}
			});
			typeStatutPieceRepository.saveAndFlush(typeStatutPiceLoad);
		}
	}
	
	/** Mise à jour d'un droit
	 * @param droit
	 * @return le droit profil maj
	 */
	private DroitProfil majDroitProfil(DroitProfil droitProfil){
		DroitProfil droitProfilLoad = droitProfilRepository.findByCodProfil(droitProfil.getCodProfil());
		if (droitProfilLoad==null){
			return droitProfilRepository.saveAndFlush(droitProfil);
		}else{
			droitProfilLoad.setLibProfil(droitProfil.getLibProfil());
			return droitProfilRepository.saveAndFlush(droitProfilLoad);
		}
	}
	
	/** Mise à jour d'une langue
	 * @param langue
	 */
	private void majLangue(Langue langue){
		Langue langueLoad = langueRepository.findOne(langue.getCodLangue());
		if (langueLoad==null){
			langueRepository.saveAndFlush(langue);
		}else{
			langueLoad.setLibLangue(langue.getLibLangue());
			langueRepository.saveAndFlush(langueLoad);
		}
	}
	
	/** Mise à jour d'un batch
	 * @param batch
	 */
	private void majBatch(Batch batch){
		Batch batchLoad = batchRepository.findOne(batch.getCodBatch());
		if (batchLoad==null){
			MethodUtils.validateBean(batch, logger);
			batch = batchRepository.saveAndFlush(batch);
		}else{
			batchLoad.setLibBatch(batch.getLibBatch());			
			batchRepository.saveAndFlush(batchLoad);
		}
	}
	
	/** Mise à jour d'un mail
	 * @param mail
	 * @param content 
	 * @return le mail maj
	 */
	private Mail majMail(Mail mail, String sujet,String content){
		Mail mailLoad = mailRepository.findByCodMail(mail.getCodMail());
		if (sujet==null || sujet.equals("")){
			sujet = mail.getLibMail();
		}
		if (content==null || content.equals("")){
			content = mail.getLibMail();
		}
		if (mailLoad==null){
			TypeTraduction typeTradSujet = typeTraductionRepository.findOne(NomenclatureUtils.TYP_TRAD_MAIL_SUJET);
			I18n i18nSujetMail = i18nRepository.saveAndFlush(new I18n(typeTradSujet));
			i18nTraductionRepository.saveAndFlush(new I18nTraduction(sujet, i18nSujetMail, tableRefController.getLangueDefault()));			
			mail.setI18nSujetMail(i18nSujetMail);
			
			TypeTraduction typeTradCorps = typeTraductionRepository.findOne(NomenclatureUtils.TYP_TRAD_MAIL_CORPS);
			I18n i18nCorpsMail = i18nRepository.saveAndFlush(new I18n(typeTradCorps));
			i18nTraductionRepository.saveAndFlush(new I18nTraduction(content, i18nCorpsMail, tableRefController.getLangueDefault()));			
			mail.setI18nCorpsMail(i18nCorpsMail);
			mail = mailRepository.saveAndFlush(mail);
			return mail;
		}
		
		return mailLoad;
	}
	
	/**
	 * Methode permettant de supprimer des élements déja insérés
	 */
	public void cleanNomenclature(){
		/*Suppression du mail de proposition*/
		Mail mailLoad = mailRepository.findByCodMail("PROP_CANDIDATURE");
		if (mailLoad!=null){
			mailRepository.delete(mailLoad);
		}
		
		/*Suppression des codes par domaine : CPT_MIN_ et CANDIDATURE_*/
		mailLoad = mailRepository.findByCodMail("MOD_MAIL_CPT_MIN");
		if (mailLoad!=null){
			mailLoad.setCodMail(NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL);
			mailRepository.saveAndFlush(mailLoad);
		}
		mailLoad = mailRepository.findByCodMail("ID_OUBLIE");
		if (mailLoad!=null){
			mailLoad.setCodMail(NomenclatureUtils.MAIL_CPT_MIN_ID_OUBLIE);
			mailRepository.saveAndFlush(mailLoad);
		}
		mailLoad = mailRepository.findByCodMail("COMMISSION_PROP");
		if (mailLoad!=null){
			mailLoad.setCodMail(NomenclatureUtils.MAIL_CANDIDATURE_COMMISSION_PROP);
			mailRepository.saveAndFlush(mailLoad);
		}
		mailLoad = mailRepository.findByCodMail("COMMISSION_ANNUL");
		if (mailLoad!=null){
			mailLoad.setCodMail(NomenclatureUtils.MAIL_CANDIDATURE_COMMISSION_ANNUL);
			mailRepository.saveAndFlush(mailLoad);
		}
		
		mailLoad = mailRepository.findByCodMail("ANNUL_CANDIDATURE");
		if (mailLoad!=null){
			mailLoad.setCodMail(NomenclatureUtils.MAIL_CANDIDATURE_ANNULATION);
			mailRepository.saveAndFlush(mailLoad);
		}
		
		mailLoad = mailRepository.findByCodMail("CONFIRM_CANDIDATURE");
		if (mailLoad!=null){
			mailLoad.setCodMail(NomenclatureUtils.MAIL_CANDIDATURE_CONFIRM);
			mailRepository.saveAndFlush(mailLoad);
		}
		
		mailLoad = mailRepository.findByCodMail("DESIST_CANDIDATURE");
		if (mailLoad!=null){
			mailLoad.setCodMail(NomenclatureUtils.MAIL_CANDIDATURE_DESIST);
			mailRepository.saveAndFlush(mailLoad);
		}
		
		Parametre paramLoad = parametreRepository.findByCodParam("NB_VOEUX_CTR_MAX");
		if (paramLoad!=null){
			parametreRepository.delete(paramLoad);
		}
	}
	
	/**
	 * @return la liste des versions
	 */
	public List<SimpleTablePresentation> getVersions(){
		List<SimpleTablePresentation> liste = new ArrayList<SimpleTablePresentation>();
		liste.add(new SimpleTablePresentation(1,NomenclatureUtils.VERSION_APPLICATION_COD,applicationContext.getMessage("version.app", null, UI.getCurrent().getLocale()), environment.getRequiredProperty("app.version"),null));
		
		Version vDb = getDbVersion(NomenclatureUtils.VERSION_DB);
		liste.add(getPresentationFromVersion(vDb,NomenclatureUtils.VERSION_DB));
		
		Version vNomenclature = getVersion(NomenclatureUtils.VERSION_NOMENCLATURE_COD);
		liste.add(getPresentationFromVersion(vNomenclature,NomenclatureUtils.VERSION_NOMENCLATURE_COD));
		
		Version vApo = getVersion(NomenclatureUtils.VERSION_SI_SCOL_COD);
		liste.add(getPresentationFromVersion(vApo,NomenclatureUtils.VERSION_SI_SCOL_COD));
		
		liste.add(new SimpleTablePresentation(5,NomenclatureUtils.VERSION_WS,applicationContext.getMessage("version.ws", null, UI.getCurrent().getLocale()),MethodUtils.getClassVersion(WSUtils.class),null));
		
		/*demat*/
		String libDemat = NomenclatureUtils.VERSION_NO_VERSION_VAL;
		if (fileController.getModeDemat().equals(ConstanteUtils.TYPE_FICHIER_STOCK_CMIS)){
			libDemat = applicationContext.getMessage("demat.cmis", null, UI.getCurrent().getLocale());
		}else if (fileController.getModeDemat().equals(ConstanteUtils.TYPE_FICHIER_STOCK_FILE_SYSTEM)){
			libDemat = applicationContext.getMessage("demat.fs", null, UI.getCurrent().getLocale());
		}
		liste.add(new SimpleTablePresentation(6,NomenclatureUtils.VERSION_DEMAT,applicationContext.getMessage("version.demat", null, UI.getCurrent().getLocale()),libDemat,null));
		liste.add(new SimpleTablePresentation(7,NomenclatureUtils.VERSION_LS,applicationContext.getMessage("version.limesurvey", null, UI.getCurrent().getLocale()),"-",null));
		
		return liste;
	}
	
	/** Renvoi un objet de presentation grace a son bean
	 * @param version
	 * @param code
	 * @return la table de presentation des nomenclatures
	 */
	public SimpleTablePresentation getPresentationFromVersion(Version version, String code){
		Integer i = 0;
		String lib = "";
		if(code.equals(NomenclatureUtils.VERSION_DB)){
			i = 2;
			lib = applicationContext.getMessage("version.db", null, UI.getCurrent().getLocale());
		}else if (code.equals(NomenclatureUtils.VERSION_NOMENCLATURE_COD)){
			i = 3;
			lib = applicationContext.getMessage("version.nomenclature", null, UI.getCurrent().getLocale());
		}else if(code.equals(NomenclatureUtils.VERSION_SI_SCOL_COD)){
			i = 4;
			lib = applicationContext.getMessage("version.siScol", null, UI.getCurrent().getLocale());
		}
		return new SimpleTablePresentation(i,code,lib,version!=null?version.getValVersion():NomenclatureUtils.VERSION_NO_VERSION_VAL,version!=null?version.getDatVersion():null);
	}
}
