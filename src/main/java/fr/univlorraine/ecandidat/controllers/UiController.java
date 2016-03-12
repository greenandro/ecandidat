package fr.univlorraine.ecandidat.controllers;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.views.AdminView;
import fr.univlorraine.ecandidat.views.MaintenanceView;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.InputWindow;

/**
 * Gestion des sessions
 * @author Kevin Hergalant
 *
 */

@Component
public class UiController implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8199347806899210906L;
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient UserController userController;

	/** Thread pool  */
	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	/* Envoi de messages aux clients connectés */

	/** UIs connectées */
	private LinkedList<MainUI> uis = new LinkedList<MainUI>();
	
	/**
	 * @return les UIs
	 */
	@SuppressWarnings("unchecked")
	public synchronized LinkedList<MainUI> getUis() {
		return (LinkedList<MainUI>) uis.clone();
	}
	
	/** Doit-on rediriger vers la page de maintenance
	 * @param viewDemande
	 * @return true si on doit rediriger
	 */
	public Boolean redirectToMaintenanceView(String viewDemande){
		if (parametreController.getIsMaintenance() && !viewDemande.equals(MaintenanceView.NAME) && !userController.isAdmin()){
			return true;
		}
		return false;
	}

	/**
	 * Ajoute une UI à la liste des UIs connectées
	 * @param ui l'UI a ajouter
	 */
	public synchronized void registerUI(final MainUI ui) {
		VaadinSession session = ui.getSession();
		SecurityContext securityContext = (SecurityContext) session.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
				
		if (securityContext==null || securityContext.getAuthentication()==null){
			return;
		}
		uis.add(ui);
		notifyUIAdded(ui);

		/* Met à jour les AdminViews lorsqu'une UI change de vue */
		ui.getNavigator().addViewChangeListener(new ViewChangeListener() {
			private static final long serialVersionUID = -23117484566254727L;

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				notifyUIUpdated(ui);
			}
		});
	}

	/**
	 * Enlève une UI de la liste des UIs connectées
	 * @param ui l'UI a enlever
	 */
	public synchronized void unregisterUI(MainUI ui) {
		uis.remove(ui);
		notifyUIRemoved(ui);
	}

	/**
	 * Envoie une notification à tous les clients connectés
	 * @param notification
	 */
	public synchronized void sendNotification(Notification notification) {
		uis.forEach(ui ->
			executorService.execute(() ->
				ui.access(() -> notification.show(ui.getPage()))
			)
		);
	}

	/**
	 * Permet la saisie et l'envoi d'un message à tous les clients connectés
	 */
	public void sendMessage() {
		InputWindow inputWindow = new InputWindow(applicationContext.getMessage("admin.sendMessage.message", null, UI.getCurrent().getLocale()), applicationContext.getMessage("admin.sendMessage.title", null, UI.getCurrent().getLocale()), true, 255);
		inputWindow.addBtnOkListener(text -> {
			if (text instanceof String && !text.isEmpty()) {
				Notification notification = new Notification(applicationContext.getMessage("admin.sendMessage.notificationCaption", new Object[] {text}, UI.getCurrent().getLocale()), null, Type.TRAY_NOTIFICATION, true);
				notification.setDelayMsec(-1);
				notification.setDescription("\n" + applicationContext.getMessage("admin.sendMessage.notificationDescription", null, UI.getCurrent().getLocale()));
				notification.setPosition(Position.TOP_CENTER);
				sendNotification(notification);
			}
		});
		UI.getCurrent().addWindow(inputWindow);
	}

	/* Mise à jour des AdminViews */

	/** AdminViews actives */
	private LinkedList<AdminView> adminViews = new LinkedList<AdminView>();

	/**
	 * Ajoute une AdminView à la liste des AdminViews actives
	 * @param adminView
	 */
	public synchronized void registerAdminView(AdminView adminView) {
		adminViews.add(adminView);
	}

	/**
	 * Enlève une AdminView de la liste des AdminViews actives
	 * @param adminView la vue admin
	 */
	public synchronized void unregisterAdminView(AdminView adminView) {
		adminViews.remove(adminView);
	}

	/**
	 * Informe les AdminViews de l'ajout d'une UI
	 * @param addedUI l'UI ajoute
	 */
	public synchronized void notifyUIAdded(UI addedUI) {
		if (UI.getCurrent().getPushConfiguration().getPushMode().isEnabled()) {
			adminViews.forEach(adminView ->
				executorService.execute(() ->
					adminView.receiveAddedUINotification(addedUI)
				)
			);
		}
	}

	/**
	 * Informe les AdminViews de la modification d'une UI
	 * @param updatedUI l'UI modifie
	 */
	public synchronized void notifyUIUpdated(UI updatedUI) {
		if (UI.getCurrent().getPushConfiguration().getPushMode().isEnabled()) {
			adminViews.forEach(adminView ->
				executorService.execute(() ->
					adminView.receiveUpdatedUINotification(updatedUI)
				)
			);
		}
	}

	/**
	 * Informe les AdminViews de la suppression d'une UI
	 * @param removedUI l'UI supprime
	 */
	public synchronized void notifyUIRemoved(UI removedUI) {		
		if (UI.getCurrent().getPushConfiguration().getPushMode().isEnabled()) {
			adminViews.forEach(adminView ->
				executorService.execute(() ->{
					adminView.receiveRemovedUINotification(removedUI);					
				}
					
				)
			);
		}
	}

	/**
	 * Vérifie si une UI est toujours active
	 * @param ui l'UI a vérifier
	 * @return true si l'UI est active
	 */
	public synchronized boolean isUIStillActive(UI ui) {
		return uis.contains(ui);
	}

	/* Tuer des UIs, sessions et utilisateurs */

	/**
	 * Confirme la fermeture de toutes les sessions associées à un utilisateur
	 * @param user
	 */
	public void confirmKillUser(UserDetails user) {
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("admin.uiList.confirmKillUser", new Object[]{user.getUsername()}, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> killUser(user));
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Ferme toutes les sessions associées à un utilisateur
	 * @param user le user a kill
	 */
	public synchronized void killUser(UserDetails user) {
		for (MainUI mainUI : uis) {
			SecurityContext securityContext = (SecurityContext) mainUI.getSession().getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);			
			if (user.getUsername().equals(securityContext.getAuthentication().getName())) {
				mainUI.close();
			}
		}
	}

	/**
	 * Confirme la fermeture d'une session
	 * @param session la session a kill
	 */
	public void confirmKillSession(VaadinSession session) {
		SecurityContext securityContext = (SecurityContext) session.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		String userName = applicationContext.getMessage("user.notconnected", null, UI.getCurrent().getLocale());
		if (securityContext!=null && securityContext.getAuthentication()!=null){
			userName = securityContext.getAuthentication().getName();
		}
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("admin.uiList.confirmKillSession", new Object[]{session.getSession().getId(), userName}, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> killSession(session));
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Ferme une session
	 * @param session la session a kill
	 */
	public void killSession(VaadinSession session) {
		session.close();
	}

	/**
	 * Confirme la fermeture d'une UI
	 * @param ui l'UI a kill
	 */
	public void confirmKillUI(UI ui) {
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("admin.uiList.confirmKillUI", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> killUI(ui));
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Ferme une UI
	 * @param ui lUI a fermer
	 */
	public void killUI(UI ui) {
		ui.close();
	}

	/**
	 * Supprime un verrou
	 * @param lock le verrou a supprimer
	 */
	public void confirmRemoveLock(Object lock) {
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("admin.uiList.confirmRemoveLock", new Object[]{lock}, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> lockController.removeLock(lock));
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Connecte un candidat
	 * Registre l'ui de connexion, ferme les autres appartenant à la session
	 * @param ui
	 */
	public void registerUiCandidat(MainUI ui) {
		registerUI(ui);
	}

	/**
	 * @return la fenetre de deconnexion
	 */
	/*private Window getDeconnectWindow(){
		Window wDeconnect = new Window();
		wDeconnect.setWidth("190");
		wDeconnect.setHeight("40");
		wDeconnect.setResizable(false);
		wDeconnect.setClosable(false);
		wDeconnect.setCaption(applicationContext.getMessage("vaadin.reconnectDialog.textGaveUp", null, UI.getCurrent().getLocale()));
		wDeconnect.setModal(true);
		return wDeconnect;
	}*/
	
	/**Deonnecte un candidat
	 * @param ui
	 */
	public void unregisterUiCandidat(MainUI ui) {
		unregisterUI(ui);
		/*uis.forEach(e->{
			if (ui.getSession().getSession().equals(e.getSession().getSession())){								
				e.access(() -> e.addWindow(getDeconnectWindow()));
				e.close();
			}
		});*/
	}
	
	/** Navigue à une vue
	 * @param view
	 */
	public void navigateTo(String view){
		MainUI ui = (MainUI)UI.getCurrent();
		if (ui!=null){
			ui.navigateToView(view);
		}
	}
}
