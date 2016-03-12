package fr.univlorraine.ecandidat.controllers;

import java.util.Locale;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.I18nTraduction;
import fr.univlorraine.ecandidat.entities.ecandidat.I18nTraductionPK;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraduction;
import fr.univlorraine.ecandidat.repositories.I18nRepository;
import fr.univlorraine.ecandidat.repositories.TypeTraductionRepository;
import fr.univlorraine.ecandidat.services.security.SecurityUserCandidat;


/**
 * Gestion de tout ce qui est internationalisation
 * @author Kevin Hergalant
 */
@Component
public class I18nController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient I18nRepository i18nRepository;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient TypeTraductionRepository typeTraductionRepository;
	
	/** Enregistre une entité i18n
	 * @param i18nMaybeIncomplet
	 * @return l'entite I18n créé
	 */
	public I18n saveI18n(I18n i18nMaybeIncomplet){
		if (i18nMaybeIncomplet.getIdI18n()==null){
			I18n i18n = i18nRepository.save(new I18n(i18nMaybeIncomplet.getTypeTraduction()));
			i18nMaybeIncomplet.getI18nTraductions().forEach(e -> {
				e.setI18n(i18n);
				e.setId(new I18nTraductionPK(i18n.getIdI18n(),e.getLangue().getCodLangue()));
				i18n.getI18nTraductions().add(e);
			});
			return i18nRepository.save(i18n);
		}else{
			return i18nRepository.save(i18nMaybeIncomplet);
		}		
	}
	
	/**
	 * @param typeTraduction
	 * @return le type de traduction
	 */
	public TypeTraduction getTypeTraduction(String typeTraduction){
		return typeTraductionRepository.findOne(typeTraduction);
	}
	
	/** Renvoi la valeur d'une traduction (langue default si plus d'une traduction)
	 * @param i18n
	 * @return la valeur d'une traduction 
	 */
	public String getI18nTraduction(I18n i18n){
		return getI18nTraduction(i18n,getLangueCandidat());
	}
	
	/** Renvoi la valeur d'un traduction (langue default si plus d'une traduction)
	 * @param i18n
	 * @param locale
	 * @return la valeur d'un traduction par une locale
	 */
	public String getI18nTraduction(I18n i18n, Locale locale){
		String codLangue = null;
		if (locale!=null){
			codLangue = locale.getLanguage();
		}
		return getI18nTraduction(i18n,codLangue);
	}
	
	/**
	 * @param i18n
	 * @return les traductions sous forme de libellé
	 */
	public String getI18nTraductionLibelle(I18n i18n){
		StringBuilder ret = new StringBuilder("");
		i18n.getI18nTraductions().forEach(e->{
			ret.append(e.getValTrad()+"; ");
		});
		return ret.toString();
	}
	

	/**Renvoi la valeur d'un traduction (langue default si plus d'une traduction)
	 * @param i18n
	 * @param codLangueCand
	 * @return la valeur d'un traduction par une langue
	 */
	public String getI18nTraduction(I18n i18n, String codLangueCand){
		if (i18n.getI18nTraductions().size()==0){
			return null;
		}else if (i18n.getI18nTraductions().size()==1){
			return i18n.getI18nTraductions().get(0).getValTrad();
		}else{
			if (codLangueCand != null){
				Optional<I18nTraduction> i18nTraductionPref = i18n.getI18nTraductions().stream().filter(t -> t.getLangue().getCodLangue().equals(codLangueCand)).findFirst();
				if (i18nTraductionPref.isPresent()){
					return i18nTraductionPref.get().getValTrad();
				}
			}else{
				Optional<I18nTraduction> i18nTraductionDefault = i18n.getI18nTraductions().stream().filter(t -> t.getLangue().equals(tableRefController.getLangueDefault())).findFirst();
				if (i18nTraductionDefault.isPresent()){
					return i18nTraductionDefault.get().getValTrad();
				}
			}
		}
		return null;
	}
	
	/**
	 * Change la langue de l'utilisateur-->verifie qu'elle existe d'abord et est
	 * active
	 * 
	 * @return true si la langue a été changée
	 */
	public Boolean changeLangue(Langue langue) {
		return changeLangueUI(getCodeLangueActive(langue.getCodLangue()),false);
	}
	
	/**
	 * @param langue
	 * @return la langue active
	 */
	private String getCodeLangueActive(String codLangue){
		String codLangueDefault = tableRefController.getLangueDefault().getCodLangue();
		if (!codLangue.equals(codLangueDefault)) {
			Optional<Langue> langueFilter = tableRefController.getLangueEnService().stream()
					.filter(e -> e.getCodLangue().equals(codLangue)).findAny();
			if (langueFilter.isPresent()) {
				return codLangue;
			}
		}
		return codLangueDefault;
	}
	
	/**
	 * @return la langue préférée du candidat
	 */
	public String getLangueCandidat() {
		SecurityUserCandidat user = userController.getSecurityUserCandidat();
		if (user != null) {
			return user.getCodLangue();
		} else {
			if (UI.getCurrent().getLocale() != null) {
				return UI.getCurrent().getLocale().getLanguage();
			} else {
				return tableRefController.getLangueDefault().getCodLangue();
			}
		}
	}

	/**
	 * Change la langue
	 * @param codLangue
	 */
	public void setLangueCandidat(String codLangue) {
		SecurityUserCandidat user = userController.getSecurityUserCandidat();
		if (user != null) {
			user.setCodLangue(codLangue);
		}
	}
	
	/**
	 * Initialise la langue lors de l'arrivée sur l'UI
	 */
	public void initLanguageUI(Boolean forceToReloadMenu){
		
		/*Mise a jour de la langue*/
		String langue = tableRefController.getLangueDefault().getCodLangue();
		SecurityUserCandidat user = userController.getSecurityUserCandidat();
		if (user != null && user.getCodLangue()!=null) {
			langue = getCodeLangueActive(user.getCodLangue());
		}
		
		if (langue != null){
			changeLangueUI(langue, forceToReloadMenu);
		}else{
			Langue langueDefault = tableRefController.getLangueDefault();
			if (langueDefault != null) {
				changeLangueUI(langueDefault.getCodLangue(), forceToReloadMenu);
			}
		}
	}
	
	/** Change la langue de l'UI
	 * @param codeLangue le code langage de la locale
	 * @param forceToReloadMenu si le menu doit être forcé à être rechargé-->cas du candidat en connexion interne
	 * @return true si la langue a été& changée
	 */
	public Boolean changeLangueUI(String codeLangue, Boolean forceToReloadMenu){
		if (codeLangue==null || UI.getCurrent()==null){
			return false;
		}
		Locale locale = UI.getCurrent().getLocale();
		
		if (forceToReloadMenu || locale == null || locale.getLanguage() == null
				|| (codeLangue != null && !codeLangue.equals(locale.getLanguage()))) {
			setLangueCandidat(codeLangue);
			((MainUI) UI.getCurrent()).setLocale(new Locale(codeLangue));
			((MainUI) UI.getCurrent()).configReconnectDialogMessages();		
			((MainUI) UI.getCurrent()).constructMainMenu();
			
			return true;
		}
		return false;
	}
}