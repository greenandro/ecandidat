package fr.univlorraine.ecandidat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.UIDetachedException;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.LoadBalancingController;
import fr.univlorraine.ecandidat.controllers.LockCandidatController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.UiController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCommission;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.menu.AccordionItemMenu;
import fr.univlorraine.ecandidat.vaadin.menu.AccordionMenu;
import fr.univlorraine.ecandidat.vaadin.menu.Menu;
import fr.univlorraine.ecandidat.vaadin.menu.SubMenu;
import fr.univlorraine.ecandidat.vaadin.menu.SubMenuBar;
import fr.univlorraine.ecandidat.views.AccueilView;
import fr.univlorraine.ecandidat.views.AdminBatchView;
import fr.univlorraine.ecandidat.views.AdminDroitProfilIndView;
import fr.univlorraine.ecandidat.views.AdminDroitProfilView;
import fr.univlorraine.ecandidat.views.AdminLangueView;
import fr.univlorraine.ecandidat.views.AdminParametreView;
import fr.univlorraine.ecandidat.views.AdminVersionView;
import fr.univlorraine.ecandidat.views.AdminView;
import fr.univlorraine.ecandidat.views.AssistanceView;
import fr.univlorraine.ecandidat.views.CandidatAdminView;
import fr.univlorraine.ecandidat.views.CandidatAdresseView;
import fr.univlorraine.ecandidat.views.CandidatBacView;
import fr.univlorraine.ecandidat.views.CandidatCandidaturesView;
import fr.univlorraine.ecandidat.views.CandidatCompteMinimaView;
import fr.univlorraine.ecandidat.views.CandidatCreerCompteView;
import fr.univlorraine.ecandidat.views.CandidatCursusExterneView;
import fr.univlorraine.ecandidat.views.CandidatCursusInterneView;
import fr.univlorraine.ecandidat.views.CandidatFormationProView;
import fr.univlorraine.ecandidat.views.CandidatInfoPersoView;
import fr.univlorraine.ecandidat.views.CandidatStageView;
import fr.univlorraine.ecandidat.views.CommissionCandidatureView;
import fr.univlorraine.ecandidat.views.CtrCandCandidatureArchivedView;
import fr.univlorraine.ecandidat.views.CtrCandCandidatureCanceledView;
import fr.univlorraine.ecandidat.views.CtrCandCandidatureView;
import fr.univlorraine.ecandidat.views.CtrCandCommissionView;
import fr.univlorraine.ecandidat.views.CtrCandFormationView;
import fr.univlorraine.ecandidat.views.CtrCandFormulaireCommunView;
import fr.univlorraine.ecandidat.views.CtrCandFormulaireView;
import fr.univlorraine.ecandidat.views.CtrCandParametreView;
import fr.univlorraine.ecandidat.views.CtrCandPieceJustifCommunView;
import fr.univlorraine.ecandidat.views.CtrCandPieceJustifView;
import fr.univlorraine.ecandidat.views.ErreurView;
import fr.univlorraine.ecandidat.views.MaintenanceView;
import fr.univlorraine.ecandidat.views.OffreFormationView;
import fr.univlorraine.ecandidat.views.ScolCampagneView;
import fr.univlorraine.ecandidat.views.ScolCentreCandidatureView;
import fr.univlorraine.ecandidat.views.ScolFaqView;
import fr.univlorraine.ecandidat.views.ScolFormulaireView;
import fr.univlorraine.ecandidat.views.ScolMailView;
import fr.univlorraine.ecandidat.views.ScolMotivAvisView;
import fr.univlorraine.ecandidat.views.ScolPieceJustifView;
import fr.univlorraine.ecandidat.views.ScolTypeDecisionView;
import fr.univlorraine.ecandidat.views.ScolTypeStatutPieceView;
import fr.univlorraine.ecandidat.views.ScolTypeStatutView;
import fr.univlorraine.ecandidat.views.ScolTypeTraitementView;
import fr.univlorraine.ecandidat.views.windows.SearchCandidatWindow;
import fr.univlorraine.ecandidat.views.windows.SearchCommissionWindow;
import fr.univlorraine.ecandidat.views.windows.SearchCtrCandWindow;
import fr.univlorraine.tools.vaadin.IAnalyticsTracker;
import fr.univlorraine.tools.vaadin.LogAnalyticsTracker;
import fr.univlorraine.tools.vaadin.PiwikAnalyticsTracker;
import lombok.Getter;

/**
 * UI principale
 * @author Adrien Colson
 */
@Theme("valo-ul")
@SpringUI(path="/*")
@Push
public class MainUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7960169450062541509L;
	
	/**
	* Nombre maximum de tentatives de reconnexion lors d'une déconnexion.
	*/
	private static final int TENTATIVES_RECO = 3;
	

	/* Redirige java.util.logging vers SLF4j */
	static {
		SLF4JBridgeHandler.install();
	}

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient UiController uiController;
	@Resource
	private transient LockCandidatController lockCandidatController;

	/* Propriétés */
	@Value("${app.name}")
	private String appName;
	@Value("${app.version}")
	private String appVersion;
	@Value("${demoMode}")
	private String demoMode;
	
	@Value("${piwikAnalytics.trackerUrl:}")
	private transient String piwikAnalyticsTrackerUrl;
	@Value("${piwikAnalytics.siteId:}")
	private transient String piwikAnalyticsSiteId;
	
	@Value("${enablePush}")
	private transient String enablePush;
	@Value("${enableWebSocketPush:}")
	private transient String enableWebSocketPush;
	
	
	/** Logger SLF4J */
	private Logger logger = LoggerFactory.getLogger(MainUI.class);

	/* Composants */
	private final CssLayout menu = new CssLayout();
	private final CssLayout menuLayout = new CssLayout(menu);
	private final CssLayout menuButtonLayout = new CssLayout();
	private final CssLayout contentLayout = new CssLayout();
	private final CssLayout layoutWithSheet = new CssLayout();
	private final HorizontalLayout layout = new HorizontalLayout(menuLayout, layoutWithSheet);
	private final SubMenuBar subBarMenu = new SubMenuBar();
	
	private Button lastButtonView;
	private AccordionMenu accordionMenu;
	private AccordionItemMenu itemMenuCtrCand;
	private AccordionItemMenu itemMenuCommission;
	private AccordionItemMenu itemMenuGestCandidat;
	private Button changeCandBtn;
	private Button createCandBtn;
	
	/** The view provider. */
	@Resource
	private SpringViewProvider viewProvider;
	@Getter
	private IAnalyticsTracker analyticsTracker;
	
	/** Gestionnaire de vues */
	private final Navigator navigator = new Navigator(this, contentLayout);
	
	/**Nom de la dernière vue visitée*/
	private String currentViewName = null;
	
	/** Noms des vues et boutons du menu associés */
	private Map<String, Menu> viewButtons = new HashMap<>();
	
	/** Noms des vues et numéro accordeon associé */
	private Map<String, String> viewAccordion = new HashMap<>();
	
	/** Noms des vues et numéro accordeon associé */
	private Map<String, String> viewAccordionCtrCand = new HashMap<>();
	
	/** Noms des vues et numéro accordeon associé */
	private Map<String, String> viewAccordionCommission = new HashMap<>();
	
	/** Noms des vues et numéro accordeon associé */
	private Map<String, String> viewAccordionGestCandidat = new HashMap<>();

	/**Les infos en cours d'edition*/
	private Integer idCtrCandEnCours = null;
	private Integer idCommissionEnCours = null;
	private String noDossierCandidatEnCours = null;

	/*TODO*/
	private String vueToDisplay = AccueilView.NAME;
	private static final String SELECTED_ITEM = "selected";

	/**
	 * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init(VaadinRequest vaadinRequest) {
		
		/*Test du mode de push*/
		if (isPushEnable() && !isWebSocketPushEnable()){
			getPushConfiguration().setTransport(Transport.LONG_POLLING);
		}
		//getPushConfiguration().setPushMode(PushMode.DISABLED);
		
		VaadinSession.getCurrent().setErrorHandler(e -> {
			Throwable cause = e.getThrowable();
			while (cause instanceof Throwable) {
				/* Gère les accès non autorisés */
				if (cause instanceof AccessDeniedException) {					
					Notification.show(cause.getMessage(), Type.ERROR_MESSAGE);
					navigator.navigateTo(ErreurView.NAME);
					return;
				}
				if (cause instanceof UIDetachedException) {
					return;
				}
				/* Gère les UIs détachées pour les utilisateurs déconnectés */
				if (cause instanceof AuthenticationCredentialsNotFoundException) {
					return;
				}
				cause = cause.getCause();
			}
			/* Log les autres erreurs */
			logger.error("Erreur non gérée", e.getThrowable());
		});
		
		//getPushConfiguration().setTransport(Transport.LONG_POLLING);
		
		configReconnectDialog();
		
		/* Affiche le nom de l'application dans l'onglet du navigateur */
		getPage().setTitle(appName);

		initLayout();
		
		initNavigator();
		
		initAnalyticsTracker();
		
		initLanguage();

		buildTitle();

		buildMenu();
		
		/* Enregistre l'UI pour la réception de notifications */
		uiController.registerUI(this);
	}
	

	/**
	 * Initialise la langue
	 */
	private void initLanguage() {
		i18nController.initLanguageUI(false);
	}


	/**
	 * Initialise le layout principal
	 */
	private void initLayout() {
		layout.setSizeFull();
		setContent(layout);

		menuLayout.setPrimaryStyleName(ValoTheme.MENU_ROOT);

		layoutWithSheet.setPrimaryStyleName(StyleConstants.VALO_CONTENT);
		layoutWithSheet.addStyleName(StyleConstants.SCROLLABLE);		
		layoutWithSheet.setSizeFull();
		
		VerticalLayout vlAll = new VerticalLayout();
		vlAll.addStyleName(StyleConstants.SCROLLABLE);
		vlAll.setSizeFull();
		
		subBarMenu.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		subBarMenu.setVisible(false);
		vlAll.addComponent(subBarMenu);
		
		contentLayout.addStyleName(StyleConstants.SCROLLABLE);
		contentLayout.setSizeFull();
		vlAll.addComponent(contentLayout);
		vlAll.setExpandRatio(contentLayout, 1);
		
		layoutWithSheet.addComponent(vlAll);
		
		menuButtonLayout.addStyleName(StyleConstants.VALO_MY_MENU_MAX_WIDTH);	
		layout.setExpandRatio(layoutWithSheet, 1);

		Responsive.makeResponsive(this);
		addStyleName(ValoTheme.UI_WITH_MENU);
	}
	
	/**
	 * Va à la vue
	 */
	public void navigateToView(String name){
		navigator.navigateTo(name);
	}
	
	/**
	 * Retourne à l'accueil
	 */
	public void navigateToAccueilView() {
		navigator.navigateTo(AccueilView.NAME);
	}

	/**
	 * Construit le titre de l'application
	 */
	private void buildTitle() {		
		Button itemBtn = new Button(appName, new ThemeResource("logo.png"));
		String demo = "";
		if (demoMode!=null && Boolean.valueOf(demoMode)){
			demo = " - Version Demo";
		}
		itemBtn.setDescription("v" + appVersion+demo);
		itemBtn.setPrimaryStyleName(ValoTheme.MENU_TITLE);
		itemBtn.addStyleName(ValoTheme.MENU_ITEM);
		itemBtn.addClickListener(e->getNavigator().navigateTo(AccueilView.NAME));
		menu.addComponent(itemBtn);
		
		/*final HorizontalLayout titleLayout = new HorizontalLayout();
		titleLayout.setWidth(100, Unit.PERCENTAGE);
		titleLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		titleLayout.addStyleName(ValoTheme.MENU_TITLE);
		titleLayout.addStyleName(StyleConstants.CLICKABLE);
		menu.addComponent(titleLayout);

		Button itemBtn = new Button(appName, new ThemeResource("images/logo.png"));
		itemBtn.setDescription("v" + appVersion);
		itemBtn.setPrimaryStyleName(StyleConstants.TITLE_BTN);
		itemBtn.addStyleName(ValoTheme.MENU_ITEM);
		itemBtn.addClickListener(e->getNavigator().navigateTo(AccueilView.NAME));
		titleLayout.addComponent(itemBtn);*/

		/*Image logo = new Image(null, new ThemeResource("images/logo.png"));
		logo.addStyleName(StyleConstants.LOGO);
		titleInnerLayout.addComponent(logo);

		final Label appNameLabel = new Label(appName);
		appNameLabel.setDescription("v" + appVersion);
		appNameLabel.addStyleName(ValoTheme.LABEL_H3);
		appNameLabel.addStyleName(ValoTheme.LABEL_BOLD);
		titleInnerLayout.addComponent(appNameLabel);*/

		/*final MenuBar settings = new MenuBar();
		settings.addStyleName(StyleConstants.VALO_USER_MENU);
		settings.addItem(userController.getCurrentUserName(), new ThemeResource("images/user.png"), si -> getNavigator().navigateTo(AccueilView.NAME));
		menu.addComponent(settings);*/
	}

	/**
	 * Construit le menu
	 */
	private void buildMenu() {
		menu.addStyleName(ValoTheme.MENU_PART);

		final Button showMenu = new Button(applicationContext.getMessage("mainUi.menu", null, getLocale()), FontAwesome.LIST);
		showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
		showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
		showMenu.addStyleName(StyleConstants.VALO_MY_MENU_TOGGLE);
		showMenu.addStyleName(StyleConstants.VALO_MENU_TOGGLE);
		showMenu.addClickListener(e -> {
			if (menu.getStyleName().contains(StyleConstants.VALO_MENU_VISIBLE)) {
				menu.removeStyleName(StyleConstants.VALO_MENU_VISIBLE);
			} else {
				menu.addStyleName(StyleConstants.VALO_MENU_VISIBLE);
			}
		});
		menu.addComponent(showMenu);

		menuButtonLayout.setPrimaryStyleName(StyleConstants.VALO_MENUITEMS);
		menu.addComponent(menuButtonLayout);
		
		constructMainMenu();

		/* Boutons du menu */
		/*createMenuButton(applicationContext.getMessage(AccueilView.NAME + ".title", null, getLocale()), FontAwesome.HOME, AccueilView.NAME);
		createMenuButton(applicationContext.getMessage(AssistanceView.NAME + ".title", null, getLocale()), FontAwesome.LIST, AssistanceView.NAME);*/
	}
	
	/**
	 * Reconstruit le menu apres une connexion
	 */
	public void reconstructMainMenu(){
		constructMainMenu();
		navigateToAccueilView();
	}
	
	/**
	 * Deconnect un candidat et le renvoie a l'accueil
	 */
	/*public void disconnect(){
		userController.disconnectUser();
		constructMainMenu();
	}*/
	
	/**
	 * Construit tout les boutons
	 */
	public void constructMainMenu(){		
		menuButtonLayout.removeAllComponents();		
		
		/* Titre: Username */
		Label usernameLabel = new Label(userController.getCurrentUserName());
		usernameLabel.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
		usernameLabel.setSizeUndefined();
		menuButtonLayout.addComponent(usernameLabel);		

		/* Accueil */
		if (userController.isAnonymous() && !loadBalancingController.isLoadBalancingGestionnaireMode()){
			LinkedList<SubMenu> subMenuAccueil = new LinkedList<SubMenu>();
			subMenuAccueil.add(new SubMenu(AccueilView.NAME, FontAwesome.POWER_OFF));			
			subMenuAccueil.add(new SubMenu(CandidatCreerCompteView.NAME, FontAwesome.MAGIC));
			addItemMenu(applicationContext.getMessage("main.menu.accueil.title", null, getLocale()), null, FontAwesome.HOME ,subMenuAccueil, null);
		}else{
			addItemMenu(applicationContext.getMessage("main.menu.accueil.title", null, getLocale()), AccueilView.NAME, FontAwesome.HOME,null,null);
		}
		

		/* Assistance */
		addItemMenu(applicationContext.getMessage(AssistanceView.NAME + ".title", null, getLocale()), AssistanceView.NAME, FontAwesome.SUPPORT,null,null);
		
		/* Accueil */
		addItemMenu(applicationContext.getMessage(OffreFormationView.NAME + ".title", null, getLocale()), OffreFormationView.NAME, FontAwesome.BOOKMARK,null,null);
		
		/*Bouton de connexion*/
		if (userController.isAnonymous()){
			Button itemBtn = new Button(applicationContext.getMessage("btnConnect", null, getLocale()), FontAwesome.POWER_OFF);
			itemBtn.addClickListener(e -> userController.connectCAS());
			itemBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
			menuButtonLayout.addComponent(itemBtn);
		}else{
			Button itemBtn = new Button(applicationContext.getMessage("btnDisconnect", null, getCurrent().getLocale()), FontAwesome.POWER_OFF);
			itemBtn.addClickListener(e -> {
				userController.deconnect();
			});
			itemBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
			menuButtonLayout.addComponent(itemBtn);
		}
		
		/* Bouton permettant de rétablir l'utilisateur ayant changé de rôle */
		if (userController.isUserSwitched()) {
			Button btnSwitchUserBack = new Button(applicationContext.getMessage("admin.switchUser.btnSwitchUserBack", null, getLocale()), FontAwesome.UNDO);
			btnSwitchUserBack.setPrimaryStyleName(ValoTheme.MENU_ITEM);
			btnSwitchUserBack.addClickListener(e -> userController.switchBackToPreviousUser());
			menuButtonLayout.addComponent(btnSwitchUserBack);
		}

		/* Bouton vers la vue Admin */
		/*if (userController.canCurrentUserAccessView(AdminView.class)) {
			LinkedList<SubMenu> subMenuAdmin = new LinkedList<SubMenu>();
			subMenuAdmin.add(new SubMenu(AdminDroitProfilView.NAME, FontAwesome.USER));
			subMenuAdmin.add(new SubMenu(AdminBatchView.NAME, FontAwesome.ROCKET));
			
			subMenuAdmin.add(new SubMenu(AdminView.NAME, FontAwesome.WRENCH));
			subMenuAdmin.add(new SubMenu(AdminLangueView.NAME, FontAwesome.FLAG));			
			addItemMenu(applicationContext.getMessage("admin.mainmenu", null, getLocale()), null, FontAwesome.HOME, subMenuAdmin);
		}*/
		
		accordionMenu = new AccordionMenu();
		menuButtonLayout.addComponent(accordionMenu);
		
		Boolean isCandidatMode = loadBalancingController.isLoadBalancingCandidatMode();
		
		if (!isCandidatMode){
			/* Bouton vers la vue Admin */
			if (userController.canCurrentUserAccessView(AdminDroitProfilView.class)) {
				AccordionItemMenu itemMenuAdmin = new AccordionItemMenu(applicationContext.getMessage("admin.mainmenu", null, getLocale()),accordionMenu);
				accordionMenu.addItemMenu(itemMenuAdmin, ConstanteUtils.UI_MENU_ADMIN);			
				
				
				/*Accès uniquement aux admins*/
				if (userController.canCurrentUserAccessView(AdminView.class)) {
					LinkedList<SubMenu> subMenuParametrage = new LinkedList<SubMenu>();				
					subMenuParametrage.add(new SubMenu(AdminParametreView.NAME, FontAwesome.COGS));
					subMenuParametrage.add(new SubMenu(AdminLangueView.NAME, FontAwesome.FLAG));
					subMenuParametrage.add(new SubMenu(AdminVersionView.NAME, FontAwesome.COG));
					addItemMenu(applicationContext.getMessage(AdminParametreView.NAME + ".title", null, getLocale()), null, FontAwesome.COGS ,subMenuParametrage,itemMenuAdmin);		
					addItemMenu(applicationContext.getMessage(AdminBatchView.NAME + ".title", null, getLocale()), AdminBatchView.NAME, FontAwesome.ROCKET,null,itemMenuAdmin);	
					addItemMenu(applicationContext.getMessage(AdminView.NAME + ".title", null, getLocale()), AdminView.NAME, FontAwesome.WRENCH,null,itemMenuAdmin);
					addItemMenu(applicationContext.getMessage(ScolCampagneView.NAME + ".title", null, getLocale()), ScolCampagneView.NAME, FontAwesome.STAR,null,itemMenuAdmin);
				}
				
				LinkedList<SubMenu> subMenuDroits = new LinkedList<SubMenu>();
				subMenuDroits.add(new SubMenu(AdminDroitProfilView.NAME, FontAwesome.USER));
				subMenuDroits.add(new SubMenu(AdminDroitProfilIndView.NAME, FontAwesome.USERS));
				
				addItemMenu(applicationContext.getMessage("AdminDroitProfilMenu.title", null, getLocale()), null, FontAwesome.USER ,subMenuDroits, itemMenuAdmin);
			}

			/* Bouton vers la vue Scol centrale */
			if (userController.canCurrentUserAccessView(ScolMailView.class)) {
				AccordionItemMenu itemMenuScol = new AccordionItemMenu(applicationContext.getMessage("scolcentrale.mainmenu", null, getLocale()),accordionMenu);		
				accordionMenu.addItemMenu(itemMenuScol, ConstanteUtils.UI_MENU_SCOL);
							
				addItemMenu(applicationContext.getMessage(ScolMailView.NAME + ".title", null, getLocale()), ScolMailView.NAME, FontAwesome.ENVELOPE,null,itemMenuScol);
				addItemMenu(applicationContext.getMessage(ScolTypeDecisionView.NAME + ".title", null, getLocale()), ScolTypeDecisionView.NAME, FontAwesome.GAVEL,null,itemMenuScol);
				addItemMenu(applicationContext.getMessage(ScolMotivAvisView.NAME + ".title", null, getLocale()), ScolMotivAvisView.NAME, FontAwesome.REPLY,null,itemMenuScol);
				addItemMenu(applicationContext.getMessage(ScolCentreCandidatureView.NAME + ".title", null, getLocale()), ScolCentreCandidatureView.NAME, FontAwesome.HOME,null,itemMenuScol);
				addItemMenu(applicationContext.getMessage(ScolPieceJustifView.NAME + ".title", null, getLocale()), ScolPieceJustifView.NAME, FontAwesome.FILE_TEXT,null,itemMenuScol);
				addItemMenu(applicationContext.getMessage(ScolFormulaireView.NAME + ".title", null, getLocale()), ScolFormulaireView.NAME, FontAwesome.FILE_ZIP_O,null,itemMenuScol);
				
				LinkedList<SubMenu> subMenuTypDec = new LinkedList<SubMenu>();
				subMenuTypDec.add(new SubMenu(ScolTypeTraitementView.NAME, FontAwesome.CARET_SQUARE_O_UP));
				subMenuTypDec.add(new SubMenu(ScolTypeStatutView.NAME, FontAwesome.CARET_SQUARE_O_LEFT));
				subMenuTypDec.add(new SubMenu(ScolTypeStatutPieceView.NAME, FontAwesome.CARET_SQUARE_O_RIGHT));
				subMenuTypDec.add(new SubMenu(ScolFaqView.NAME, FontAwesome.QUESTION_CIRCLE));
				
				addItemMenu(applicationContext.getMessage("ScolNomenclature.title", null, getLocale()), null, FontAwesome.CARET_SQUARE_O_UP ,subMenuTypDec, itemMenuScol);
			}
			
			/* Bouton vers la vue Centre de candidature */
			if (userController.canCurrentUserAccessView(CtrCandParametreView.class)) {
				itemMenuCtrCand = new AccordionItemMenu(applicationContext.getMessage("ctrcand.mainmenu", null, getLocale()),accordionMenu);		
				accordionMenu.addItemMenu(itemMenuCtrCand, ConstanteUtils.UI_MENU_CTR);
				buildMenuCtrCand();
			}
			
			/* Bouton vers la vue Commission */
			if (userController.canCurrentUserAccessView(CommissionCandidatureView.class)) {
				itemMenuCommission = new AccordionItemMenu(applicationContext.getMessage("commission.mainmenu", null, getLocale()),accordionMenu);		
				accordionMenu.addItemMenu(itemMenuCommission, ConstanteUtils.UI_MENU_COMM);
				buildMenuCommission();
			}
			
			/*Bouton vers la vue de gestion du candidat*/
			if (userController.canCurrentUserAccessView(CtrCandParametreView.class)) {
				itemMenuGestCandidat = new AccordionItemMenu(applicationContext.getMessage("gestcand.mainmenu", null, getLocale()),accordionMenu);		
				accordionMenu.addItemMenu(itemMenuGestCandidat, ConstanteUtils.UI_MENU_GEST_CAND);
				
				createCandBtn = new Button("Créer compte candidat",FontAwesome.PENCIL);
				createCandBtn.setDescription(applicationContext.getMessage("btn.create.candidat", null, getLocale()));
				//createCandBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
				createCandBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
				createCandBtn.addClickListener(e->{
					candidatController.createCompteMinima(true);
				});
				//itemMenuGestCandidat.addButton(createCandBtn, Alignment.TOP_CENTER);
				itemMenuGestCandidat.addButton(createCandBtn);
				
				/*Changement de candidat*/
				changeCandBtn = new Button(applicationContext.getMessage("btn.find.candidat", null, getLocale()));
				changeCandBtn.setDescription(applicationContext.getMessage("btn.find.candidat", null, getLocale()));
				changeCandBtn.setIcon(FontAwesome.SEARCH);
				changeCandBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
				changeCandBtn.addClickListener(e->{
					SearchCandidatWindow win = new SearchCandidatWindow();
					win.addCompteMinimaListener(compteMinima->{
						if (compteMinima!=null){
							noDossierCandidatEnCours = compteMinima.getNumDossierOpiCptMin();
							userController.setNoDossierNomCandidat(compteMinima);
							buildMenuGestCand();
						}					
					});
					
					getCurrent().addWindow(win);
				});
				itemMenuGestCandidat.addButton(changeCandBtn);
				buildMenuGestCand();
			}
		}else{
			/*Accès uniquement aux admins*/
			if (userController.canCurrentUserAccessView(AdminView.class)) {
				AccordionItemMenu itemMenuAdmin = new AccordionItemMenu(applicationContext.getMessage("admin.mainmenu", null, getLocale()),accordionMenu);
				accordionMenu.addItemMenu(itemMenuAdmin, ConstanteUtils.UI_MENU_ADMIN);
				
				addItemMenu(applicationContext.getMessage(AdminVersionView.NAME + ".title", null, getLocale()), AdminVersionView.NAME, FontAwesome.COG ,null,itemMenuAdmin);		
				addItemMenu(applicationContext.getMessage(AdminView.NAME + ".title", null, getLocale()), AdminView.NAME, FontAwesome.WRENCH,null,itemMenuAdmin);
			}
		}
		
		
		
		accordionMenu.selectFirst();
		
		/* Gestion de candidature */
		if (userController.canCurrentUserAccessView(CandidatInfoPersoView.class) && userController.isCandidatValid()) {
			//addItemMenu(applicationContext.getMessage("MonCompteMenu.title", null, getLocale()), CandidatInfoPersoView.NAME, FontAwesome.HOME,null,null);
			AccordionItemMenu itemMenuCandidat = new AccordionItemMenu(applicationContext.getMessage("compte.main.menu", null, getLocale()),accordionMenu, false);
			accordionMenu.addItemMenu(itemMenuCandidat, ConstanteUtils.UI_MENU_CAND);
			
			buildMenuCandidat(itemMenuCandidat);			
			/*subMenuCompte.add(new SubMenu(CandidatBacView.NAME, FontAwesome.BOOK));
			subMenuCompte.add(new SubMenu(CandidatCursusUnivView.NAME, FontAwesome.GRADUATION_CAP));			
			subMenuCompte.add(new SubMenu(CandidatInfoProView.NAME, FontAwesome.USERS));*/
		}
		
		/*String fragment = Page.getCurrent().getUriFragment();
		System.out.println(fragment);
		if (!changeView){
			navigateToView(AccueilView.NAME);
		}*/
		
		//System.out.println(navigator.getCurrentView().getClass());
		focusCurrentMenu(currentViewName);
		focusCurrentAccordion(currentViewName);
	}
	
	/** Verifie la concordance du candidat en cours d'édition avec les menus
	 * @param noDossierCandidat
	 * @return true si ok, false si nok
	 */
	public Boolean checkConcordanceCandidat(String noDossierCandidat){
		if (noDossierCandidatEnCours!=null && noDossierCandidat!=null && !noDossierCandidatEnCours.equals(noDossierCandidat)){
			Notification.show(applicationContext.getMessage("cptMin.change.error", null, getLocale()));
			constructMainMenu();
			return false;
		}
		return true;
	}
	
	/** Contruit le menu candidat
	 * @param itemMenu l'item de menu du candidat
	 */
	private void buildMenuCandidat(AccordionItemMenu itemMenu){
		/*LinkedList<SubMenu> subMenuInfoPerso = new LinkedList<SubMenu>();
		subMenuInfoPerso.add(new SubMenu(CandidatInfoPersoView.NAME, FontAwesome.PENCIL));			
		subMenuInfoPerso.add(new SubMenu(CandidatAdresseView.NAME, FontAwesome.HOME));
		addItemMenu(applicationContext.getMessage("main.menu.infoperso.title", null, getLocale()), null, FontAwesome.USER ,subMenuInfoPerso, itemMenu);
		
		LinkedList<SubMenu> subMenuCursusUniv = new LinkedList<SubMenu>();
		subMenuCursusUniv.add(new SubMenu(CandidatBacView.NAME, FontAwesome.BOOK));
		subMenuCursusUniv.add(new SubMenu(CandidatCursusInterneView.NAME, FontAwesome.UNIVERSITY));			
		subMenuCursusUniv.add(new SubMenu(CandidatCursusExterneView.NAME, FontAwesome.GRADUATION_CAP));
		addItemMenu(applicationContext.getMessage("main.menu.cursusscol.title", null, getLocale()), null, FontAwesome.BOOK ,subMenuCursusUniv, itemMenu);
		
		LinkedList<SubMenu> subMenuCursusPro = new LinkedList<SubMenu>();
		subMenuCursusPro.add(new SubMenu(CandidatStageView.NAME, FontAwesome.CUBE));			
		subMenuCursusPro.add(new SubMenu(CandidatFormationProView.NAME, FontAwesome.CUBES));
		addItemMenu(applicationContext.getMessage("main.menu.cursuspro.title", null, getLocale()), null, FontAwesome.CUBE ,subMenuCursusPro, itemMenu);*/
		
		addItemMenu(applicationContext.getMessage("candidatInfoPersoView.title.short", null, getLocale()), CandidatInfoPersoView.NAME, FontAwesome.PENCIL ,null, itemMenu);
		addItemMenu(applicationContext.getMessage(CandidatAdresseView.NAME+".title", null, getLocale()), CandidatAdresseView.NAME, FontAwesome.HOME ,null, itemMenu);
		addItemMenu(applicationContext.getMessage(CandidatBacView.NAME+".title", null, getLocale()), CandidatBacView.NAME, FontAwesome.BOOK ,null, itemMenu);
		addItemMenu(applicationContext.getMessage(CandidatCursusInterneView.NAME+".title", null, getLocale()), CandidatCursusInterneView.NAME, FontAwesome.UNIVERSITY ,null, itemMenu);
		addItemMenu(applicationContext.getMessage(CandidatCursusExterneView.NAME+".title", null, getLocale()), CandidatCursusExterneView.NAME, FontAwesome.GRADUATION_CAP ,null, itemMenu);
		addItemMenu(applicationContext.getMessage(CandidatStageView.NAME+".title", null, getLocale()), CandidatStageView.NAME, FontAwesome.CUBE ,null, itemMenu);
		addItemMenu(applicationContext.getMessage("candidatFormationProView.title.short", null, getLocale()), CandidatFormationProView.NAME, FontAwesome.CUBES ,null, itemMenu);
		
		addItemMenu(applicationContext.getMessage("main.menu.candidature.title", null, getLocale()), CandidatCandidaturesView.NAME, FontAwesome.ASTERISK ,null, itemMenu);
		
		if (userController.isGestionnaire()){
			addItemMenu(applicationContext.getMessage("gestcand.adminmenu", null, getLocale()), CandidatAdminView.NAME, FontAwesome.FLASH ,null, itemMenu);
			viewAccordionGestCandidat.put(CandidatInfoPersoView.NAME, (String)itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatAdresseView.NAME, (String)itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatBacView.NAME, (String)itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatCursusInterneView.NAME, (String)itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatCursusExterneView.NAME, (String)itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatStageView.NAME, (String)itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatFormationProView.NAME, (String)itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatCandidaturesView.NAME, (String)itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatAdminView.NAME, (String)itemMenu.getData());
		}
	}
	
	/**
	 * Construit le menu de gestion de candidature
	 */
	public void buildMenuGestCand(){		
		String noDossier = userController.getNoDossierCandidat();
		String name = userController.getDisplayNameCandidat();
		if (name == null || name.equals("")){
			name = noDossier;
		}
		if (name!=null && !name.equals("")){	
			noDossierCandidatEnCours = userController.getNoDossierCandidat();
			changeCandBtn.setCaption(name);
			changeCandBtn.setIcon(null);
			if (itemMenuGestCandidat.getNbButton()<=2){
				buildMenuCandidat(itemMenuGestCandidat);
			}
			navigateToView(CandidatInfoPersoView.NAME);
		}else{
			itemMenuGestCandidat.removeAllButtons(changeCandBtn, createCandBtn);		
			viewAccordionGestCandidat.forEach((key,value)->{
				viewButtons.remove(key);
				viewAccordion.remove(key);
			});
			viewAccordionGestCandidat.clear();
			changeCandBtn.setCaption(applicationContext.getMessage("btn.find.candidat", null, getLocale()));
			changeCandBtn.setIcon(FontAwesome.SEARCH);
			changeCandBtn.setVisible(true);
			navigateToView(AccueilView.NAME);
		}
	}
	
	/**
	 * Construit le menu centre de candidature
	 */
	public void buildMenuCtrCand(){
		itemMenuCtrCand.removeAllButtons();		
		viewAccordionCtrCand.forEach((key,value)->{
			viewButtons.remove(key);
			viewAccordion.remove(key);
		});
		viewAccordionCtrCand.clear();
		
		SecurityCentreCandidature centreCandidature = userController.getCentreCandidature();
		if (centreCandidature!=null){
			idCtrCandEnCours = centreCandidature.getIdCtrCand();
			Button ctrCandBtn = constructCtrCandChangeBtn(centreCandidature.getLibCtrCand());
			ctrCandBtn.setDescription(applicationContext.getMessage("ctrCand.window.change", new Object[]{centreCandidature.getLibCtrCand()}, getLocale()));
			itemMenuCtrCand.addButton(ctrCandBtn);			
			
			Boolean isScolCentrale = userController.isScolCentrale();
			
			List<DroitProfilFonc> listFonctionnalite = centreCandidature.getListFonctionnalite();
			if (hasAccessToFonctionnalite(isScolCentrale,listFonctionnalite,NomenclatureUtils.FONCTIONNALITE_PARAM)){
				addItemMenu(applicationContext.getMessage(CtrCandParametreView.NAME + ".title", null, getLocale()), CtrCandParametreView.NAME, FontAwesome.COG,null,itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandParametreView.NAME,(String)itemMenuCtrCand.getData());
			}
			
			if (hasAccessToFonctionnalite(isScolCentrale,listFonctionnalite,NomenclatureUtils.FONCTIONNALITE_GEST_COMMISSION)){
				addItemMenu(applicationContext.getMessage(CtrCandCommissionView.NAME + ".title", null, getLocale()), CtrCandCommissionView.NAME, FontAwesome.ARCHIVE,null,itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandCommissionView.NAME, (String)itemMenuCtrCand.getData());
			}
			
			if (hasAccessToFonctionnalite(isScolCentrale,listFonctionnalite,NomenclatureUtils.FONCTIONNALITE_GEST_PJ)){
				LinkedList<SubMenu> subMenuPj = new LinkedList<SubMenu>();
				subMenuPj.add(new SubMenu(CtrCandPieceJustifView.NAME, FontAwesome.FILE_TEXT));
				subMenuPj.add(new SubMenu(CtrCandPieceJustifCommunView.NAME, FontAwesome.FILES_O));
				
				addItemMenu(applicationContext.getMessage(CtrCandPieceJustifView.NAME + ".title", null, getLocale()), CtrCandPieceJustifView.NAME, FontAwesome.FILE_TEXT,subMenuPj,itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandPieceJustifView.NAME, (String)itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandPieceJustifCommunView.NAME, (String)itemMenuCtrCand.getData());
			}
			
			if (hasAccessToFonctionnalite(isScolCentrale,listFonctionnalite,NomenclatureUtils.FONCTIONNALITE_GEST_FORMULAIRE)){
				LinkedList<SubMenu> subMenuForm = new LinkedList<SubMenu>();
				subMenuForm.add(new SubMenu(CtrCandFormulaireView.NAME, FontAwesome.FILE_ZIP_O));
				subMenuForm.add(new SubMenu(CtrCandFormulaireCommunView.NAME, FontAwesome.FILES_O));
				
				addItemMenu(applicationContext.getMessage(CtrCandFormulaireView.NAME + ".title", null, getLocale()), CtrCandFormulaireView.NAME, FontAwesome.FILE_ZIP_O,subMenuForm,itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandFormulaireView.NAME, (String)itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandFormulaireCommunView.NAME, (String)itemMenuCtrCand.getData());
			}
			
			if (hasAccessToFonctionnalite(isScolCentrale,listFonctionnalite,NomenclatureUtils.FONCTIONNALITE_GEST_FORMATION)){
				addItemMenu(applicationContext.getMessage(CtrCandFormationView.NAME + ".title", null, getLocale()), CtrCandFormationView.NAME, FontAwesome.LEAF,null,itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandFormationView.NAME, (String)itemMenuCtrCand.getData());
			}
			
			if (hasAccessToFonctionnalite(isScolCentrale,listFonctionnalite,NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE)){
				LinkedList<SubMenu> subMenuCandidatures = new LinkedList<SubMenu>();
				subMenuCandidatures.add(new SubMenu(CtrCandCandidatureView.NAME, FontAwesome.BRIEFCASE));
				subMenuCandidatures.add(new SubMenu(CtrCandCandidatureCanceledView.NAME, FontAwesome.WARNING));
				subMenuCandidatures.add(new SubMenu(CtrCandCandidatureArchivedView.NAME, FontAwesome.FOLDER_OPEN));
				
				addItemMenu(applicationContext.getMessage(CtrCandCandidatureView.NAME + ".title", null, getLocale()), CtrCandCandidatureView.NAME, FontAwesome.BRIEFCASE,subMenuCandidatures,itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandCandidatureView.NAME, (String)itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandCandidatureCanceledView.NAME, (String)itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandCandidatureArchivedView.NAME, (String)itemMenuCtrCand.getData());
			}	
		}else{
			Button ctrCandBtn = constructCtrCandChangeBtn(applicationContext.getMessage("ctrCand.window.change.default", null, getLocale()));
			itemMenuCtrCand.addButton(ctrCandBtn);
		}
	}
	
	
	/** Construit le bouton de recherche de centre
	 * @param libelle le libelle du bouton
	 * @return	le bouton de recherche
	 */
	private Button constructCtrCandChangeBtn(String libelle){
		Button ctrCandBtn = new Button(libelle);
		ctrCandBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
		ctrCandBtn.addClickListener(e->{
			SearchCtrCandWindow win = new SearchCtrCandWindow();
			win.addCentreCandidatureListener(centre->{
				userController.setCentreCandidature(centre);
				buildMenuCtrCand();
				navigateToView(AccueilView.NAME);
				idCtrCandEnCours = centre.getIdCtrCand();				
			});
			getCurrent().addWindow(win);
		});
		return ctrCandBtn;
	}
	
	/** Verifie la concordance de la commission en cours d'édition
	 * @param idCtrCand
	 */
	public Boolean checkConcordanceCentreCandidature(Integer idCtrCand){
		if (idCtrCandEnCours!=null && idCtrCand!=null && idCtrCandEnCours!=idCtrCand){
			Notification.show(applicationContext.getMessage("ctrCand.change.error", null, getLocale()));
			constructMainMenu();
			return false;
		}
		return true;
	}
	
	/**
	 * Construit le menu de commission
	 */
	public void buildMenuCommission(){
		itemMenuCommission.removeAllButtons();		
		viewAccordionCommission.forEach((key,value)->{
			viewButtons.remove(key);
			viewAccordion.remove(key);
		});
		viewAccordionCommission.clear();
		
		SecurityCommission commission = userController.getCommission();
		if (commission!=null){
			idCommissionEnCours = commission.getIdComm();
			
			Button commissionBtn = constructCommissionChangeBtn(commission.getLibComm());
			commissionBtn.setDescription(applicationContext.getMessage("commission.window.change", new Object[]{commission.getLibComm()}, getLocale()));
			itemMenuCommission.addButton(commissionBtn);
			
			addItemMenu(applicationContext.getMessage(CommissionCandidatureView.NAME + ".title", null, getLocale()), CommissionCandidatureView.NAME, FontAwesome.BRIEFCASE,null,itemMenuCommission);
			viewAccordionCommission.put(CommissionCandidatureView.NAME, (String)itemMenuCommission.getData());
		}else{
			Button commissionBtn = constructCommissionChangeBtn(applicationContext.getMessage("commission.window.change.default", null, getLocale()));
			itemMenuCommission.addButton(commissionBtn);
		}
	}
	
	/** Construit le bouton de recherche de commission
	 * @param libelle le libellé du bouton
	 * @return le bouton de recherche
	 */
	private Button constructCommissionChangeBtn(String libelle){
		Button commissionBtn = new Button(libelle);
		commissionBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
		commissionBtn.addClickListener(e->{
			SearchCommissionWindow win = new SearchCommissionWindow(null);
			win.addCommissionListener(comm->{
				userController.setCommission(comm);
				buildMenuCommission();
				navigateToView(CommissionCandidatureView.NAME);
				idCommissionEnCours = comm.getIdComm();
			});
			getCurrent().addWindow(win);
		});
		return commissionBtn;
	}
	
	/** Verifie la concordance de la commission en cours d'édition
	 * @param idCommission
	 */
	public void checkConcordanceCommission(Integer idCommission){
		if (idCommissionEnCours!=null && idCommission!=null && idCommissionEnCours!=idCommission){
			Notification.show(applicationContext.getMessage("commission.change.error", null, getLocale()));
			constructMainMenu();
		}
	}

	/** Verifie si l'utilisateur a le droit d'accéder à la fonctionnalite
	 * @param isAdmin est-il admin
	 * @param listFonctionnalite la liste des fonctionnalite du gestionnaire
	 * @param codFonc le code de la fonctionnalite a tester
	 * @return true si il a acces, false sinon
	 */
	private Boolean hasAccessToFonctionnalite(Boolean isScolCentrale, List<DroitProfilFonc> listFonctionnalite,String codFonc){
		if (isScolCentrale){
			return true;
		}
		if (listFonctionnalite!=null && listFonctionnalite.stream().filter(e->e.getDroitFonctionnalite().getCodFonc().equals(codFonc)).findFirst().isPresent()){
			return true;
		}
		return false;
	}
	
	/**Ajout d'un menu d'item avec ou sans sous menu
	 * @param caption le libelle
	 * @param viewName la vue rattachee
	 * @param icon l'icon du menu
	 * @param itemMenu l'item menu rattache
	 * @param mapSubMenu un eventuel sous-menu
	 */
	private void addItemMenu(String caption, String viewName, com.vaadin.server.Resource icon, LinkedList<SubMenu> subMenus, AccordionItemMenu itemMenu) {
		Button itemBtn = new Button(caption, icon);
		Menu menu = new Menu(viewName,subMenus,itemBtn);
		itemBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
		/*Pas de sous menu*/
		if (subMenus==null){
			itemBtn.addClickListener(e -> {
				navigateToView(viewName);
			});
			viewButtons.put(viewName, menu);
			if (itemMenu!=null){
				viewAccordion.put(viewName, (String)itemMenu.getData());
			}
		}
		/*Des sous menu, on associe le bouton du menu à chaque vue de sous menu*/
		else{
			subMenus.forEach(e -> {
				viewButtons.put(e.getVue(), menu);
				if (itemMenu!=null){
					viewAccordion.put(e.getVue(), (String)itemMenu.getData());
				}
			});
			itemBtn.addClickListener(e -> {
				navigateToView(subMenus.getFirst().getVue());
			});
			
		}
		//itemBtn.setWidth(sizeBtn,Unit.PIXELS);
		if (itemMenu==null){
			menuButtonLayout.addComponent(itemBtn);
		}else{
			itemMenu.addButton(itemBtn);			
		}
		
	}
	
	/** Construction du sous-menu
	 * @param menu le menu
	 * @param vue la vue rattachee
	 */
	private void contructSubMenu(Menu menu, String vue){
		if (menu.hasSubMenu()){
			/*Si le menu n'a pas déjà été créé lors de la dernière action*/
			if (lastButtonView==null || !lastButtonView.equals(menu.getBtn())){
				subBarMenu.constructMenuBar(menu,navigator,vue);
			}else{
				//on bouge vers la vue
				subBarMenu.selectSubMenuSheet(menu, vue, navigator, true);
			}
			subBarMenu.setVisible(true);
		}else{
			subBarMenu.setVisible(false);
		}
		/*On stocke le dernier bouton cliqué pour ne pas avoir à reconstruire le menu à chaque fois*/
		lastButtonView = menu.getBtn();
	}
	
	/**
	* Configure la reconnexion en cas de déconnexion.
	*/
	private void configReconnectDialog() {
		getReconnectDialogConfiguration().setDialogModal(true);
		getReconnectDialogConfiguration().setReconnectAttempts(TENTATIVES_RECO);
		configReconnectDialogMessages();
	}
	
	/**
	 * Modifie les messages de reconnexion
	 */
	public void configReconnectDialogMessages() {
		getReconnectDialogConfiguration().setDialogText(
				applicationContext.getMessage("vaadin.reconnectDialog.text",
						null, getLocale()));
		getReconnectDialogConfiguration()
				.setDialogTextGaveUp(
						applicationContext.getMessage(
								"vaadin.reconnectDialog.textGaveUp", null,
								getLocale()));
	}

	/**
	 * Initialise le gestionnaire de vues
	 */
	private void initNavigator() {
		//navigator.setErrorProvider(new SpringErrorViewProvider(ErreurView.class, navigator));
		navigator.addProvider(viewProvider);
		navigator.setErrorProvider(new ViewProvider() {
			/**
			 * serialVersionUID
			 */
			private static final long serialVersionUID = -4519599785696009660L;

			@Override
			public String getViewName(final String viewAndParameters) {
				return ErreurView.NAME;
			}

			@Override
			public View getView(final String viewName) {
				return viewProvider.getView(ErreurView.NAME);
			}
		});
		navigator.addViewChangeListener(new ViewChangeListener() {
			private static final long serialVersionUID = 7905379446201794289L;			

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				
				if (!event.getViewName().equals(AccueilView.NAME) &&!event.getViewName().equals(ErreurView.NAME) 
						&& !event.getViewName().equals(CandidatCompteMinimaView.NAME)
						&& !event.getViewName().equals(MaintenanceView.NAME)
						&& !viewButtons.containsKey(event.getViewName())){
					navigateToView(ErreurView.NAME);
					return false;
				}
				viewButtons.values().forEach(menu -> menu.getBtn().removeStyleName(SELECTED_ITEM));
				if (uiController.redirectToMaintenanceView(event.getViewName())){
					navigateToView(MaintenanceView.NAME);
					return false;
				}
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				focusCurrentMenu(event.getViewName());
				Menu menu =  viewButtons.get(event.getViewName());
				if (menu!=null && menu.getBtn() instanceof Button) {
					focusCurrentMenu(event.getViewName());
					contructSubMenu(menu, event.getViewName());
				}
				//String id = getIdItemCLicked();
				focusCurrentAccordion(event.getViewName());
				currentViewName = event.getViewName();
			}
		});
		
		/* Résout la vue à afficher */
		String fragment = Page.getCurrent().getUriFragment();
		if (fragment == null || fragment.isEmpty()) {
			navigateToView(vueToDisplay);
		}
	}
	
	/** Focus le menu courant
	 * @param viewName
	 */
	private void focusCurrentMenu(String viewName){
		if (viewName!=null){
			Menu menu =  viewButtons.get(viewName);
			if (menu!=null && menu.getBtn() instanceof Button) {
				menu.getBtn().addStyleName(SELECTED_ITEM);
				menu.getBtn().focus();
			}
		}		
	}
	
	/** Focus l'accordéon courant
	 * @param viewName
	 */
	private void focusCurrentAccordion(String viewName){
		String idAccordion = viewAccordion.get(viewName);
		if (idAccordion!=null && !idAccordion.equals(accordionMenu.getItemId())){
			accordionMenu.changeItem(idAccordion);
		}
	}
	
	/**
	 * Initialise le tracker d'activité.
	 */
	private void initAnalyticsTracker() {
		if (piwikAnalyticsTrackerUrl instanceof String && piwikAnalyticsTrackerUrl!=null && !piwikAnalyticsTrackerUrl.equals("") &&
				piwikAnalyticsSiteId instanceof String && piwikAnalyticsSiteId!=null && !piwikAnalyticsSiteId.equals("")) {
			analyticsTracker = new PiwikAnalyticsTracker(this, piwikAnalyticsTrackerUrl, piwikAnalyticsSiteId);
		} else {
			analyticsTracker = new LogAnalyticsTracker();
		}
		analyticsTracker.trackNavigator(navigator);
	}
	
	/**
	 * @return true si le push est actif
	 */
	private Boolean isPushEnable(){
		Boolean pushEnable = Boolean.valueOf(enablePush);
		if (pushEnable == null){
			pushEnable = false;
		}
		return pushEnable;
	}
	
	/**
	 * @return true si le WebSocket est actif
	 */
	private Boolean isWebSocketPushEnable(){
		Boolean pushWSEnable = true;
		if (enableWebSocketPush instanceof String && enableWebSocketPush!=null && !enableWebSocketPush.equals("")){
			pushWSEnable = Boolean.valueOf(enableWebSocketPush);
			if (pushWSEnable == null){
				pushWSEnable = false;
			}			
		}		
		return pushWSEnable;
	}
	
	/**
	 * @see com.vaadin.ui.UI#detach()
	 */
	@Override
	public void detach() {
		lockCandidatController.removeAllLockUI();
		/* Se désinscrit de la réception de notifications */
		uiController.unregisterUI(this);

		super.detach();
	}
}
