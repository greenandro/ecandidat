package fr.univlorraine.ecandidat.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.repositories.CentreCandidatureRepository;
import fr.univlorraine.ecandidat.repositories.CommissionRepository;
import fr.univlorraine.ecandidat.repositories.DroitFonctionnaliteRepository;
import fr.univlorraine.ecandidat.repositories.DroitProfilIndRepository;
import fr.univlorraine.ecandidat.repositories.DroitProfilRepository;
import fr.univlorraine.ecandidat.repositories.GestionnaireRepository;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.AdminDroitProfilWindow;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.DroitProfilIndividuWindow;

/**
 * Gestion des profils et droits
 * @author Kevin Hergalant
 */
@Component
public class DroitProfilController {
	
	/* Injections */
	@Resource
	private transient Environment environment;
	@Resource
	private transient ApplicationContext applicationContext;
	
	@Resource
	private transient LockController lockController;
	
	@Resource
	private transient UserController userController;
	
	@Resource
	private transient DroitProfilRepository droitProfilRepository;
	@Resource
	private transient DroitFonctionnaliteRepository droitFonctionnaliteRepository;
	@Resource
	private transient DroitProfilIndRepository droitProfilIndRepository;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient CentreCandidatureRepository centreCandidatureRepository;
	@Resource
	private transient CommissionRepository commissionRepository;
	@Resource
	private transient GestionnaireRepository gestionnaireRepository;

	/**
	 * @return liste des droitProfils
	 */
	public List<DroitProfil> getDroitProfils() {
		return droitProfilRepository.findAll();
	}
	
	/**
	 * @return un droitProfil sans liste (admin, scolcentral)
	 */
	public DroitProfil getDroitProfil(String codProfil) {
		return droitProfilRepository.findByCodProfil(codProfil);
	}
		
	/**
	 * @return liste des getDroitUsersProfils
	 */
	public List<DroitProfilInd> getDroitProfilInds() {
		List<DroitProfilInd> liste = new ArrayList<DroitProfilInd>();
		/*if (userController.isAdmin()){
			DroitProfil droitProfil = new DroitProfil(NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH,NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,true,false,false,true);
			Individu ind = new Individu(environment.getRequiredProperty("admin.technique"),environment.getRequiredProperty("admin.technique"),null);
			liste.add(new DroitProfilInd(ind,droitProfil));
			liste.addAll(droitProfilIndRepository.findByDroitProfilTemAdminProfil(true));
		}else{
			liste.addAll(droitProfilIndRepository.findByDroitProfilCodProfil(NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE));
		}*/
		DroitProfil droitProfil = new DroitProfil(NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH,NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH,NomenclatureUtils.USER_NOMENCLATURE,NomenclatureUtils.USER_NOMENCLATURE,true,false,false,true);
		Individu ind = new Individu(environment.getRequiredProperty("admin.technique"),environment.getRequiredProperty("admin.technique"),null);
		liste.add(new DroitProfilInd(ind,droitProfil));
		liste.addAll(droitProfilIndRepository.findByDroitProfilTemAdminProfil(true));
		
		return liste;
	}
	
	/**
	 * @return liste des droitFonctionnalite
	 */
	public List<DroitFonctionnalite> getDroitFonctionnalites() {
		return droitFonctionnaliteRepository.findAll();
	}
	
	/** Verifie que le code du profil n'existe pas
	 * @param codProfil
	 * @return true si le code existe
	 */
	public Boolean existCodeProfil(String codProfil){
		return droitProfilRepository.findByCodProfil(codProfil)!=null;
	}
	
	/**
	 * Ouvre une fenêtre d'édition d'un droitProfil.
	 */
	public void editNewDroitProfil() {
		UI.getCurrent().addWindow(new AdminDroitProfilWindow(new DroitProfil(userController.getCurrentUserLogin(),true)));
	}

	/**
	 * Ouvre une fenêtre d'édition de droitProfil.
	 * @param droitProfil
	 */
	public void editDroitProfil(DroitProfil droitProfil) {
		Assert.notNull(droitProfil);

		/* Verrou */
		if (!lockController.getLockOrNotify(droitProfil, null)) {
			return;
		}

		AdminDroitProfilWindow window = new AdminDroitProfilWindow(droitProfil);
		window.addCloseListener(e->lockController.releaseLock(droitProfil));
		UI.getCurrent().addWindow(window);
	}
	
	/** Renvoie la liste proposee lors de l'ajout de profil
	 * @param type
	 * @return la liste des droits
	 */
	public List<DroitProfil> getListDroitProfilByType(String type){
		if (type.equals(NomenclatureUtils.DROIT_PROFIL_ADMIN)){
			return droitProfilRepository.findByTemAdminProfil(true);
		}
		else if (type.equals(NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE)){
			return droitProfilRepository.findByTemCtrCandProfil(true);
		}
		else if (type.equals(NomenclatureUtils.DROIT_PROFIL_COMMISSION)){
			List<DroitProfil> liste = new ArrayList<DroitProfil>();
			liste.add(droitProfilRepository.findByCodProfil(NomenclatureUtils.DROIT_PROFIL_COMMISSION));
			return liste;
		}
		return null;
	}
	
	/** Enregistre profil d'individu
	 * @param droit 
	 * @param ind 
	 * @return le profil rattaché a un individu
	 */
	public DroitProfilInd saveProfilInd(Individu ind, DroitProfil droit){
		return saveProfilInd(new DroitProfilInd(ind, droit));
	}
	
	/**
	 * @param dpi
	 * @return le profil rattaché a un individu
	 */
	public DroitProfilInd saveProfilInd(DroitProfilInd dpi){
		return droitProfilIndRepository.saveAndFlush(dpi);
	}
	
	
	/** Renvoi les profil d'individu par commission et login
	 * @param commission
	 * @param individu
	 * @return la liste des profil ind rattaché a une commission
	 */
	public List<DroitProfilInd> getProfilIndByCommissionAndLogin(Commission commission, Individu individu){
		return droitProfilIndRepository.findByCommissionMembreCommissionIdCommAndIndividuLoginInd(commission.getIdComm(),individu.getLoginInd());
	}
	
	/** Renvoi les profil d'individu par ctrCand et login
	 * @param ctrCand
	 * @param individu
	 * @return la liste des profil ind rattaché a un ctr
	 */
	public List<DroitProfilInd> getProfilIndByCentreCandidatureAndLogin(CentreCandidature ctrCand, Individu individu){
		return droitProfilIndRepository.findByGestionnaireCentreCandidatureIdCtrCandAndIndividuLoginInd(ctrCand.getIdCtrCand(),individu.getLoginInd());
	}
	
	/**
	 * @return les profil d'individu ctrCand de l'individu courant
	 */
	public List<DroitProfilInd> getProfilIndCtrCandCurrentUser(){
		return droitProfilIndRepository.findByIndividuLoginIndAndDroitProfilTemCtrCandProfil(userController.getCurrentUserLogin(),true);
	}

	/** Enregistre un droit profil
	 * @param droitProfil
	 * @param fonctionnaliteMap
	 */
	public void saveDroitProfil(DroitProfil droitProfil, HashMap<DroitFonctionnalite, Boolean> fonctionnaliteMap) {
		Assert.notNull(droitProfil);

		/* Verrou */
		if (droitProfil.getIdProfil()!=null && !lockController.getLockOrNotify(droitProfil, null)) {
			return;
		}
		
		final DroitProfil droitProfilSaved = droitProfilRepository.saveAndFlush(droitProfil);
		droitProfilSaved.getDroitProfilFoncs().clear();
		fonctionnaliteMap.forEach((k,v)->droitProfilSaved.addFonctionnalite(new DroitProfilFonc(k,droitProfilSaved,v)));
		
		
		droitProfilRepository.saveAndFlush(droitProfilSaved);
		lockController.releaseLock(droitProfilSaved);
	}
	
	/**
	 * Supprime un droitProfil
	 * @param droitProfil
	 */
	public void deleteDroitProfil(DroitProfil droitProfil) {
		Assert.notNull(droitProfil);

		if (droitProfil.getDroitProfilInds().size()>0){
			Notification.show(applicationContext.getMessage("droitprofil.error.delete", new Object[]{DroitProfilInd.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		/* Verrou */
		if (!lockController.getLockOrNotify(droitProfil, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("droitprofil.window.confirmDelete", new Object[]{droitProfil.getCodProfil()}, UI.getCurrent().getLocale()), applicationContext.getMessage("droitprofil.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(droitProfil, null)) {
				droitProfilRepository.delete(droitProfil);
				/* Suppression du lock */
				lockController.releaseLock(droitProfil);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(droitProfil);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	/**
	 * Ajoute un profil à un admin
	 */
	public void addProfilToAdmin(){
		DroitProfilIndividuWindow window = new DroitProfilIndividuWindow(NomenclatureUtils.DROIT_PROFIL_ADMIN);
		window.addDroitProfilIndividuListener((individu,droit)->{
			Individu ind = saveIndividu(individu);
			if (droitProfilIndRepository.findByDroitProfilCodProfilAndIndividuLoginInd(droit.getCodProfil(),individu.getLoginInd()).size()==0){
				droitProfilIndRepository.saveAndFlush(new DroitProfilInd(ind,droit));
			}else{
				Notification.show(applicationContext.getMessage("droitprofilind.allready", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
		});
		UI.getCurrent().addWindow(window);
	}
	
	/** Ajoute le droit de profil a un utilisateur via son login, nom et adrMail-->Util pour la demo
	 * @param login
	 * @param displayName
	 * @param adrMail
	 */
	public void addDroitProfilIndForAdmin(String login, String displayName, String adrMail){
		DroitProfil droit = droitProfilRepository.findByCodProfil(NomenclatureUtils.DROIT_PROFIL_ADMIN);
		Individu individu = saveIndividu(new Individu(login, displayName, adrMail));
		if (droit==null || individu==null){
			return;
		}
		if (droitProfilIndRepository.findByDroitProfilCodProfilAndIndividuLoginInd(droit.getCodProfil(),individu.getLoginInd()).size()==0){
			droitProfilIndRepository.saveAndFlush(new DroitProfilInd(individu,droit));
		}
	}
	
	/** Enregistre un individu (on le charge d'abord pour ne pas perdre le login apogée)
	 * @param individu
	 * @return l'individu
	 */
	public Individu saveIndividu(Individu individu){
		Individu individuLoad = individuController.getIndividu(individu.getLoginInd());
		if (individuLoad != null){
			individuLoad.setLibelleInd(individu.getLibelleInd());	
			return individuController.saveIndividu(individuLoad);
		}else{
			return individuController.saveIndividu(individu);
		}
	}
	
	/** SUpprime un droitProfilInd et supprime l'individu si il n'a pas d'autre role
	 * @param droitProfilInd
	 */
	public void deleteDroitProfilInd(DroitProfilInd droitProfilInd){		
		droitProfilIndRepository.delete(droitProfilInd);
		Individu individu = droitProfilInd.getIndividu();
		if (individu!=null){
			List<DroitProfilInd> liste = droitProfilIndRepository.findByIndividuLoginInd(individu.getLoginInd());
			if (liste!=null && liste.size()==0){
				individuController.deleteIndividu(individu);
			}
		}
	}
	
	/**
	 * Supprime un droitProfil
	 * @param droitProfilInd
	 */
	public void deleteProfilToUser(DroitProfilInd droitProfilInd) {
		Assert.notNull(droitProfilInd);

		/* Verrou */
		if (!lockController.getLockOrNotify(droitProfilInd, null)) {
			return;
		}
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("droitprofilind.window.confirmDelete", new Object[]{droitProfilInd.getDroitProfil().getCodProfil(),droitProfilInd.getIndividu().getLoginInd()}, UI.getCurrent().getLocale()), applicationContext.getMessage("droitprofilind.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(droitProfilInd, null) && droitProfilInd!=null) {
				deleteDroitProfilInd(droitProfilInd);
				/* Suppression du lock */
				lockController.releaseLock(droitProfilInd);
			}			
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(droitProfilInd);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}	
	
	/** Recupere le premier centre de candidature
	 * @return le premier centre de candidature pour un admin
	 */
	public CentreCandidature getCtrCandForAdmin(){
		Optional<CentreCandidature> ctrCandOpt = centreCandidatureRepository.findAll().stream().findFirst();
		if (ctrCandOpt.isPresent()){
			return ctrCandOpt.get();
		}
		return null;
	}

	/** Recupere la premiere commission
	 * @return la premiere commission pour un admin
	 */
	public Commission getCommissionForAdmin() {
		Optional<Commission> commissionOpt = commissionRepository.findAll().stream().findFirst();
		if (commissionOpt.isPresent()){
			return commissionOpt.get();
		}
		return null;
	}

	/** Renvoie les roles d'admin d'un individu
	 * @param username
	 * @return les roles d'admin d'un individu
	 */
	public List<DroitProfilInd> searchDroitAdminByLogin(String username) {
		return droitProfilIndRepository.findByIndividuLoginIndAndDroitProfilCodProfil(username,NomenclatureUtils.DROIT_PROFIL_ADMIN);
	}
	
	/** Renvoie les roles de scol central d'un individu
	 * @param username
	 * @return les roles de scol central d'un individu
	 */
	public List<DroitProfilInd> searchDroitScolCentralByLogin(String username) {
		return droitProfilIndRepository.findByIndividuLoginIndAndDroitProfilCodProfil(username,NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE);
	}

	/** Renvoie les roles d'un individu
	 * @param username
	 * @return les roles d'un individu
	 */
	public List<DroitProfilInd> searchDroitByLogin(String username) {
		return droitProfilIndRepository.findByIndividuLoginInd(username);
	}

	/** Renvoie les role d'un gestionnaire par rapport à son login et son centre de candidature
	 * @param idCtr
	 * @param username
	 * @return les role d'un gestionnaire par rapport à son login et son centre de candidature
	 */
	public List<DroitProfilInd> searchDroitByLoginAndIdCtrCand(Integer idCtr, String username) {
		return droitProfilIndRepository.findByGestionnaireCentreCandidatureIdCtrCandAndIndividuLoginInd(idCtr,username);
	}
	
	/** Renvoie les role d'un gestionnaire par rapport à son login et qu'il a une commission
	 * @param username
	 * @return les role d'un gestionnaire par rapport à son login et qu'il ai une commissio
	 */
	public List<DroitProfilInd> searchDroitByLoginAndIsCommissionMember(String username) {
		return droitProfilIndRepository.findByIndividuLoginIndAndCommissionMembreIsNotNull(username);
	}
}
