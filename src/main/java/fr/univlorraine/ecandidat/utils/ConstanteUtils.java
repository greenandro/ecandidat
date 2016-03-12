package fr.univlorraine.ecandidat.utils;

/**
 * Classe de constantes
 * @author Kevin Hergalant
 *
 */
public class ConstanteUtils {
	
	/*Parametres Servlet*/
	public static final String SERVLET_ALL_MATCH = "/*";
	public static final String SERVLET_NO_MATCH = "/nomatchingpossible";
	
	/*Nombre de caractère minimum pour la recherche de personnels*/
	public static final Integer NB_MIN_CAR_PERS = 2;
	
	/*Nombre de caractère minimum pour la recherche de candidats*/
	public static final Integer NB_MIN_CAR_CAND = 2;
	
	/*Nombre de caractère minimum pour la recherche de formations APO*/
	public static final Integer NB_MIN_CAR_FORM = 3;
	
	/*Nombre maximum de compte a minima en recherche*/
	public static final Integer NB_MAX_RECH_CPT_MIN = 200;
	
	/*Nombre maximum de personnel en recherche*/
	public static final Integer NB_MAX_RECH_PERS = 200;
	
	/*Nombre maximum de formation en recherche*/
	public static final Integer NB_MAX_RECH_FORM = 50;
	
	/*Taille max du param de la taille max d'une PJ*/
	public static final Integer SIZE_MAX_PARAM_MAX_FILE_PJ = 10;
	
	/*Nombre de candidature maximum pour l'edition en masse*/
	public static final Integer SIZE_MAX_EDITION_MASSE = 200;
	
	/*Boolean si on ajoute les PJ au dossier telechargé*/
	public static final Boolean ADD_PJ_TO_DOSSIER = true;
	
	
	/*Les mode de création d'adresse en OPI*/
	/*
	1 : Pas de récupération d'adresse dans l'OPI. Les adresses (fixe et année en cours) seront crées au moment de l'IA.
	2 : Adresse eCandidat = adresse pour l'année en cours. Contrôle et saisie à l'IA.
	3 : Adresse eCandidat = adresse fixe. Contrôle et saisie à l'IA.
	4 : Adresse eCandidat = adresse fixe = adresse pour l'année en cours. Contrôle à l'IA.
	*/
	public static final Integer OPI_ADR_NO_RECUP = 1;
	public static final Integer OPI_ADR_ANNEE = 2;
	public static final Integer OPI_ADR_FIXE = 3;
	public static final Integer OPI_ADR_BOTH = 4;
	public static final Integer OPI_ADR_MODE = OPI_ADR_NO_RECUP;
	
	/*Les autorisations des vues*/
	public static final String PREFIXE_ROLE = "ROLE_";
	public static final String PRE_AUTH_ADMIN = "hasAnyRole('"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH+"')";
	public static final String PRE_AUTH_SCOL_CENTRALE = "hasAnyRole('"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE+"')";
	public static final String PRE_AUTH_CTR_CAND = "hasAnyRole('"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE+"')";
	public static final String PRE_AUTH_COMMISSION = "hasAnyRole('"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_COMMISSION+"')";
	public static final String PRE_AUTH_CANDIDAT = "hasAnyRole('"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE+"','"+PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_CANDIDAT+"')";
	
	/*Droit profil*/	
	public static final String ROLE_ANONYMOUS = PREFIXE_ROLE+"ANONYMOUS";
	public static final String ROLE_ADMIN_TECH = PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH;
	public static final String ROLE_ADMIN = PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_ADMIN;
	public static final String ROLE_CANDIDAT = PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_CANDIDAT;
	public static final String ROLE_SCOL_CENTRALE = PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE;
	public static final String ROLE_CENTRE_CANDIDATURE = PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE;
	public static final String ROLE_COMMISSION = PREFIXE_ROLE+NomenclatureUtils.DROIT_PROFIL_COMMISSION;
	
	/*La property drapeau*/
	public static final String PROPERTY_FLAG = "flag";
	
	/*Les menus de l'UI*/
	public static final String UI_MENU_ADMIN = "UI_MENU_PARAM";
	public static final String UI_MENU_SCOL = "UI_MENU_SCOL";
	public static final String UI_MENU_CTR = "UI_MENU_CTR";
	public static final String UI_MENU_GEST_CAND = "UI_MENU_GEST_CAND";
	public static final String UI_MENU_CAND = "UI_MENU_CAND";
	public static final String UI_MENU_COMM = "UI_MENU_COMM";
	
	/*Constantes de batch*/
	public static String BATCH_RUNNING = "RUNNING";
	public static String BATCH_FINISH = "FINISH";
	public static String BATCH_ERROR = "ERROR";
	public static String BATCH_INTERRUPT = "INTERRUPT";
	
	/*Type fichier*/
	public static String TYPE_FICHIER_CANDIDAT = "C";
	public static String TYPE_FICHIER_GESTIONNAIRE = "G";
	public static String TYPE_FICHIER_STOCK_CMIS = "C";
	public static String TYPE_FICHIER_STOCK_FILE_SYSTEM = "F";
	public static String TYPE_FICHIER_STOCK_NONE = "N";	
	public static String TYPE_FICHIER_PJ_GEST = "G";
	public static String TYPE_FICHIER_PJ_CAND = "C";
	
	/*Type MIME Acceptes*/
	public static String[] TYPE_MIME_FILE_PDF = {"application/pdf"};
	public static String[] TYPE_MIME_FILE_JPG = {"image/jpeg","image/jpeg"};
	
	/*Extensions Acceptees*/
	public static String[] EXTENSION_PDF_JPG_JPEG = {"pdf","jpg","jpeg"};
	
	/*Type de boolean apogee*/
	public static final String TYP_BOOLEAN_YES	= "O";
	public static final String TYP_BOOLEAN_NO	= "N";
	
	/*Code pays france Apogee*/
	public static final String PAYS_CODE_FRANCE	= "100";
	
	/*Code Type pays ou dpt Apogee*/
	public static final String COD_TYP_PAY_DPT_PAYS	= "P";
	public static final String COD_TYP_PAY_DPT_DEPARTEMENT	= "D";
	
	/*Constante jour,mois,annee*/
	public static Integer TYPE_JOUR = 1;
	public static Integer TYPE_MOIS = 2;
	public static Integer TYPE_ANNEE = 3;
	
	/*Constante type de formulaire*/
	public static final String  TYP_FORM_ADR = "TYP_FORM_ADR";
	public static final String  TYP_FORM_CANDIDAT = "TYP_FORM_CANDIDAT";
	
	/*Constante type de table Apo Ref*/
	public static final String TYP_REF_PAYS = "TYP_REF_PAYS";
	public static final String TYP_REF_DEPARTEMENT = "TYP_REF_DEPARTEMENT";
	public static final String TYP_REF_TYPDIPLOME = "TYP_REF_TYPDIPLOME";
	public static final String TYP_REF_CENTREGESTION = "TYP_REF_CENTREGESTION";
	public static final String TYP_REF_BAC_OU_EQU = "TYP_REF_BAC_OU_EQU";
	public static final String TYP_REF_DIP_AUT_CUR = "TYP_REF_DIP_AUT_CUR";
	public static final String TYP_REF_MENTION = "TYP_REF_MENTION";
	public static final String TYP_REF_MENTION_BAC = "TYP_REF_MENTION_BAC";
	public static final String TYP_REF_TYPRESULTAT = "TYP_REF_TYPRESULTAT";
	/*public static final String  TYP_APO_MENTIONNIVBAC = "TYP_APO_MENTIONNIVBAC";
	public static final String  TYP_APO_MENTION = "TYP_APO_MENTION";
	public static final String  TYP_APO_ETABLISSEMENT = "TYP_APO_ETABLISSEMENT";
	public static final String  TYP_APO_DIPAUTCUR = "TYP_APO_DIPAUTCUR";
	public static final String  TYP_APO_UTILISATEUR = "TYP_APO_UTILISATEUR";	
	public static final String  TYP_APO_COMMUNE = "TYP_APO_COMMUNE";
	public static final String  TYP_APO_COMBDI = "TYP_APO_COMBDI";	
	public static final String  TYP_APO_BACOUXEQU = "TYP_APO_BACOUXEQU";*/
	
	/*Constantes generation*/
	public static final String GEN_PWD = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";
	public static final String GEN_NUM_DOSS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final Integer GEN_SIZE = 8;
	
	/*Constante ODF*/	
	public static final String ODF_CAPTION = "caption";
	public static final String ODF_ICON = "icon";
	public static final String ODF_FORM_ID = "idForm";
	public static final String ODF_DIP_ID = "idDip";
	public static final String ODF_FORM_TITLE = "title";
	public static final String ODF_FORM_DIPLOME = "diplome";
	public static final String ODF_FORM_CTR_CAND = "ctrCand";
	public static final String ODF_FORM_MOT_CLE = "motCle";
	public static final String ODF_FORM_DATE = "dates";
	public static final String ODF_FORM_MODE_CAND = "modeCand";
	
	public static final String ODF_TYPE = "type";
	public static final String ODF_TYPE_CTR = "type_ctr";
	public static final String ODF_TYPE_DIP = "type_dip";
	public static final String ODF_TYPE_FORM = "type_form";
	
	/*Constantes WS validation compte*/
	public static final String REST_VALID_SUCCESS = "success";
	public static final String REST_VALID_CPT_NULL = "cptNull";
	public static final String REST_VALID_ALREADY_VALID = "alreadyValid";
	public static final String REST_VALID_ERROR = "error";
	
	/*Les modes de fonctionnement Si Scol*/
	public static final String SI_SCOL_NOT_APOGEE = "SI_SCOL_NOT_APOGEE";
	public static final String SI_SCOL_APOGEE = "SI_SCOL_APOGEE";
	
	/*Libelle generic*/;
	public static final String GENERIC_LIBELLE = "genericLibelle";
	
	/*Lock pour le candidat*/
	public static final String LOCK_INFOS_PERSO = "LOCK_INFOS_PERSO";
	public static final String LOCK_ADRESSE = "LOCK_ADRESSE";
	public static final String LOCK_BAC = "LOCK_BAC";
	public static final String LOCK_CURSUS_EXTERNE = "LOCK_CURSUS_EXTERNE";
	public static final String LOCK_FORMATION_PRO = "LOCK_FORMATION_PRO";
	public static final String LOCK_STAGE = "LOCK_STAGE";
	public static final String LOCK_ODF = "LOCK_ODF";
	public static final String LOCK_CAND = "LOCK_CAND";
	
	/*Candidature*/
	public static final String CANDIDATURE_LIB_FORM = "libForm";
	public static final String CANDIDATURE_DAT_RETOUR_FORM = "datRetourForm";
	public static final String CANDIDATURE_LIB_STATUT = "libStatut";
	public static final String CANDIDATURE_LIB_TYPE_TRAITEMENT = "libTypTraitement";
	public static final String CANDIDATURE_LIB_LAST_DECISION = "libLastDecision";
	public static final String CANDIDATURE_COMMENTAIRE = "commentaire";
	
	/*Cursus externe*/
	public static final String CURSUS_EXTERNE_OBTENU = "O";
	public static final String CURSUS_EXTERNE_NON_OBTENU = "N";
	public static final String CURSUS_EXTERNE_EN_COURS = "E";
	
	/*Load Balancing rechargement listes*/
	public static final String LB_RELOAD_PARAM = "PARAM";
	public static final String LB_RELOAD_LANGUE = "LANGUE";
	public static final String LB_RELOAD_FAQ = "FAQ";
	public static final String LB_RELOAD_ODF = "ODF";
	public static final String LB_RELOAD_TABLE_REF_PAYS = "TABLE_REF_PAYS";
	public static final String LB_RELOAD_TABLE_REF_DPT = "TABLE_REF_DPT";
	public static final String LB_RELOAD_TABLE_REF_TYPDIP = "TABLE_REF_TYPDIP";
	public static final String LB_RELOAD_TABLE_REF_CGE = "TABLE_REF_CGE";
	public static final String LB_RELOAD_TABLE_REF_BAC = "TABLE_REF_BAC";
	public static final String LB_RELOAD_TABLE_REF_DIP = "TABLE_REF_DIP";
	public static final String LB_RELOAD_TABLE_REF_MENTION = "TABLE_REF_MENTION";
	public static final String LB_RELOAD_TABLE_REF_MENTBAC = "TABLE_REF_MENTBAC";
	public static final String LB_RELOAD_TABLE_REF_TYPRES = "TABLE_REF_TYPRES";
	
	/*Options de candidature gestionnaire*/
	public static String OPTION_CLASSIQUE = "0";
	public static String OPTION_PROP = "1";	
	
	/*Variable a intégrer dans les formulaires*/
	public static String VAR_REGEX_FORM_NUM_DOSSIER = "\\$\\{numdossier\\}";
	
	/*Constantes de mois*/
	public static String[] NOM_MOIS_SHORT = { "jan", "fev", "mar", "avr", "mai", "juin", "juil",
        "aou", "sep", "oct", "nov", "dec" };
	
	public static String[] NOM_MOIS_LONG = { "Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin", "Juillet",
        "Aout", "Septembre", "Octobre", "Novembre", "Decembre" };
	
	/*Constantes de jour*/
	public static String[] NOM_JOURS = { "lun", "mar", "mer", "jeu", "ven", "sam", "dim"};
	
	/*Pour l'upload*/
	public static long UPLOAD_MO1 = 1048576;	
	public static int UPLOAD_INTERVAL = 500000;
	
	//accepte les chiffres 
	public static String regExNoTel = "^[0-9\\/\\+\\(\\)\\-\\.\\s]+$";
	//même regex que le validator vaadin
	public static String regExMail = "^([a-zA-Z0-9_\\.\\-+])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$";
	
	//chaine de validation INE UL
	public static String chaineValidationNNE[] = new String[]{"A","B","C","D","E","F","G","H","J","K","L","M","N","P","R","S","T","U","V","W","X","Y","Z"};
	
	/*Security constants*/
	public static String SECURITY_CONNECT_PATH = "/connect";
	public static String SECURITY_LOGOUT_PATH = "/logout";
	public static String SECURITY_SWITCH_PATH = "/login/impersonate";
	public static String SECURITY_SWITCH_BACK_PATH = "/logout/impersonate";
}


