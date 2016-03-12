package fr.univlorraine.ecandidat.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.repositories.CentreCandidatureRepository;
import fr.univlorraine.ecandidat.repositories.GestionnaireRepository;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.DroitProfilGestionnaireWindow;
import fr.univlorraine.ecandidat.views.windows.ScolCentreCandidatureWindow;


/**
 * Gestion de l'entité centreCandidature
 * @author Kevin Hergalant
 */
@Component
public class CentreCandidatureController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient CentreCandidatureRepository centreCandidatureRepository;
	@Resource
	private transient GestionnaireRepository gestionnaireRepository;
	@Resource
	private transient OffreFormationController offreFormationController;
	@Resource
	private transient DateTimeFormatter formatterDate;


	
	/**
	 * @return liste des gestionnaires d'un ctrCand
	 */
	public List<Gestionnaire> getCentreCandidatureGest(CentreCandidature centre) {
		return gestionnaireRepository.findByCentreCandidatureIdCtrCand(centre.getIdCtrCand());
	} 
	
	/**
	 * @return liste des centreCandidatures
	 */
	public List<CentreCandidature> getCentreCandidatures() {
		return centreCandidatureRepository.findAll();
	}
	
	/**
	 * @return liste des centreCandidatures
	 */
	public List<CentreCandidature> getCentreCandidaturesEnService() {
		return centreCandidatureRepository.findByTesCtrCand(true);
	}
	
	/**
	 * @return liste des centreCandidatures
	 */
	public CentreCandidature getCentreCandidature(Integer id) {
		return centreCandidatureRepository.findOne(id);
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau centreCandidature.
	 */
	public void editNewCentreCandidature() {
		CentreCandidature centreCandidature = new CentreCandidature(userController.getCurrentUserLogin(),typeDecisionController.getTypeDecisionFavDefault(),parametreController.getNbVoeuxMax(), false);
		ScolCentreCandidatureWindow window = new ScolCentreCandidatureWindow(centreCandidature,true);
		window.addRecordCtrCandWindowListener(e->{
			if (userController.getCentreCandidature()==null){
				userController.setCentreCandidature(e);
				((MainUI)UI.getCurrent()).buildMenuCtrCand();
			}			
		});
		
		UI.getCurrent().addWindow(window);
	}
	
	/**
	 * Ouvre une fenêtre d'édition de centreCandidature.
	 * @param centreCandidature
	 */
	public void editCentreCandidature(CentreCandidature centreCandidature, Boolean isAdmin) {
		Assert.notNull(centreCandidature);

		/* Verrou */
		if (!lockController.getLockOrNotify(centreCandidature, null)) {
			return;
		}

		ScolCentreCandidatureWindow window = new ScolCentreCandidatureWindow(centreCandidature, isAdmin);
		window.addCloseListener(e->lockController.releaseLock(centreCandidature));
		UI.getCurrent().addWindow(window);
	}

	/** Enregistre un centreCandidature
	 * @param centreCandidature
	 * @return le centreCandidature
	 */
	public CentreCandidature saveCentreCandidature(CentreCandidature centreCandidature) {
		Assert.notNull(centreCandidature);
		
		/* Verrou */
		if (centreCandidature.getIdCtrCand()!=null && !lockController.getLockOrNotify(centreCandidature, null)) {
			return null;
		}
		/*Enregistrement du centre de candidature*/
		centreCandidature.setUserModCtrCand(userController.getCurrentUserLogin());
		centreCandidature = centreCandidatureRepository.saveAndFlush(centreCandidature);
		offreFormationController.addCtrCand(centreCandidature);

		lockController.releaseLock(centreCandidature);
		return centreCandidature;
	}

	/**
	 * Supprime une centreCandidature
	 * @param centreCandidature
	 */
	public void deleteCentreCandidature(CentreCandidature centreCandidature) {
		Assert.notNull(centreCandidature);

		if (!isAutorizedToDelete(centreCandidature)){
			return;
		}
		
		/* Verrou */
		if (!lockController.getLockOrNotify(centreCandidature, null)) {
			return;
		}
		
		String txtDelete = applicationContext.getMessage("ctrCand.window.confirmDelete", new Object[]{centreCandidature.getCodCtrCand()}, UI.getCurrent().getLocale());		
		if (centreCandidature.getGestionnaires().size()>0){
			txtDelete = txtDelete + " " + applicationContext.getMessage("ctrCand.window.delete.warning", null, UI.getCurrent().getLocale());
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(txtDelete, applicationContext.getMessage("ctrCand.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(centreCandidature, null)) {				
				centreCandidature.getGestionnaires().forEach(gest -> {
					droitProfilController.deleteDroitProfilInd(gest.getDroitProfilInd());
				});
				centreCandidature.getGestionnaires().clear();
				centreCandidatureRepository.delete(centreCandidature);
				offreFormationController.removeCtrCand(centreCandidature);
				/* Suppression du lock */
				lockController.releaseLock(centreCandidature);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(centreCandidature);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	/**
	 * Ajoute un profil à un gestionnaire
	 * @param ctrCand 
	 */
	public void addProfilToGestionnaire(CentreCandidature ctrCand) {
		/* Verrou */
		if (!lockController.getLockOrNotify(ctrCand, null)) {
			return;
		}
		
		DroitProfilGestionnaireWindow window = new DroitProfilGestionnaireWindow(ctrCand);
		window.addDroitProfilGestionnaireListener((individu, droit, loginApo ,centreGestion, isAllCommission, listeCommission)->{
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(ctrCand, null)) {
				if (droitProfilController.getProfilIndByCentreCandidatureAndLogin(ctrCand,individu).size()==0){
					Individu ind = droitProfilController.saveIndividu(individu);
					DroitProfilInd dpi = droitProfilController.saveProfilInd(ind, droit);
					Gestionnaire gest = new Gestionnaire(ctrCand,dpi,loginApo,centreGestion,isAllCommission, listeCommission);
					ctrCand.getGestionnaires().add(gest);
					centreCandidatureRepository.save(ctrCand);
				}else{
					Notification.show(applicationContext.getMessage("droitprofilind.gest.allready", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
			}
		});

		window.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(ctrCand);
		});
		UI.getCurrent().addWindow(window);
	}	
	
	/** Modifie un profil d'un gestionnaire
	 * @param gest
	 */
	public void updateProfilToGestionnaire(Gestionnaire gest) {
		/* Verrou */
		if (!lockController.getLockOrNotify(gest.getCentreCandidature(), null)) {
			return;
		}
		
		Assert.notNull(gest); 
		
		DroitProfilGestionnaireWindow window = new DroitProfilGestionnaireWindow(gest);
		window.addDroitProfilGestionnaireListener((individu, droit, loginApo ,centreGestion, isAllCommission, listeCommission)->{
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(gest.getCentreCandidature(), null)) {
				gest.getDroitProfilInd().setDroitProfil(droit);
				droitProfilController.saveProfilInd(gest.getDroitProfilInd());
				gest.setLoginApoGest(loginApo);
				gest.setSiScolCentreGestion(centreGestion);
				gest.setTemAllCommGest(isAllCommission);
				gest.setCommissions(listeCommission);
				gest.getCentreCandidature().setDatModCtrCand(LocalDateTime.now());
				centreCandidatureRepository.save(gest.getCentreCandidature());
			}
		});
		
		window.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(gest.getCentreCandidature());
		});
		UI.getCurrent().addWindow(window);
	}	
	
	/**
	 * Ajoute un profil à un gestionnaire
	 * @param gest 
	 */
	public void deleteProfilToGestionnaire(Gestionnaire gest) {
		/* Verrou */
		if (!lockController.getLockOrNotify(gest.getCentreCandidature(), null)) {
			return;
		}
		
		Assert.notNull(gest);

		/* Verrou */
		
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("droitprofilind.window.confirmDelete", new Object[]{gest.getDroitProfilInd().getDroitProfil().getCodProfil(),gest.getDroitProfilInd().getIndividu().getLoginInd()}, UI.getCurrent().getLocale()), applicationContext.getMessage("droitprofilind.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(gest.getCentreCandidature(), null)) {
				gest.getCentreCandidature().getGestionnaires().remove(gest);
				centreCandidatureRepository.save(gest.getCentreCandidature());
				droitProfilController.deleteDroitProfilInd(gest.getDroitProfilInd());
				/* Suppression du lock */
				lockController.releaseLock(gest.getCentreCandidature());
			}			
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(gest.getCentreCandidature());
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	
	/** Verifie qu'on a le droit de supprimer ce centre de candidature
	 * @param typeDecision
	 * @return
	 */
	private Boolean isAutorizedToDelete(CentreCandidature ctrCand){
		if (ctrCand.getPieceJustifs().size()>0){
			displayMsgErrorUnautorized(PieceJustif.class.getSimpleName());
			return false;
		}
		if (ctrCand.getFormulaires().size()>0){
			displayMsgErrorUnautorized(Formulaire.class.getSimpleName());
			return false;
		}
		if (ctrCand.getCommissions().size()>0){
			displayMsgErrorUnautorized(Commission.class.getSimpleName());
			return false;
		}else{
			return true;
		}
	}
	
	/**Affiche le message d'erreur
	 * @param className
	 */
	private void displayMsgErrorUnautorized(String className){
		Notification.show(applicationContext.getMessage("ctrCand.error.delete", new Object[]{className}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
	}
	
	/** Renvoi le centre de canidature actif pour l'utilisateur
	 * @return le centre de canidature actif
	 */
	public CentreCandidature getCentreCandidatureActif() {
		Integer idCtr = userController.getCentreCandidature().getIdCtrCand();
		if (idCtr!=null){
			return getCentreCandidature(idCtr);
		}
		return null;
	}
	
	/** Verifie l'unicité du code
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodCtrCandUnique(String cod, Integer id) {
		CentreCandidature ctrCand = centreCandidatureRepository.findByCodCtrCand(cod);
		if (ctrCand==null){
			return true;
		}else{
			if (ctrCand.getIdCtrCand().equals(id)){
				return true;
			}
		}
		return false;
	}
	
	/** Retourne les centre de canidatures d'un individu
	 * @return la liste des centre de canidature actifs
	 */
	public List<CentreCandidature> getListCentreCandidature(){
		if (userController.isScolCentrale()){
			return getCentreCandidaturesEnService();
		}else{
			List<CentreCandidature> listeToRet = new ArrayList<CentreCandidature>();
			 for (DroitProfilInd droitProfilInd : droitProfilController.getProfilIndCtrCandCurrentUser()){
				 Gestionnaire gestionnaire = droitProfilInd.getGestionnaire();
	        		if (gestionnaire!=null && gestionnaire.getCentreCandidature()!=null && gestionnaire.getCentreCandidature().getTesCtrCand()){
	        			listeToRet.add(gestionnaire.getCentreCandidature());
	        		}
			 }
			 return listeToRet;
		}
	}

	/** Renvoie une liste pour visualiser les parametres d'un centre cand
	 * @param ctrCand
	 * @param readOnly
	 * @return la liste d'affichage des parametres
	 */
	public List<SimpleTablePresentation> getListPresentation(CentreCandidature ctrCand, Boolean readOnly) {
		List<SimpleTablePresentation> liste = new ArrayList<SimpleTablePresentation>();
		if (readOnly){
			liste.add(new SimpleTablePresentation(1,CentreCandidature_.codCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.codCtrCand.getName(), null, UI.getCurrent().getLocale()), ctrCand.getCodCtrCand()));
			liste.add(new SimpleTablePresentation(2,CentreCandidature_.libCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.libCtrCand.getName(), null, UI.getCurrent().getLocale()), ctrCand.getLibCtrCand()));
			liste.add(new SimpleTablePresentation(3,CentreCandidature_.tesCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.tesCtrCand.getName(), null, UI.getCurrent().getLocale()), ctrCand.getTesCtrCand()));
		}else{		
			String completmentNbVoeuxMaxEtab = "";
			if (parametreController.getNbVoeuxMaxIsEtab()){
				completmentNbVoeuxMaxEtab = " "+applicationContext.getMessage("ctrCand.table.nbMaxVoeuxCtrCand.notused", null, UI.getCurrent().getLocale());
			}
			
			
			liste.add(new SimpleTablePresentation(1,CentreCandidature_.typeDecisionFav.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.typeDecisionFav.getName(), null, UI.getCurrent().getLocale()), ctrCand.getTypeDecisionFav()==null?null:ctrCand.getTypeDecisionFav().getLibTypDec()));
			liste.add(new SimpleTablePresentation(2,CentreCandidature_.temListCompCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.temListCompCtrCand.getName(), null, UI.getCurrent().getLocale()), ctrCand.getTemListCompCtrCand()));
			liste.add(new SimpleTablePresentation(3,CentreCandidature_.typeDecisionFavListComp.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.typeDecisionFavListComp.getName(), null, UI.getCurrent().getLocale()), ctrCand.getTypeDecisionFavListComp()==null?null:ctrCand.getTypeDecisionFavListComp().getLibTypDec()));
			liste.add(new SimpleTablePresentation(4,CentreCandidature_.nbMaxVoeuxCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.nbMaxVoeuxCtrCand.getName(), null, UI.getCurrent().getLocale()),ctrCand.getNbMaxVoeuxCtrCand()+completmentNbVoeuxMaxEtab));
			liste.add(new SimpleTablePresentation(5,CentreCandidature_.temDematCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.temDematCtrCand.getName(), null, UI.getCurrent().getLocale()), ctrCand.getTemDematCtrCand()));
			liste.add(new SimpleTablePresentation(6,CentreCandidature_.datDebDepotCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.datDebDepotCtrCand.getName(), null, UI.getCurrent().getLocale()), (ctrCand.getDatDebDepotCtrCand()==null)?null:formatterDate.format(ctrCand.getDatDebDepotCtrCand())));
			liste.add(new SimpleTablePresentation(7,CentreCandidature_.datFinDepotCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.datFinDepotCtrCand.getName(), null, UI.getCurrent().getLocale()), (ctrCand.getDatFinDepotCtrCand()==null)?null:formatterDate.format(ctrCand.getDatFinDepotCtrCand())));
			liste.add(new SimpleTablePresentation(8,CentreCandidature_.datAnalyseCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.datAnalyseCtrCand.getName(), null, UI.getCurrent().getLocale()), (ctrCand.getDatAnalyseCtrCand()==null)?null:formatterDate.format(ctrCand.getDatAnalyseCtrCand())));			
			liste.add(new SimpleTablePresentation(9,CentreCandidature_.datRetourCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.datRetourCtrCand.getName(), null, UI.getCurrent().getLocale()), (ctrCand.getDatRetourCtrCand()==null)?null:formatterDate.format(ctrCand.getDatRetourCtrCand())));
			liste.add(new SimpleTablePresentation(10,CentreCandidature_.datJuryCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.datJuryCtrCand.getName(), null, UI.getCurrent().getLocale()), (ctrCand.getDatJuryCtrCand()==null)?null:formatterDate.format(ctrCand.getDatJuryCtrCand())));
			liste.add(new SimpleTablePresentation(11,CentreCandidature_.datPubliCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.datPubliCtrCand.getName(), null, UI.getCurrent().getLocale()), (ctrCand.getDatPubliCtrCand()==null)?null:formatterDate.format(ctrCand.getDatPubliCtrCand())));
			liste.add(new SimpleTablePresentation(12,CentreCandidature_.datConfirmCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.datConfirmCtrCand.getName(), null, UI.getCurrent().getLocale()), (ctrCand.getDatConfirmCtrCand()==null)?null:formatterDate.format(ctrCand.getDatConfirmCtrCand())));
			liste.add(new SimpleTablePresentation(13,CentreCandidature_.infoCompCtrCand.getName(),applicationContext.getMessage("ctrCand.table." + CentreCandidature_.infoCompCtrCand.getName(), null, UI.getCurrent().getLocale()), ctrCand.getInfoCompCtrCand()));
		}
		return liste;
	}
}
