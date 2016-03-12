package fr.univlorraine.ecandidat.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.repositories.TypeDecisionRepository;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.ScolTypeDecisionWindow;

/**
 * Gestion de l'entité typeDecision
 * @author Kevin Hergalant
 */

@Component
public class TypeDecisionController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient TypeDecisionRepository typeDecisionRepository;

	
	/**
	 * @return le type de decision favorable par defaut
	 */
	public TypeDecision getTypeDecisionFavDefault(){
		return typeDecisionRepository.findByCodTypDec(NomenclatureUtils.TYP_DEC_FAVORABLE);
	}
	
	/**
	 * @return liste des typeDecisions
	 */
	public List<TypeDecision> getTypeDecisions() {
		return typeDecisionRepository.findAll();
	}
	
	/**
	 * @return liste des typeDecisions
	 */
	public List<TypeDecision> getTypeDecisionsEnService() {
		return typeDecisionRepository.findByTesTypDec(true);
	}
	
	/**
	 * @return liste des typeDecisions
	 */
	public List<TypeDecision> getTypeDecisionsFavorableEnService() {
		return typeDecisionRepository.findByTesTypDecAndTypeAvisCodTypAvis(true,NomenclatureUtils.TYP_AVIS_FAV);
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau typeDecision.
	 */
	public void editNewTypeDecision() {
		TypeDecision typ = new TypeDecision(userController.getCurrentUserLogin());
		typ.setI18nLibTypDec(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_TYP_DEC_LIB)));
		UI.getCurrent().addWindow(new ScolTypeDecisionWindow(typ));
	}
	
	/**
	 * Ouvre une fenêtre d'édition de typeDecision.
	 * @param typeDecision
	 */
	public void editTypeDecision(TypeDecision typeDecision) {
		Assert.notNull(typeDecision);

		/* Verrou */
		if (!lockController.getLockOrNotify(typeDecision, null)) {
			return;
		}
		ScolTypeDecisionWindow window = new ScolTypeDecisionWindow(typeDecision);
		window.addCloseListener(e->lockController.releaseLock(typeDecision));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un typeDecision
	 * @param typeDecision
	 */
	public void saveTypeDecision(TypeDecision typeDecision) {
		Assert.notNull(typeDecision);
		

		/* Verrou */
		if (typeDecision.getIdTypDec()!=null && !lockController.getLockOrNotify(typeDecision, null)) {
			return;
		}
		typeDecision.setUserModTypDec(userController.getCurrentUserLogin());
		typeDecision.setI18nLibTypDec(i18nController.saveI18n(typeDecision.getI18nLibTypDec()));
		typeDecision = typeDecisionRepository.saveAndFlush(typeDecision);
		
		lockController.releaseLock(typeDecision);
	}

	/**
	 * Supprime une typeDecision
	 * @param typeDecision
	 */
	public void deleteTypeDecision(TypeDecision typeDecision) {
		Assert.notNull(typeDecision);

		if (!isAutorizedToDelete(typeDecision)){
			return;
		}
		
		/* Verrou */
		if (!lockController.getLockOrNotify(typeDecision, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("typeDec.window.confirmDelete", new Object[]{typeDecision.getCodTypDec()}, UI.getCurrent().getLocale()), applicationContext.getMessage("typeDec.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(typeDecision, null)) {
				typeDecisionRepository.delete(typeDecision);
				/* Suppression du lock */
				lockController.releaseLock(typeDecision);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(typeDecision);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	/** Verifie qu'on a le droit de supprimer ce type de decision
	 * @param typeDecision
	 * @return true si on a le droit de supprimer ce type de decision
	 */
	private Boolean isAutorizedToDelete(TypeDecision typeDecision){
		if (typeDecision.getCentreCandidaturesFav().size()>0 || typeDecision.getCentreCandidaturesFavListComp().size()>0){
			displayMsgErrorUnautorized(CentreCandidature.class.getSimpleName());
			return false;
		}
		if (typeDecision.getFormationsFav().size()>0 || typeDecision.getFormationsFavListComp().size()>0){
			displayMsgErrorUnautorized(Formation.class.getSimpleName());
			return false;
		}
		if (typeDecision.getTypeDecisionCandidatures().size()>0){
			displayMsgErrorUnautorized(TypeDecisionCandidature.class.getSimpleName());
			return false;
		}
		return true;
	}
	
	/**Affiche le message d'erreur
	 * @param className
	 */
	private void displayMsgErrorUnautorized(String className){
		Notification.show(applicationContext.getMessage("typeDec.error.delete", new Object[]{className}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
	}
	
	/** Verifie l'unicité du code
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodTypeDecUnique(String cod, Integer id) {
		TypeDecision typeDecision = typeDecisionRepository.findByCodTypDec(cod);
		if (typeDecision==null){
			return true;
		}else{
			if (typeDecision.getIdTypDec().equals(id)){
				return true;
			}
		}
		return false;
	}
}
