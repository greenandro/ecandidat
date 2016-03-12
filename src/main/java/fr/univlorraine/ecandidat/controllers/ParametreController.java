package fr.univlorraine.ecandidat.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.repositories.ParametreRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.MaintenanceListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.ParametrePresentation;
import fr.univlorraine.ecandidat.views.windows.AdminParametreWindow;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;

/**
 * Gestion de l'entité parametres
 * @author Kevin Hergalant
 */
@Component
public class ParametreController {
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient ParametreRepository parametreRepository;
	
	/*Le service SI Scol*/
	@Resource(name="${siscol.implementation}")
	private SiScolGenericService siScolService;	
	
	/**
	 * @return liste des parametres
	 */
	public List<Parametre> getParametres() {
		if (getSIScolMode().equals(ConstanteUtils.SI_SCOL_APOGEE)){
			return parametreRepository.findAll();
		}
		List<Parametre> liste = parametreRepository.findAll();
		List<Parametre> listeToRet = new ArrayList<Parametre>();
		liste.forEach(e->{
			if (!e.getCodParam().equals(NomenclatureUtils.COD_PARAM_IS_FORM_COD_APO_OBLI) &&
					!e.getCodParam().equals(NomenclatureUtils.COD_PARAM_IS_UTILISE_OPI)
				){
				listeToRet.add(e);
			}
		});
		return listeToRet;
	}
	
	/**
	 * Ouvre une fenêtre d'édition de parametre.
	 * @param parametre
	 */
	public void editParametre(Parametre parametre) {
		Assert.notNull(parametre);

		/* Verrou */
		if (!lockController.getLockOrNotify(parametre, null)) {
			return;
		}
		AdminParametreWindow window = new AdminParametreWindow(parametre);
		window.addCloseListener(e->lockController.releaseLock(parametre));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un parametre
	 * @param parametre
	 * @param parametrePres 
	 */
	public void saveParametre(Parametre parametre, ParametrePresentation parametrePres) {
		Assert.notNull(parametre);
		Assert.notNull(parametrePres);
		

		/* Verrou */
		if (!lockController.getLockOrNotify(parametre, null)) {
			return;
		}

		/*Map le param de presentation dans un parametre*/
		if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_BOOLEAN)){
			parametre.setValParam(parametrePres.getValParamBoolean());
		}else if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_INTEGER)){
			parametre.setValParam(String.valueOf(parametrePres.getValParamInteger()));
		}else if (parametre.getTypParam().startsWith(NomenclatureUtils.TYP_PARAM_STRING)){
			parametre.setValParam(parametrePres.getValParamString());
		}
		
		parametreRepository.saveAndFlush(parametre);
		tableRefController.reloadMapParametre();
		lockController.releaseLock(parametre);
	}


	/** Retourne la taille maximale d'un string par rapport à son type : String(2) renvoi 2
	 * @param type
	 * @return la taille maximale
	 */
	public Integer getMaxLengthForString(String type){
		if (type != null && type.startsWith(NomenclatureUtils.TYP_PARAM_STRING)){
			Pattern patt = Pattern.compile("(\\d+)");
			Matcher match = patt.matcher(type);
			while(match.find()){
			    return Integer.valueOf(match.group());
			}
			return 0; 
		}
		return 0;
	}
	
	/**
	 * @return le mode de fonctionnement SiScol
	 */
	public String getSIScolMode(){
		if (siScolService.isImplementationApogee() == true){
			return ConstanteUtils.SI_SCOL_APOGEE;
		}else{
			return ConstanteUtils.SI_SCOL_NOT_APOGEE;
		}
	}
	
	/** Renvoie un parametre
	 * @param codParam
	 * @return le parametre
	 */
	public Parametre getParametre(String codParam){
		return tableRefController.getMapParametre().get(codParam);
	}

	/** Met en maintenance ou en service l'application-->batch
	 * @param enMaintenance
	 */
	public void changeMaintenanceParam(Boolean enMaintenance){
		Parametre parametre = tableRefController.getMapParametre().get(NomenclatureUtils.COD_PARAM_IS_MAINTENANCE);
		if (parametre!=null){
			parametre.setValParam(MethodUtils.getTemoinFromBoolean(enMaintenance));
			parametreRepository.saveAndFlush(parametre);
			tableRefController.reloadMapParametre();
		}
	}
	

	/** Met en maintenance ou en service l'application-->Bouton
	 * @param enMaintenance
	 * @param listener 
	 */
	public void changeMaintenanceStatut(Boolean enMaintenance, MaintenanceListener listener){
		Parametre parametre = getParametre(NomenclatureUtils.COD_PARAM_IS_MAINTENANCE);
		Boolean oldMaintenanceStatut = MethodUtils.getBooleanFromTemoin(parametre.getValParam());
		/* Verrou */
		if (!lockController.getLockOrNotify(parametre, null)) {
			return;
		}
		
		if (enMaintenance.equals(oldMaintenanceStatut)){
			String message = "admin.maintenance.nocorrect.";
			if (enMaintenance){
				message += "shutdown";
			}else{
				message += "wakeup";
			}			
			listener.changeModeMaintenance();
			Notification.show(applicationContext.getMessage(message, null, UI.getCurrent().getLocale()),Type.WARNING_MESSAGE);
			lockController.releaseLock(parametre);
		}else{
			String message = "admin.maintenance.confirm.";
			if (enMaintenance){
				message += "shutdown";
			}else{
				message += "wakeup";
			}
			ConfirmWindow win = new ConfirmWindow(applicationContext.getMessage(message, null, UI.getCurrent().getLocale()));
			win.addBtnOuiListener(e -> {
				changeMaintenanceParam(enMaintenance);				
				/*uiController.getUis().forEach(s->{
					SecurityContext context = (SecurityContext) s.getSession().getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
					System.out.println(context);
				});*/
				listener.changeModeMaintenance();		
				lockController.releaseLock(parametre);
			});
			UI.getCurrent().addWindow(win);			
		}
	}
	
	/** Renvoie une valeur entiere
	 * @param codParam
	 * @return la valeur integer
	 */
	private Integer getIntegerValue(String codParam){
		Parametre param = getParametre(codParam);
		if (param == null){
			return 0;
		}else{
			return Integer.valueOf(param.getValParam());
		}
	}
	
	/** Renvoie une valeur string
	 * @param codParam
	 * @return la valeur string
	 */
	private String getStringValue(String codParam){
		Parametre param = getParametre(codParam);
		if (param == null){
			return "";
		}else{
			return param.getValParam();
		}
	}
	
	/** Renvoie une valeur boolean
	 * @param codParam
	 * @return la valeur boolean
	 */
	private Boolean getBooleanValue(String codParam){
		Parametre param = getParametre(codParam);
		if (param == null){
			return MethodUtils.getBooleanFromTemoin(ConstanteUtils.TYP_BOOLEAN_NO);
		}else{
			return MethodUtils.getBooleanFromTemoin(param.getValParam());
		}
	}
	
	/**
	 * @return le nombre de voeux max par defaut
	 */
	public Integer getNbVoeuxMax(){
		return getIntegerValue(NomenclatureUtils.COD_PARAM_NB_VOEUX_MAX);
	}
	
	/**
	 * @return le nombre de voeux max par defaut
	 */
	public Boolean getNbVoeuxMaxIsEtab(){
		return getBooleanValue(NomenclatureUtils.COD_PARAM_NB_VOEUX_MAX_IS_ETAB);
	}
	
	/**
	 * @return le nombre de jour apres quoi les dossier archivés sont detruits
	 */
	public Integer getNbJourArchivage(){
		return getIntegerValue(NomenclatureUtils.COD_PARAM_NB_JOUR_ARCHIVAGE);
	}
	
	/**
	 * @return le nombre de jour apres quoi les comptes a minima sont detruits
	 */
	public Integer getNbJourKeepCptMin(){
		return getIntegerValue(NomenclatureUtils.COD_PARAM_NB_JOUR_KEEP_CPT_MIN);
	}

	/**
	 * @return le prefixe des dossiers
	 */
	public String getPrefixeNumDossCpt(){
		return getStringValue(NomenclatureUtils.COD_PARAM_PREFIXE_NUM_DOSS_CPT);
	}
	
	/**
	 * @return le prefixe des no OPI
	 */
	public String getPrefixeOPI(){
		return getStringValue(NomenclatureUtils.COD_PARAM_PREFIXE_OPI);
	}

	/**
	 * @return true si l'etablissement utilise les OPI
	 */
	public Boolean getIsUtiliseOpi(){
		return getBooleanValue(NomenclatureUtils.COD_PARAM_IS_UTILISE_OPI);
	}

	/**
	 * @return true si l'etablissement a l'ine obligatoire pour les francais
	 */
	public Boolean getIsIneObligatoireFr(){
		return getBooleanValue(NomenclatureUtils.COD_PARAM_IS_INE_OBLIGATOIRE_FR);
	}

	/**
	 * @return true si l'etablissement le code apogée est obligatoire pour les formations
	 */
	public Boolean getIsFormCodApoOblig(){
		return getBooleanValue(NomenclatureUtils.COD_PARAM_IS_FORM_COD_APO_OBLI);
	}

	/**
	 * @return true si l'etablissement utilise la demat'
	 */
	public Boolean getIsUtiliseDemat(){
		return getBooleanValue(NomenclatureUtils.COD_PARAM_IS_UTILISE_DEMAT);
	}
	
	/**
	 * @return la taille max d'un fichier en Mo
	 */
	public Integer getFileMaxSize(){
		return getIntegerValue(NomenclatureUtils.COD_PARAM_FILE_MAX_SIZE);
	}

	/**
	 * @return true si l'application est en maintenance 
	 */
	public Boolean getIsMaintenance(){
		return getBooleanValue(NomenclatureUtils.COD_PARAM_IS_MAINTENANCE);
	}
	
	/**
	 * @return true si l'application accepte les appel 
	 */
	public Boolean getIsAppel(){
		return getBooleanValue(NomenclatureUtils.COD_PARAM_IS_APPEL);
	}
	
	/**
	 * @return true si l'application accepte les appel 
	 */
	public Boolean getOpiImmediat(){
		return getBooleanValue(NomenclatureUtils.COD_PARAM_IS_OPI_IMMEDIAT);
	}
	
	/**
	 * @return le nombre de jour apres quoi l'histo de batch est effacé
	 */
	public Integer getNbJourKeepHistoBatch(){
		return getIntegerValue(NomenclatureUtils.COD_PARAM_NB_JOUR_KEEP_HISTO_BATCH);
	}
}
