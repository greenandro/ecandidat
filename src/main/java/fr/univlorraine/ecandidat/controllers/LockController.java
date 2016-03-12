package fr.univlorraine.ecandidat.controllers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

/**
 * Gestion des verrous
 * @author Kevin Hergalant
 */
@Component
public class LockController {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UiController uiController;

	/** Liste des verrous */
	private Map<Object, UI> locks = new ConcurrentHashMap<>();

	/**
	 * @param ui
	 * @return liste des verrous associés à l'ui
	 */
	public List<Object> getUILocks(UI ui) {		
		return locks.entrySet().stream()
			.filter(e -> e.getValue() == ui)
			.map(Entry::getKey)
			.collect(Collectors.toList());
	}

	/**
	 * Verrouille une ressource pour l'UI courante
	 * @param obj la ressource à verrouiller
	 * @return true si la ressource est bien verrouillée pour l'UI courante, false sinon
	 */
	public boolean getLock(Object obj) {
		Assert.notNull(obj);

		UI lockUI = locks.get(obj);
		if (lockUI instanceof UI && lockUI != UI.getCurrent() && uiController.isUIStillActive(lockUI)) {
			return false;
		}

		locks.put(obj, UI.getCurrent());
		uiController.notifyUIUpdated(UI.getCurrent());
		return true;
	}
	
	/** Vérifie qu'une ressource est lockée
	 * @return true si la ressource verrouillée pour une autre UI, false sinon
	 */
	public boolean checkLock(Object obj){
		Assert.notNull(obj);

		UI lockUI = locks.get(obj);
		
		if (lockUI instanceof UI && lockUI != UI.getCurrent() && uiController.isUIStillActive(lockUI)) {
			return true;
		}
		return false;
	}

	/**
	 * Verrouille une ressource pour l'UI courante
	 * @param obj la ressource à verrouiller
	 * @param msgIfAlreadyLocked message affiché si la ressource est déjà verrouillée pour une autre UI. Si cette propriété vaut null, un message par défaut est affiché.
	 * @return true si la ressource est bien verrouillée pour l'UI courante, false sinon
	 */
	public boolean getLockOrNotify(Object obj, String msgIfAlreadyLocked) {
		boolean ret = getLock(obj);
		if (!ret) {
			if (msgIfAlreadyLocked == null || msgIfAlreadyLocked.isEmpty()) {
				msgIfAlreadyLocked = applicationContext.getMessage("lock.alreadyLocked", new Object[] {obj.getClass().getSimpleName(), getUserNameFromLock(obj)}, UI.getCurrent().getLocale());
			}
			Notification.show(msgIfAlreadyLocked, Notification.Type.WARNING_MESSAGE);
		}
		return ret;
	}

	/**
	 * Supprime un verrou
	 * @param obj
	 */
	public void removeLock(Object obj) {
		UI ui = locks.get(obj);
		locks.remove(obj);
		uiController.notifyUIUpdated(ui);
	}

	/**
	 * Rend un verrou, après avoir vérifié qu'il appartient à l'UI courante
	 * @param obj
	 */
	public void releaseLock(Object obj) {
		if (locks.get(obj) == UI.getCurrent()) {
			removeLock(obj);
		}
	}

	/**
	 * Retourne le nom de l'utilisateur pour le lock passé en paramètre
	 * @param obj
	 * @return userName
	 */
	public String getUserNameFromLock(Object obj){
		UI lockUi = locks.get(obj);
		if (lockUi != null) {
			SecurityContext securityContext = (SecurityContext) lockUi.getSession().getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
			return securityContext.getAuthentication().getName();
		}
		return null;
	}

}
