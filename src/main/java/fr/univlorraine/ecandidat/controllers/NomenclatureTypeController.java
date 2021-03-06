package fr.univlorraine.ecandidat.controllers;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.repositories.TypeStatutPieceRepository;
import fr.univlorraine.ecandidat.repositories.TypeStatutRepository;
import fr.univlorraine.ecandidat.repositories.TypeTraitementRepository;
import fr.univlorraine.ecandidat.views.windows.ScolTypeStatutPieceWindow;
import fr.univlorraine.ecandidat.views.windows.ScolTypeStatutWindow;
import fr.univlorraine.ecandidat.views.windows.ScolTypeTraitementWindow;

/**
 * Gestion de la nomenclature des types
 * @author Kevin Hergalant
 */
@Component
public class NomenclatureTypeController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient TypeStatutRepository typeStatutRepository;
	@Resource
	private transient TypeStatutPieceRepository typeStatutPieceRepository;
	
	@Resource
	private transient TypeTraitementRepository typeTraitementRepository;
	
	/**
	 * @return liste des typeTraitements
	 */
	public List<TypeTraitement> getTypeTraitements() {
		return typeTraitementRepository.findAll();
	}
	
	/**
	 * Ouvre une fenêtre d'édition de typeTraitement.
	 * @param typeTraitement
	 */
	public void editTypeTraitement(TypeTraitement typeTraitement) {
		Assert.notNull(typeTraitement);
		/* Verrou */
		if (!lockController.getLockOrNotify(typeTraitement, null)) {
			return;
		}
		ScolTypeTraitementWindow window = new ScolTypeTraitementWindow(typeTraitement);
		window.addCloseListener(e->lockController.releaseLock(typeTraitement));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un typeTraitement
	 * @param typeTraitement
	 */
	public void saveTypeTraitement(TypeTraitement typeTraitement) {
		Assert.notNull(typeTraitement);
		
		/* Verrou */
		if (!lockController.getLockOrNotify(typeTraitement, null)) {
			return;
		}
		typeTraitement.setI18nLibTypTrait(i18nController.saveI18n(typeTraitement.getI18nLibTypTrait()));
		typeTraitement.setDatModTypTrait(LocalDateTime.now());
		typeTraitement = typeTraitementRepository.saveAndFlush(typeTraitement);
		
		lockController.releaseLock(typeTraitement);
	}
	
	/**
	 * @return liste des typeStatuts
	 */
	public List<TypeStatut> getTypeStatuts() {
		return typeStatutRepository.findAll();
	}
	
	/**
	 * Ouvre une fenêtre d'édition de typeStatut.
	 * @param typeStatut
	 */
	public void editTypeStatut(TypeStatut typeStatut) {
		Assert.notNull(typeStatut);
		/* Verrou */
		if (!lockController.getLockOrNotify(typeStatut, null)) {
			return;
		}
		ScolTypeStatutWindow window = new ScolTypeStatutWindow(typeStatut);
		window.addCloseListener(e->lockController.releaseLock(typeStatut));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un typeStatut
	 * @param typeStatut
	 */
	public void saveTypeStatut(TypeStatut typeStatut) {
		Assert.notNull(typeStatut);
		
		/* Verrou */
		if (!lockController.getLockOrNotify(typeStatut, null)) {
			return;
		}
		typeStatut.setI18nLibTypStatut(i18nController.saveI18n(typeStatut.getI18nLibTypStatut()));
		typeStatut.setDatModTypStatut(LocalDateTime.now());
		typeStatut = typeStatutRepository.saveAndFlush(typeStatut);
		
		lockController.releaseLock(typeStatut);
	}

	/**
	 * @return liste des typeStatutPieces
	 */
	public List<TypeStatutPiece> getTypeStatutPieces() {
		return typeStatutPieceRepository.findAll();
	}
	
	/**
	 * Ouvre une fenêtre d'édition de typeStatutPiece.
	 * @param typeStatutPiece
	 */
	public void editTypeStatutPiece(TypeStatutPiece typeStatutPiece) {
		Assert.notNull(typeStatutPiece);
		/* Verrou */
		if (!lockController.getLockOrNotify(typeStatutPiece, null)) {
			return;
		}
		ScolTypeStatutPieceWindow window = new ScolTypeStatutPieceWindow(typeStatutPiece);
		window.addCloseListener(e->lockController.releaseLock(typeStatutPiece));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un typeStatutPiece
	 * @param typeStatutPiece
	 */
	public void saveTypeStatutPiece(TypeStatutPiece typeStatutPiece) {
		Assert.notNull(typeStatutPiece);
		
		/* Verrou */
		if (!lockController.getLockOrNotify(typeStatutPiece, null)) {
			return;
		}
		typeStatutPiece.setI18nLibTypStatutPiece(i18nController.saveI18n(typeStatutPiece.getI18nLibTypStatutPiece()));
		typeStatutPiece.setDatModTypStatutPiece(LocalDateTime.now());
		typeStatutPiece = typeStatutPieceRepository.saveAndFlush(typeStatutPiece);
		
		lockController.releaseLock(typeStatutPiece);
	}
}
