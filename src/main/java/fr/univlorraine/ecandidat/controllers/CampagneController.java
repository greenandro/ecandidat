package fr.univlorraine.ecandidat.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.repositories.CampagneRepository;
import fr.univlorraine.ecandidat.repositories.SiScolAnneeUniRepository;
import fr.univlorraine.ecandidat.views.windows.AdminCampagneWindow;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 *
 */
@Component
public class CampagneController {
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
	private transient ParametreController parametreController;
	@Resource
	private transient CampagneRepository campagneRepository;
	@Resource
	private transient SiScolAnneeUniRepository siScolAnneeUniRepository;
	
	private Campagne campagneEnService;

	
	/** Cherche les année univ valides
	 * @return les années univ valides
	 */
	public List<SiScolAnneeUni> getAnneeUnis(){
		return siScolAnneeUniRepository.findAll();
	}
	
	/**
	 * @return liste des campagnes
	 */
	public List<Campagne> getCampagnes() {
		return campagneRepository.findAll();
	}
	
	/**
	 * @return la campagne active
	 */
	public Campagne getCampagneEnService() {
		if (campagneEnService==null){
			List<Campagne> liste = campagneRepository.findByTesCampAndDatArchivCampIsNull(true);
			if (liste==null || liste.size()==0){
				return null;
			}
			campagneEnService = liste.get(0);
		}
		return campagneEnService;
	}
	
	/**
	 * @return la campagne active
	 */
	public Campagne getCampagneActive() {
		Campagne campagne = getCampagneEnService();
		if (campagne==null){
			return null;
		}
		
		if (campagne.getDatFinCamp().isBefore(LocalDate.now()) || campagne.getDatDebCamp().isAfter(LocalDate.now())){
			return null;
		}
		
		return campagne;
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau campagne.
	 */
	public void editNewCampagne() {
		List<Campagne> listeCampagneToActivate = campagneRepository.findByDatActivatPrevCampIsNotNullAndDatActivatEffecCampIsNull();
		if (listeCampagneToActivate.size()>0){
			Notification.show(applicationContext.getMessage("campagne.error.new", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		Campagne camp = getCampagneEnService();
		/* Verrou */
		if (camp!=null && !lockController.getLockOrNotify(camp, null)) {
			return;
		}
		
		AdminCampagneWindow window = new AdminCampagneWindow(new Campagne(), camp);
		window.addCloseListener(e->{
			if (camp!=null){
				lockController.releaseLock(camp);
			}			
		});
		UI.getCurrent().addWindow(window);
		
	}
	
	/**
	 * Ouvre une fenêtre d'édition de campagne.
	 * @param campagne
	 */
	public void editCampagne(Campagne campagne) {
		Assert.notNull(campagne);

		/* Verrou */
		if (!lockController.getLockOrNotify(campagne, null)) {
			return;
		}
		AdminCampagneWindow window = new AdminCampagneWindow(campagne, null);
		window.addCloseListener(e->lockController.releaseLock(campagne));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un campagne
	 * @param campagne
	 * @param campagneAArchiver 
	 */
	public void saveCampagne(Campagne campagne, Campagne campagneAArchiver) {
		Assert.notNull(campagne);
		if (campagne.getIdCamp()==null){
			if (campagneRepository.findAll().size()==0){
				campagne.setTesCamp(true);
				campagne.setDatActivatPrevCamp(LocalDateTime.now());
				campagne.setDatActivatEffecCamp(LocalDateTime.now());
			}else{
				campagne.setTesCamp(false);
			}
		}		

		/* Verrou */
		if (!lockController.getLockOrNotify(campagne, null)) {
			return;
		}
		
		if (campagneAArchiver!=null){
			if (!lockController.getLockOrNotify(campagneAArchiver, null)) {
				return;
			}
			campagne.setCampagneArchiv(campagneAArchiver);
		}
		
		campagne = campagneRepository.saveAndFlush(campagne);
		campagneEnService = null;
		lockController.releaseLock(campagne);
		if (campagneAArchiver!=null){
			lockController.releaseLock(campagneAArchiver);
		}
	}

	/**
	 * Supprime une campagne
	 * @param campagne
	 */
	public void deleteCampagne(Campagne campagne) {		
		Assert.notNull(campagne);
		
		if (campagne.getDatActivatEffecCamp()!=null){
			Notification.show(applicationContext.getMessage("campagne.error.delete.active", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		if (campagneRepository.findOne(campagne.getIdCamp()).getCompteMinimas().size()>0){
			Notification.show(applicationContext.getMessage("campagne.error.delete", new Object[]{CompteMinima.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		/* Verrou */
		if (!lockController.getLockOrNotify(campagne, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("campagne.window.confirmDelete", new Object[]{campagne.getCodCamp()}, UI.getCurrent().getLocale()), applicationContext.getMessage("campagne.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(campagne, null)) {
				campagneRepository.delete(campagne);
				/* Suppression du lock */
				lockController.releaseLock(campagne);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(campagne);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Archive une campagne et active l'autre
	 */
	public void archiveCampagne() {
		List<Campagne> listeCampagne = campagneRepository.findByDatActivatEffecCampIsNullAndDatActivatPrevCampIsNotNull();
		listeCampagne.forEach(campagne->{
			if (campagne.getDatActivatPrevCamp().isBefore(LocalDateTime.now())){
				campagne.setDatActivatEffecCamp(LocalDateTime.now());
				campagne.setTesCamp(true);
				campagne = campagneRepository.save(campagne);
				campagne.getCampagneArchiv().setDatArchivCamp(LocalDateTime.now());
				campagne.getCampagneArchiv().setTesCamp(false);
				campagneRepository.save(campagne.getCampagneArchiv());
				campagneEnService = null;
			}			
		});
	}
	
	public Campagne saveDateDestructionCampagne(Campagne campagne){
		campagne.setDatDestructEffecCamp(LocalDateTime.now());
		campagne.setCompteMinimas(new ArrayList<CompteMinima>());
		return campagneRepository.save(campagne);
	}
	
	/** 
	 * @param camp
	 * @return la date prévisionnelle de destruction de dossier
	 */
	public LocalDateTime getDateDestructionDossier(Campagne camp){
		if (camp.getDatArchivCamp()!=null){
			return camp.getDatArchivCamp().plusDays(parametreController.getNbJourArchivage());
		}
		return null;
	}

	/** Verifie que le code de la campagne est unique
	 * @param cod
	 * @param idCamp
	 * @return true si le code est unique
	 */
	public Boolean isCodCampUnique(String cod, Integer idCamp) {
		Campagne camp = campagneRepository.findByCodCamp(cod);
		if (camp==null){
			return true;
		}else{
			if (camp.getIdCamp().equals(idCamp)){
				return true;
			}
		}
		return false;
	}
}
