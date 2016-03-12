package fr.univlorraine.ecandidat.controllers;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.LockCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.LockCandidatPK;
import fr.univlorraine.ecandidat.repositories.LockCandidatRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Controller gérant les appels Ldap
 * @author Kevin Hergalant
 *
 */
@Component
public class LockCandidatController {
	
	/*applicationContext pour les messages*/
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockCandidatRepository lockCandidatRepository;
	@Resource
	private transient LoadBalancingController loadBalancingController;

	
	/**
	 * @return l'id de l'ui de l'utilisateur
	 */
	protected Integer getUIId() {
        UI uI = UI.getCurrent();
        if (uI == null) {
            return null;
        } else {
            return UI.getCurrent().getUIId();
        }
    }
	
	/**
	 * Supprime tout les locks
	 */
	public void cleanAllLockCandidatForInstance(){
		lockCandidatRepository.deleteInBatch(lockCandidatRepository.findByInstanceIdLock(loadBalancingController.getIdInstance()));
	}
	
	public Boolean getLock(CompteMinima cptMin, String ressource){
		Integer uiId = getUIId();
		if (uiId==null){
			return false;
		}
		
		LockCandidatPK lockPk = new LockCandidatPK(cptMin.getNumDossierOpiCptMin(), ressource);
		LockCandidat lock = lockCandidatRepository.findOne(lockPk);
		
		if (lock!=null && (!lock.getUiIdLock().equals(uiId) || !lock.getInstanceIdLock().equals(loadBalancingController.getIdInstance()))) {			
			return false;
		}
		if (lock!=null && lock.getUiIdLock().equals(uiId) && lock.getInstanceIdLock().equals(loadBalancingController.getIdInstance())){
			return true;
		}
		try{
			lockCandidatRepository.saveAndFlush(new LockCandidat(lockPk, loadBalancingController.getIdInstance(), uiId));
			return true;
		}catch (Exception e){
			return false;
		}		
	}
	
	/** Vérifie qu'une ressource est lockée
	 * @return true si la ressource verrouillée pour une autre UI, false sinon
	 */
	public boolean checkLock(CompteMinima cptMin, String ressource){
		Integer uiId = getUIId();
		if (uiId==null){
			return true;
		}
		LockCandidat lock = lockCandidatRepository.findOne(new LockCandidatPK(cptMin.getNumDossierOpiCptMin(), ressource));
		if (lock!=null && !lock.getUiIdLock().equals(uiId)) {
			return true;
		}
		return false;
	}
	
	/**Rend un verrou, après avoir vérifié qu'il appartient à l'UI courante
	 * @param cptMin
	 * @param ressource
	 */
	public void releaseLock(CompteMinima cptMin, String ressource) {
		Integer uiId = getUIId();
		if (uiId==null || cptMin==null || ressource==null){
			return;
		}
		LockCandidat lock = lockCandidatRepository.findOne(new LockCandidatPK(cptMin.getNumDossierOpiCptMin(), ressource));
		if (lock!=null && lock.getUiIdLock().equals(uiId) && lock.getInstanceIdLock().equals(loadBalancingController.getIdInstance())) {
			removeLock(lock);
		}
	}
	
	/** Rend un verrou de candidature
	 * @param candidature
	 */
	public void releaseLockCandidature(Candidature candidature){
		releaseLock(candidature.getCandidat().getCompteMinima(), ConstanteUtils.LOCK_CAND+"_"+candidature.getIdCand());
	}
	
	/**
	 * Supprime tout les locks de l'UI
	 */
	public void removeAllLockUI(){
		Integer uiId = getUIId();
		if (uiId==null){
			return;
		}
		lockCandidatRepository.deleteInBatch(lockCandidatRepository.findByUiIdLockAndInstanceIdLock(uiId,loadBalancingController.getIdInstance()));
	}
	
	/**Supprime un verrou
	 * @param cptMin
	 * @param ressource
	 */
	/*public void removeLock(CompteMinima cptMin, String ressource) {
		LockCandidat lock = lockCandidatRepository.getOne(new LockCandidatPK(cptMin.getNumDossierOpiCptMin(), ressource));
		if (lock!=null) {
			removeLock(lock);
		}
	}*/
	
	/**Supprime un verrou
	 * @param lock
	 */
	private void removeLock(LockCandidat lock){
		lockCandidatRepository.delete(lock);
	}
	
	public boolean getLockOrNotifyCandidature(Candidature candidature){
		if (candidature==null){
			return false;
		}
		return getLockOrNotify(candidature.getCandidat().getCompteMinima(), ConstanteUtils.LOCK_CAND+"_"+candidature.getIdCand(), applicationContext.getMessage("lock.message.candidature", null, UI.getCurrent().getLocale()));
	}
	
	/** Verrouille une ressource pour l'UI courante
	 * @param cptMin
	 * @param ressource
	 * @param msgIfAlreadyLocked message affiché si la ressource est déjà verrouillée pour une autre UI. Si cette propriété vaut null, un message par défaut est affiché.
	 * @return true si la ressource est bien verrouillée pour l'UI courante, false sinon
	 */
	public boolean getLockOrNotify(CompteMinima cptMin, String ressource, String msgIfAlreadyLocked) {
		boolean ret = getLock(cptMin, ressource);
		if (!ret) {
			if (msgIfAlreadyLocked == null || msgIfAlreadyLocked.isEmpty()) {
				msgIfAlreadyLocked = applicationContext.getMessage("lock.message.candidat", null, UI.getCurrent().getLocale());
			}
			Notification.show(msgIfAlreadyLocked, Notification.Type.WARNING_MESSAGE);
		}
		return ret;
	}
}
