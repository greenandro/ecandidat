package fr.univlorraine.ecandidat.controllers;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.siscol.Vet;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandFormationWindow;
import fr.univlorraine.ecandidat.views.windows.ScolPieceComplementaireWindow;

/**
 * Gestion de l'entité formation
 * @author Kevin Hergalant
 *
 */
@Component
public class FormationController {
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
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient FormationRepository formationRepository;
	/*Le service SI Scol*/
	@Resource(name="${siscol.implementation}")
	private SiScolGenericService siScolService;
	@Resource
	private transient OffreFormationController offreFormationController;

	
	/**
	 * @return liste des formations
	 */
	public List<Formation> getFormations() {
		return formationRepository.findAll();
	}
	
	/**
	 * @param ctrCand
	 * @return liste des formations d'un centre de candidature
	 */
	public List<Formation> getFormationsByCtrCand(CentreCandidature ctrCand) {
		return formationRepository.findByCommissionCentreCandidatureIdCtrCand(ctrCand.getIdCtrCand());	
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau formation.
	 */
	public void editNewFormation(CentreCandidature ctrCand) {
		Formation form = new Formation(userController.getCurrentUserLogin());
		UI.getCurrent().addWindow(new CtrCandFormationWindow(form,ctrCand));
	}
	
	/**Ouvre une fenêtre d'édition de formation.
	 * @param formation
	 * @param ctrCand
	 */
	public void editFormation(Formation formation,CentreCandidature ctrCand) {
		Assert.notNull(formation);

		/* Verrou */
		if (!lockController.getLockOrNotify(formation, null)) {
			return;
		}

		CtrCandFormationWindow window = new CtrCandFormationWindow(formation,ctrCand);
		window.addCloseListener(e->lockController.releaseLock(formation));
		UI.getCurrent().addWindow(window);
	}
	
	/** Edite les pieces complémentaires d'une formation
	 * @param formation
	 * @param ctrCand
	 */
	public void editPieceCompFormation(Formation formation, CentreCandidature ctrCand) {
		Assert.notNull(formation);

		/* Verrou */
		if (!lockController.getLockOrNotify(formation, null)) {
			return;
		}

		ScolPieceComplementaireWindow window = new ScolPieceComplementaireWindow(formation,ctrCand);
		window.addCloseListener(e->lockController.releaseLock(formation));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un formation
	 * @param formation
	 */
	public void saveFormation(Formation formation) {
		Assert.notNull(formation);

		/* Verrou */
		if (formation.getIdForm()!=null && !lockController.getLockOrNotify(formation, null)) {
			return;
		}
		formation.setUserModForm(userController.getCurrentUserLogin());
		formation = formationRepository.saveAndFlush(formation);
		offreFormationController.addFormation(formation);
		lockController.releaseLock(formation);
	}
	
	/** Enregistre les pieces comp d'une formation
	 * @param formation
	 * @param listFormulaire
	 * @param listPj
	 */
	public void savePiecesComplementaires(final Formation formation,
			List<Formulaire> listFormulaire,
			List<PieceJustif> listPj) {
		Assert.notNull(formation);

		/* Verrou */
		if (!lockController.getLockOrNotify(formation, null)) {
			return;
		}
		
		formation.setFormulaires(listFormulaire);
		formation.setPieceJustifs(listPj);
		
		formationRepository.saveAndFlush(formation);
		
		lockController.releaseLock(formation);
	}

	/**
	 * Supprime une formation
	 * @param formation
	 */
	public void deleteFormation(Formation formation) {
		Assert.notNull(formation);

		if (formation.getCandidatures().size()>0){
			Notification.show(applicationContext.getMessage("formation.error.delete", new Object[]{Candidature.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		/* Verrou */
		if (!lockController.getLockOrNotify(formation, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("formation.window.confirmDelete", new Object[]{formation.getCodForm()}, UI.getCurrent().getLocale()), applicationContext.getMessage("formation.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(formation, null)) {
				formationRepository.delete(formation);
				offreFormationController.removeFormation(formation);
				/* Suppression du lock */
				lockController.releaseLock(formation);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(formation);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Verifie l'unicité du code
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodFormUnique(String cod, Integer id) {
		Formation form = formationRepository.findByCodForm(cod);
		if (form==null){
			return true;
		}else{
			if (form.getIdForm().equals(id)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param search
	 * @return la liste des VET d'un CGE et d'une recherche
	 * @throws SiScolException 
	 */
	public List<Vet> getVetByCGE(String search) throws SiScolException{
		if (siScolService.isImplementationApogee()){
			SecurityCentreCandidature ctrCand = userController.getCentreCandidature();
			if (ctrCand!=null){
				if (ctrCand.getIsAdmin()){
					return siScolService.getListFormation(null,search);
				}else{
					if (ctrCand.getCodCGE()!=null){
						return siScolService.getListFormation(ctrCand.getCodCGE(),search);
					}
				}				
			}
		}
		return new ArrayList<Vet>();
	}
}
