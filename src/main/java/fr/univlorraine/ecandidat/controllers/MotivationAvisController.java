package fr.univlorraine.ecandidat.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.repositories.MotivationAvisRepository;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.ScolMotivationAvisWindow;

/**
 * Gestion de l'entité motivationAvis
 * @author Kevin Hergalant
 */
@Component
public class MotivationAvisController {
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
	private transient MotivationAvisRepository motivationAvisRepository;
	

	
	/**
	 * @return liste des motivationAvis
	 */
	public List<MotivationAvis> getMotivationAvis() {
		return motivationAvisRepository.findAll();
	}
	
	/**
	 * @return liste des motivationAvis
	 */
	public List<MotivationAvis> getMotivationAvisEnService() {
		return motivationAvisRepository.findByTesMotiv(true);
	}
	
	/**
	 * Ouvre une fenêtre d'édition d'un nouveau motivationAvis.
	 */
	public void editNewMotivationAvis() {
		MotivationAvis motiv = new MotivationAvis(userController.getCurrentUserLogin());
		motiv.setI18nLibMotiv(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_MOTIV_LIB)));
		UI.getCurrent().addWindow(new ScolMotivationAvisWindow(motiv));
	}
	
	/**
	 * Ouvre une fenêtre d'édition de motivationAvis.
	 * @param motivationAvis
	 */
	public void editMotivationAvis(MotivationAvis motivationAvis) {
		Assert.notNull(motivationAvis);

		/* Verrou */
		if (!lockController.getLockOrNotify(motivationAvis, null)) {
			return;
		}
		ScolMotivationAvisWindow window = new ScolMotivationAvisWindow(motivationAvis);
		window.addCloseListener(e->lockController.releaseLock(motivationAvis));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un motivationAvis
	 * @param motivationAvis
	 */
	public void saveMotivationAvis(MotivationAvis motivationAvis) {
		Assert.notNull(motivationAvis);
		

		/* Verrou */
		if (motivationAvis.getIdMotiv()!=null && !lockController.getLockOrNotify(motivationAvis, null)) {
			return;
		}
		motivationAvis.setUserModMotiv(userController.getCurrentUserLogin());
		motivationAvis.setI18nLibMotiv(i18nController.saveI18n(motivationAvis.getI18nLibMotiv()));
		motivationAvis = motivationAvisRepository.saveAndFlush(motivationAvis);
		
		lockController.releaseLock(motivationAvis);
	}

	/**
	 * Supprime une motivationAvis
	 * @param motivationAvis
	 */
	public void deleteMotivationAvis(MotivationAvis motivationAvis) {
		Assert.notNull(motivationAvis);

		if (motivationAvis.getTypeDecisionCandidatures().size()>0){
			Notification.show(applicationContext.getMessage("motivAvis.error.delete", new Object[]{TypeDecisionCandidature.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		/* Verrou */
		if (!lockController.getLockOrNotify(motivationAvis, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("motivAvis.window.confirmDelete", new Object[]{motivationAvis.getCodMotiv()}, UI.getCurrent().getLocale()), applicationContext.getMessage("motivAvis.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(motivationAvis, null)) {
				motivationAvisRepository.delete(motivationAvis);
				/* Suppression du lock */
				lockController.releaseLock(motivationAvis);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(motivationAvis);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Verifie l'unicité du code
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodMotivUnique(String cod, Integer id) {
		MotivationAvis motiv = motivationAvisRepository.findByCodMotiv(cod);
		if (motiv==null){
			return true;
		}else{
			if (motiv.getIdMotiv().equals(id)){
				return true;
			}
		}
		return false;
	}
}
