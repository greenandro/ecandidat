package fr.univlorraine.ecandidat.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.repositories.LangueRepository;
import fr.univlorraine.ecandidat.views.windows.AdminLangueWindow;

/**
 * Gestion de l'entité langue
 * @author Kevin Hergalant
 *
 */
@Component
public class LangueController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient LangueRepository langueRepository;	
	@Resource
	private transient TableRefController tableRefController;
	
	/**
	 * @return liste des langues
	 */
	public List<Langue> getLangues() {
		return langueRepository.findAll();
	}

	/**
	 * Ouvre une fenêtre d'édition de langue.
	 * @param langue
	 */
	public void editLangue(Langue langue) {
		Assert.notNull(langue);

		/* Verrou */
		if (!lockController.getLockOrNotify(langue, null)) {
			return;
		}

		AdminLangueWindow window = new AdminLangueWindow(langue);
		window.addCloseListener(e->lockController.releaseLock(langue));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un langue
	 * @param langue
	 */
	public void saveLangue(Langue langue) {
		Assert.notNull(langue);

		/* Verrou */
		if (!lockController.getLockOrNotify(langue, null)) {
			return;
		}

		langueRepository.saveAndFlush(langue);
		tableRefController.reloadLangues();
		lockController.releaseLock(langue);
	}


}
