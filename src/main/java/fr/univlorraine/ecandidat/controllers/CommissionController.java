package fr.univlorraine.ecandidat.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CommissionMembre;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.repositories.CommissionRepository;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandCommissionWindow;
import fr.univlorraine.ecandidat.views.windows.DroitProfilMembreCommWindow;

/**
 * Gestion de l'entité commission
 * @author Kevin Hergalant
 */
@Component
public class CommissionController {
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
	private transient CommissionRepository commissionRepository;	

	
	/**
	 * @return liste des commissions
	 */
	public List<Commission> getCommissionsByCtrCand(CentreCandidature ctrCand) {
		return commissionRepository.findByCentreCandidatureIdCtrCand(ctrCand.getIdCtrCand());
	}
	
	public Commission getCommissionById(Integer idComm) {
		return commissionRepository.findOne(idComm);
	}
	
	/**
	 * @param ctrCand
	 * @param isGestAllCommission
	 * @param listeIdCommission
	 * @return les commissions d'un centre de candidature
	 */
	public List<Commission> getCommissionsByCtrCand(CentreCandidature ctrCand, Boolean isGestAllCommission, List<Integer> listeIdCommission) {
		if (isGestAllCommission!=null && isGestAllCommission == true){
			return commissionRepository.findByCentreCandidatureIdCtrCand(ctrCand.getIdCtrCand());
		}else if (listeIdCommission!=null && listeIdCommission.size()>0){
			return commissionRepository.findByCentreCandidatureIdCtrCandAndIdCommIn(ctrCand.getIdCtrCand(),listeIdCommission);
		}else{
			return new ArrayList<Commission>();
		}		
	}
	
	/**
	 * @return la liste des commission dont le user est membre
	 */
	public List<Commission> getCommissionsGestionnaire() {
		if (userController.isScolCentrale()){
			return commissionRepository.findAll();
		}
		
		List<DroitProfilInd> listeProfil = droitProfilController.searchDroitByLoginAndIsCommissionMember(userController.getCurrentUserLogin());
		return listeProfil.stream().map(e->e.getCommissionMembre().getCommission()).filter(c->c!=null).collect(Collectors.toList());
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau commission.
	 */
	public void editNewCommission(CentreCandidature ctrCand) {
		Commission commission = new Commission(ctrCand,userController.getCurrentUserLogin());
		UI.getCurrent().addWindow(new CtrCandCommissionWindow(commission));
	}
	
	/**
	 * Ouvre une fenêtre d'édition de commission.
	 * @param commission
	 */
	public void editCommission(Commission commission) {
		Assert.notNull(commission);

		/* Verrou */
		if (!lockController.getLockOrNotify(commission, null)) {
			return;
		}
		CtrCandCommissionWindow window = new CtrCandCommissionWindow(commission);
		window.addCloseListener(e->lockController.releaseLock(commission));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un commission
	 * @param commission
	 * @param adresse 
	 */
	public void saveCommission(Commission commission, Adresse adresse) {
		Assert.notNull(commission);
		Assert.notNull(adresse);

		/* Verrou */
		if (commission.getIdComm()!=null && !lockController.getLockOrNotify(commission, null)) {
			return;
		}
		commission.setUserModComm(userController.getCurrentUserLogin());
		commission.setAdresse(adresse);
		//validateBean(commission);
		
		
		commission = commissionRepository.saveAndFlush(commission);
		
		lockController.releaseLock(commission);
	}

	/**
	 * Supprime une commission
	 * @param commission
	 */
	public void deleteCommission(Commission commission) {
		Assert.notNull(commission);

		if (commission.getFormations().size()>0){
			Notification.show(applicationContext.getMessage("commission.error.delete", new Object[]{Formation.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		/* Verrou */
		if (!lockController.getLockOrNotify(commission, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("commission.window.confirmDelete", new Object[]{commission.getCodComm()}, UI.getCurrent().getLocale()), applicationContext.getMessage("commission.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(commission, null)) {
				commission.getCommissionMembres().forEach(gest -> {
					droitProfilController.deleteDroitProfilInd(gest.getDroitProfilInd());
				});
				commission.getCommissionMembres().clear();
				commissionRepository.delete(commission);
				/* Suppression du lock */
				lockController.releaseLock(commission);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(commission);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Verifie l'unicité du code
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodCommUnique(String cod, Integer id) {
		Commission motiv = commissionRepository.findByCodComm(cod);
		if (motiv==null){
			return true;
		}else{
			if (motiv.getIdComm().equals(id)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Ajoute un profil à un membre
	 * @param commission 
	 */
	public void addProfilToMembre(Commission commission) {
		/* Verrou */
		if (!lockController.getLockOrNotify(commission, null)) {
			return;
		}
		DroitProfilMembreCommWindow window = new DroitProfilMembreCommWindow();
		window.addDroitProfilIndCommListener((individu, droit, isPresident)->{
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(commission, null)) {
				if (droitProfilController.getProfilIndByCommissionAndLogin(commission, individu).size()==0){
					Individu ind = droitProfilController.saveIndividu(individu);
					DroitProfilInd dpi = droitProfilController.saveProfilInd(ind, droit);
					commission.getCommissionMembres().add(new CommissionMembre(commission,dpi,isPresident));
					commissionRepository.save(commission);
				}else{
					Notification.show(applicationContext.getMessage("droitprofilind.gest.allready", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
			}
		});
		
		window.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(commission);
		});
		UI.getCurrent().addWindow(window);
	}
	
	/** Modifie le profil d'un membre
	 * @param membre
	 */
	public void updateProfilToMembre(CommissionMembre membre) {
		/* Verrou */
		if (!lockController.getLockOrNotify(membre.getCommission(), null)) {
			return;
		}
		
		Assert.notNull(membre); 
		
		DroitProfilMembreCommWindow window = new DroitProfilMembreCommWindow(membre);
		window.addDroitProfilIndCommListener((individu, droit, isPresident)->{
			membre.getDroitProfilInd().setDroitProfil(droit);
			droitProfilController.saveProfilInd(membre.getDroitProfilInd());
			membre.setTemIsPresident(isPresident);
			membre.getCommission().setDatModComm(LocalDateTime.now());
			commissionRepository.save(membre.getCommission());
		});
		
		window.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(membre.getCommission());
		});
		UI.getCurrent().addWindow(window);
	}
	
	/**
	 * Ajoute un profil à un membre
	 * @param membre 
	 */
	public void deleteProfilToMembre(CommissionMembre membre) {
		/* Verrou */
		if (!lockController.getLockOrNotify(membre.getCommission(), null)) {
			return;
		}
		
		Assert.notNull(membre);

		/* Verrou */
		
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("droitprofilind.window.confirmDelete", new Object[]{membre.getDroitProfilInd().getDroitProfil().getCodProfil(),membre.getDroitProfilInd().getIndividu().getLoginInd()}, UI.getCurrent().getLocale()), applicationContext.getMessage("droitprofilind.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {			
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(membre.getCommission(), null)) {
				membre.getCommission().getCommissionMembres().remove(membre);
				commissionRepository.save(membre.getCommission());
				droitProfilController.deleteDroitProfilInd(membre.getDroitProfilInd());
				/* Suppression du lock */
				lockController.releaseLock(membre.getCommission());
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(membre.getCommission());
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
}
