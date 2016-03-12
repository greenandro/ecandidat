package fr.univlorraine.ecandidat.utils;


/**
 * Classe de constantes de nomenclature
 * @author Kevin Hergalant
 *
 */
public class NomenclatureUtils {

	/*Nomenclature*/
	public static final String USER_NOMENCLATURE = "Nomenclature";
	
	/*Version*/
	public static final String VERSION_NOMENCLATURE_COD = "VERSION_NOMENCLATURE";	
	public static final String VERSION_NOMENCLATURE_VAL = "2.1.1.1";
	public static final String VERSION_NO_VERSION_VAL = "-";
	public static final String VERSION_APPLICATION_COD = "VERSION_APPLICATION";
	public static final String VERSION_DB = "VERSION_DB";
	public static final String VERSION_SI_SCOL_COD = "VERSION_SI_SCOL";
	public static final String VERSION_DEMAT = "VERSION_DEMAT";
	
	public static final String VERSION_WS = "VERSION_WS";
	public static final String VERSION_LS = "VERSION_LS";
	
	/*Type d'avis*/
	public static final String TYP_AVIS_FAV = "FA";
	public static final String TYP_AVIS_DEF = "DE";
	public static final String TYP_AVIS_LISTE_COMP = "LC";
	public static final String TYP_AVIS_LISTE_ATTENTE = "LA";
	public static final String TYP_AVIS_PRESELECTION = "PR";
	public static final String TYP_AVIS_ATTENTE = "AT";
	
	/*Type de statut de pièce*/
	public static final String TYP_STATUT_PIECE_TRANSMIS = "TR";
	public static final String TYP_STATUT_PIECE_VALIDE = "VA";
	public static final String TYP_STATUT_PIECE_REFUSE = "RE";
	public static final String TYP_STATUT_PIECE_ATTENTE = "AT";
	public static final String TYP_STATUT_PIECE_NON_CONCERNE = "NC";
	
	/*Type de traitement*/
	public static final String TYP_TRAIT_AD = "AD";
	public static final String TYP_TRAIT_AC = "AC";
	public static final String TYP_TRAIT_AT = "AT";
	
	/*Civilite*/
	public static final String CIVILITE_APO_M = "1";
	public static final String CIVILITE_APO_F = "2";
	
	/*Langues*/
	public static final String LANGUE_FR = "fr";
	public static final String LANGUE_EN = "en";
	public static final String LANGUE_ES = "es";
	public static final String LANGUE_DE = "de";
	
	/*Types de traduction*/
	public static final String TYP_TRAD_FORM_LIB = "FORM_LIB";
	public static final String TYP_TRAD_FORM_URL = "FORM_URL";
	public static final String TYP_TRAD_MAIL_SUJET = "MAIL_SUJET";
	public static final String TYP_TRAD_MAIL_CORPS = "MAIL_CORPS";
	public static final String TYP_TRAD_MOTIV_LIB = "MOTIV_LIB";
	public static final String TYP_TRAD_PJ_LIB = "PJ_LIB";
	/*public static final String TYP_TRAD_PJ_FILE = "PJ_FILE";
	public static final String TYP_TRAD_PJ_LIB_FILE = "PJ_LIB_FILE";*/
	public static final String TYP_TRAD_TYP_DEC_LIB = "TYP_DEC_LIB";
	public static final String TYP_TRAD_TYP_TRAIT_LIB = "TYP_TRAIT_LIB";
	public static final String TYP_TRAD_TYP_STATUT = "TYP_STATUT";
	public static final String TYP_TRAD_TYP_STATUT_PIECE = "TYP_STATUT_PIECE";
	public static final String TYP_TRAD_FAQ_QUESTION = "FAQ_QUESTION";
	public static final String TYP_TRAD_FAQ_REPONSE = "FAQ_REPONSE";
	
	/*Droit profil*/
	public static final String DROIT_PROFIL_ADMIN_TECH = "adminTechnique";
	public static final String DROIT_PROFIL_ADMIN = "admin";
	public static final String DROIT_PROFIL_CANDIDAT = "candidat";
	public static final String DROIT_PROFIL_SCOL_CENTRALE = "scolCentrale";
	public static final String DROIT_PROFIL_CENTRE_CANDIDATURE = "centreCandidature";
	public static final String DROIT_PROFIL_COMMISSION = "commission";
	
	/*Fonctionnalite*/
	public static final String FONCTIONNALITE_PARAM = "PARAMETRAGE";
	public static final String FONCTIONNALITE_GEST_FORMATION = "GEST_FORMATION";
	public static final String FONCTIONNALITE_GEST_COMMISSION = "GEST_COMMISSION";
	public static final String FONCTIONNALITE_GEST_PJ = "GEST_PJ";
	public static final String FONCTIONNALITE_GEST_FORMULAIRE = "GEST_FORMULAIRE";
	public static final String FONCTIONNALITE_GEST_CANDIDATURE = "GEST_CANDIDATURE";	
	public static final String FONCTIONNALITE_GEST_CANDIDATURE_STATUT_DOSSIER = "GEST_CANDIDATURE_STATUT_DOSSIER";
	public static final String FONCTIONNALITE_GEST_CANDIDATURE_HISTO = "GEST_CANDIDATURE_HISTO";
	public static final String FONCTIONNALITE_EDIT_TYPTRAIT = "EDIT_TYPTRAIT";
	public static final String FONCTIONNALITE_VALID_TYPTRAIT = "VALID_TYPTRAIT";
	public static final String FONCTIONNALITE_EDIT_AVIS = "EDIT_AVIS";
	public static final String FONCTIONNALITE_VALID_AVIS = "VALID_AVIS";	
	
	/*Action fonctionnalite sans droit*/
	public static final String FONCTIONNALITE_OPEN_CANDIDAT = "OPEN_CANDIDAT";
	
	/*Mail*/
	public static final String MAIL_GEN_VAR = "libelleCampagne";
	public static final String MAIL_CANDIDAT_GEN_VAR = "candidat.numDossierOpi;candidat.nomPat;candidat.nomUsu;candidat.prenom;candidat.autrePrenom;candidat.ine;candidat.cleIne;candidat.datNaiss;candidat.libVilleNaiss;candidat.libLangue;candidat.tel;candidat.telPort";
	public static final String MAIL_FORMATION_GEN_VAR = "formation.code;formation.libelle;formation.codEtpVetApo;formation.codVrsVetApo;formation.libApo;formation.motCle;formation.datDebDepot;formation.datFinDepot;formation.datPreAnalyse;formation.datRetour;formation.datJury;formation.datPubli;formation.datConfirm";
	public static final String MAIL_COMMISSION_GEN_VAR = "commission.libelle;commission.mail;commission.adresse;commission.tel;commission.fax;commission.commentaireRetour";
	
	public static final String MAIL_DEC_VAR = "commentaire;complementAppel";
	public static final String MAIL_DEC_VAR_DEFAVORABLE = "motif";
	public static final String MAIL_DEC_VAR_PRESELECTION = "complementPreselect";
	public static final String MAIL_DEC_VAR_LISTE_COMP = "rang";
	public static final String MAIL_DEC_FAVORABLE = "AVIS_FAVORABLE";
	public static final String MAIL_DEC_DEFAVORABLE = "AVIS_DEFAVORABLE";
	public static final String MAIL_DEC_LISTE_ATT = "AVIS_LISTE_ATT";
	public static final String MAIL_DEC_LISTE_COMP = "AVIS_LISTE_COMP";
	public static final String MAIL_DEC_PRESELECTION = "AVIS_PRESELECTION";
	
	public static final String MAIL_STATUT_PREFIX = "STATUT_";
	public static final String MAIL_STATUT_AT = "STATUT_AT";
	public static final String MAIL_STATUT_RE = "STATUT_RE";
	public static final String MAIL_STATUT_IN = "STATUT_IN";
	public static final String MAIL_STATUT_CO = "STATUT_CO";
	
	
	public static final String MAIL_CPT_MIN = "CPT_MIN_CREATE";
	public static final String MAIL_CPT_MIN_VAR = "prenom;nom;numDossierOpi;password;lienValidation;jourDestructionCptMin";
	
	public static final String MAIL_CPT_MIN_ID_OUBLIE = "CPT_MIN_ID_OUBLIE";
	public static final String MAIL_CPT_MIN_ID_OUBLIE_VAR = "prenom;nom;numDossierOpi;password;";
	
	public static final String MAIL_CPT_MIN_MOD_MAIL = "CPT_MIN_MOD_MAIL";
	public static final String MAIL_CPT_MIN_MOD_MAIL_VAR = "prenom;nom;numDossierOpi;lienValidation";
	
	/*Mail de candidature*/
	public static final String MAIL_CANDIDATURE = "CANDIDATURE";
	
	/*Mail de proposition*/
	//public static final String MAIL_PROP_CANDIDATURE = "PROP_CANDIDATURE";
	
	/*Mail de proposition pour la commission*/
	public static final String MAIL_CANDIDATURE_COMMISSION_PROP = "CANDIDATURE_COMMISSION_PROP";
	
	/*Mail d'annulation de candidature*/
	public static final String MAIL_CANDIDATURE_ANNULATION = "CANDIDATURE_ANNULATION";
	
	/*Mail d'annulation de candidature pour la commission*/
	public static final String MAIL_CANDIDATURE_COMMISSION_ANNUL = "CANDIDATURE_COMMISSION_ANNUL";
	
	/*Mail de confirmation de candidature*/
	public static final String MAIL_CANDIDATURE_CONFIRM = "CANDIDATURE_CONFIRM";
	
	/*Mail de desistement de candidature*/
	public static final String MAIL_CANDIDATURE_DESIST = "CANDIDATURE_DESIST";
	
	/*Mail type de traitement AD*/
	public static final String MAIL_TYPE_TRAIT_AD = "TYPE_TRAIT_AD";
	
	/*Mail type de traitement AC*/
	public static final String MAIL_TYPE_TRAIT_AC = "TYPE_TRAIT_AC";
	
	/*Mail type de traitement AC*/
	public static final String MAIL_TYPE_TRAIT_ATT = "TYPE_TRAIT_ATT";
	
	/*Type de decision*/
	public static final String TYP_DEC_FAVORABLE = "AVIS_FAVORABLE";
	public static final String TYP_DEC_DEFAVORABLE = "AVIS_DEFAVORABLE";
	public static final String TYP_DEC_LISTE_ATT = "AVIS_LISTE_ATT";
	public static final String TYP_DEC_LISTE_COMP = "AVIS_LISTE_COMP";
	public static final String TYP_DEC_PRESELECTION = "AVIS_PRESELECTION";
	
	/*Type de statut de dossier*/
	public static final String TYPE_STATUT_ATT = "AT";
	public static final String TYPE_STATUT_REC = "RE";
	public static final String TYPE_STATUT_INC = "IN";
	public static final String TYPE_STATUT_COM = "CO";
	
	/*Parametres*/
	public static final String COD_PARAM_NB_VOEUX_MAX = "NB_VOEUX_MAX";
	public static final String COD_PARAM_NB_VOEUX_MAX_IS_ETAB = "NB_VOEUX_MAX_IS_ETAB";
	public static final String COD_PARAM_NB_JOUR_ARCHIVAGE = "NB_JOUR_ARCHIVAGE";
	public static final String COD_PARAM_NB_JOUR_KEEP_CPT_MIN = "NB_JOUR_KEEP_CPT_MIN";
	public static final String COD_PARAM_PREFIXE_NUM_DOSS_CPT = "PREFIXE_NUM_DOSS_CPT";
	public static final String COD_PARAM_PREFIXE_OPI = "PREFIXE_OPI";
	public static final String COD_PARAM_IS_UTILISE_OPI = "IS_UTILISE_OPI";
	public static final String COD_PARAM_IS_INE_OBLIGATOIRE_FR = "IS_INE_OBLI_FR";
	public static final String COD_PARAM_IS_FORM_COD_APO_OBLI = "IS_FORM_COD_APO_OBLI";
	public static final String COD_PARAM_IS_UTILISE_DEMAT = "IS_UTILISE_DEMAT";
	public static final String COD_PARAM_FILE_MAX_SIZE = "FILE_MAX_SIZE";
	public static final String COD_PARAM_IS_MAINTENANCE = "IS_MAINTENANCE";
	public static final String COD_PARAM_IS_APPEL = "IS_APPEL";
	public static final String COD_PARAM_IS_OPI_IMMEDIAT = "IS_OPI_IMMEDIAT";
	public static final String COD_PARAM_NB_JOUR_KEEP_HISTO_BATCH = "NB_JOUR_KEEP_HISTO_BATCH";

	public static final String TYP_PARAM_BOOLEAN= "Boolean";
	public static final String TYP_PARAM_INTEGER= "Integer";
	public static final String TYP_PARAM_STRING = "String";
	
	/*Batch*/
	public static final String BATCH_SI_SCOL = "BATCH_SYNCHRO_SISCOL";
	public static final String BATCH_APP_EN_MAINT = "BATCH_APP_EN_MAINT";
	public static final String BATCH_APP_EN_SERVICE = "BATCH_APP_EN_SERVICE";
	public static final String BATCH_NETTOYAGE_CPT = "BATCH_NETTOYAGE_CPT";
	public static final String BATCH_NETTOYAGE = "BATCH_NETTOYAGE";
	public static final String BATCH_DESTRUCT_HISTO = "BATCH_DESTRUCT_HISTO";	
	public static final String BATCH_ARCHIVAGE = "BATCH_ARCHIVAGE";
	public static final String BATCH_SYNC_LIMESURVEY = "BATCH_SYNC_LIMESURVEY";
	public static final String BATCH_DESTRUCT_DOSSIER = "BATCH_DESTRUCT_DOSSIER";
	public static final String BATCH_ASYNC_OPI = "BATCH_ASYNC_OPI";
	public static final String BATCH_DEMO = "BATCH_DEMO";
		
}


