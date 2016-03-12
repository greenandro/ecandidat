package fr.univlorraine.ecandidat.views;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CampagneController;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatureGestionController;
import fr.univlorraine.ecandidat.controllers.DemoController;
import fr.univlorraine.ecandidat.controllers.FormulaireController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.IndividuController;
import fr.univlorraine.ecandidat.controllers.LoadBalancingController;
import fr.univlorraine.ecandidat.controllers.MailController;
import fr.univlorraine.ecandidat.controllers.SiScolController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.TestController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.repositories.CompteMinimaRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.vaadin.components.ConnexionLayout;
import fr.univlorraine.ecandidat.views.windows.CandidatIdOublieWindow;

/**
 * Page d'accueil
 * @author Kevin Hergalant
 *
 */
@SpringView(name = AccueilView.NAME)
public class AccueilView extends VerticalLayout implements View {
	
	private Logger logger = LoggerFactory.getLogger(AccueilView.class);

	/** serialVersionUID **/
	private static final long serialVersionUID = -1892026915407604201L;

	public static final String NAME = "accueilView";
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient I18nController i18nController;
	
	private Label labelTitle = new Label();
	private Label labelAccueil = new Label("", ContentMode.HTML);
	private HorizontalLayout hlConnectedCreateCompte = new HorizontalLayout();
	private VerticalLayout vlConnexionWithCompte = new VerticalLayout();
	private Panel panelCreateCompte = new Panel();
	private Button logBtnNoCompte = new Button(FontAwesome.SIGN_OUT);
	private ConnexionLayout connexionLayout = new ConnexionLayout();
	
	private String title;
	
	/**TODO a retirer-->Test*/
	@Resource(name="${siscol.implementation}")
	private SiScolGenericService siScolService;
	
	@Resource
	private transient MailController mailController;
	@Resource
	private transient FormulaireController formulaireController;
	@Resource
	private transient DemoController demoController;
	@Resource
	private transient SiScolController siScolController;
	@Resource
	private transient CandidatureGestionController candidatureGestionController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient CompteMinimaRepository compteMinimaRepository;
	@Resource
	private transient TestController testController;
	
	/**TODO fin a retirer-->Test*/

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		//candidatController.nettoyageCptMinInvalides();
		/* Style */
		setMargin(true);
		setSpacing(true);
		
		if (testController.isTestMode()){
			logger.debug("Arrivé sur l'accueil");
			
			HorizontalLayout hlBtnTest = new HorizontalLayout();
			hlBtnTest.setSpacing(true);
			addComponent(hlBtnTest);
			
			Button btnCreate = new Button("Creer compte test");		
			btnCreate.addClickListener(e->{
				logger.debug("Click sur create");
				CompteMinima cpt =  testController.createCompteMinima();
				if (cpt!=null){
					connexionLayout.setLogin(cpt.getNumDossierOpiCptMin());
				}				
			});
			hlBtnTest.addComponent(btnCreate);
			
			
			Button btnDelete = new Button("Supression compte test");
			btnDelete.addClickListener(e->{
				logger.debug("Click sur delete");
				/*logger.debug("click sur delete du candidat");
				testController.deleteCandidat();
				connexionLayout.setLogin("");*/
				//testController.finish();
				testController.deleteCandidat();
			});
			//hlBtnTest.addComponent(btnDelete);			
			hlBtnTest.addComponent(btnDelete);
			
			Button btnCorrigeFile = new Button("Corrige fichier");
			btnCorrigeFile.addClickListener(e->{
				testController.afficheFichierPerdu();
			});
			//addComponent(btnCorrigeFile);
			
			
			//btnCreate.click();
			
			/*Button btnCandidat = new Button("Candidature");	
			
			btnCandidat.addClickListener(e->{
				testController.candidatToFormation();
			});
			addComponent(btnCandidat);
			
			Button btnOpen = new Button("Ouvrir Candidature");	
			
			btnOpen.addClickListener(e->{
				testController.openCandidature();
			});
			addComponent(btnOpen);
			
			Button btnDownload = new Button("Download dossier");			
			btnDownload.addClickListener(e->{
				testController.downloadDossier();
			});
			addComponent(btnDownload);
			
			Button btnSupp = new Button("Delete dossier");			
			btnSupp.addClickListener(e->{
				testController.deleteCandidat();
			});
			addComponent(btnSupp);
			
			Button btnAll = new Button("Tout En Un");			
			btnAll.addClickListener(e->{
				testController.allInOne();
			});
			addComponent(btnAll);
			
			*/
			
			
			Button btn = new Button("Test");		
			btn.addClickListener(e->{
				/*UI.getCurrent().addWindow(new InformationWindow(
						applicationContext.getMessage("informationImportanteWindow.tite", null, UI.getCurrent().getLocale()),
						applicationContext.getMessage("candidature.validPJ.window.info.afteraction", new Object[]{"21/12/2015"}, UI.getCurrent().getLocale()),
						425, null));*/
				try {
					
					//siScolService.getAdresse("31118612");
					/*System.out.println(siScolService.getIndividu("31118613", null, null));
					System.out.println(siScolService.getIndividu(null, "1204014627", "B"));
					System.out.println(siScolService.getAdresse("31118613"));
					//System.out.println(siScolService.getINE("31118613"));
					System.out.println(siScolService.getBac("30303977"));*/
					//formulaireController.launchBatchSyncLimeSurvey();
					//demoController.launchDemoBatch();
					//candidatureGestionController.generateOpi(candidatureRepository.findOne(1));
					//System.out.println(demoController.getDemoMode());
					/*System.out.println(individuController.getCodCgeUserByLogin("admin"));
					System.out.println(individuController.getCodCgeUserByLogin("ADMIN"));
					System.out.println(individuController.getCodCgeUserByLogin("totots"));*/
					//testController.createCandidats();
					
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}
				candidatureGestionController.launchBatchAsyncOPI();
				//formulaireController.launchBatchSyncLimeSurvey();
				/*CandidatMailBean bean = new CandidatMailBean();
				CandidatureMailBean cBean = new CandidatureMailBean(bean,null);
				bean.setNomPat("Hergalant");
				bean.setPrenom("Kevin");
				bean.setNumDossierOpi(null);
				bean.setDatNaiss("08/06/1980");
				String contentMail = "Bonjour {if($candidat.datNaiss)}voici ma date de naissance ${candidat.datNaiss} au format dd/mm/yyyy {endif($candidat.datNaiss)}${candidat.prenom} ${candidat.nomPat},<br><br>Votre {if($candidat.numDossierOpi)}bonjour, je veux de l'argent ${candidat.numDossierOpi} {endif($candidat.numDossierOpi)}candidature à la formation '${formation.libelle}' vient d'avoir un avis favorable.<br><br> Vous devez consulter l'application eCandidat pour confirmer ou vous desister${dateConfirmation}.<br>${commentaire}";
				String var = NomenclatureUtils.MAIL_CANDIDAT_GEN_VAR;
				
				contentMail = mailController.parseVar2(contentMail, var, cBean);*/
				//System.out.println(contentMail);
				/*String complementPreselect = "sdfjsdkfjs qsdlkjqslkdj ";
				if (complementPreselect!=null && complementPreselect.length()!=0 && complementPreselect.substring(complementPreselect.length()-1, complementPreselect.length()).equals(" ")){
					complementPreselect = complementPreselect.substring(0,complementPreselect.length()-1);
				}
				System.out.println("**"+complementPreselect+"**");*/
				
			});
			//addComponent(btn);
		}
		
		

		/* Titre */
		HorizontalLayout hlLangue = new HorizontalLayout();
		hlLangue.setWidth(100, Unit.PERCENTAGE);
		hlLangue.setSpacing(true);
		
		/*Le titre*/
		labelTitle.addStyleName(ValoTheme.LABEL_H2);
		hlLangue.addComponent(labelTitle);
		hlLangue.setExpandRatio(labelTitle, 1);
		hlLangue.setComponentAlignment(labelTitle, Alignment.MIDDLE_LEFT);
		
		if (tableRefController.getLangueEnService().size()>0){
			Langue langueDef = tableRefController.getLangueDefault();
			Image flagDef = new Image(null, new ThemeResource("images/flags/"+langueDef.getCodLangue()+".png"));
			flagDef.addClickListener(e->changeLangue(langueDef));
			flagDef.addStyleName(StyleConstants.CLICKABLE);
			hlLangue.addComponent(flagDef);
			hlLangue.setComponentAlignment(flagDef, Alignment.MIDDLE_CENTER);
			tableRefController.getLangueEnService().forEach(langue->{
				Image flag = new Image(null, new ThemeResource("images/flags/"+langue.getCodLangue()+".png"));
				flag.addClickListener(e->changeLangue(langue));
				flag.addStyleName(StyleConstants.CLICKABLE);
				hlLangue.addComponent(flag);
				hlLangue.setComponentAlignment(flag, Alignment.MIDDLE_CENTER);
				
			});
		}

		addComponent(hlLangue);

		/* Texte */		
		addComponent(labelAccueil);
		
		if (campagneController.getCampagneActive()==null){
			addComponent(new Label(applicationContext.getMessage("accueilView.nocampagne", null, UI.getCurrent().getLocale())));
			return;
		}
		
		/*Connexion*/		
		/*Avec compte*/
		vlConnexionWithCompte.setWidth(500, Unit.PIXELS);
		addComponent(vlConnexionWithCompte);
		connexionLayout.addCasListener(()->userController.connectCAS());
		connexionLayout.addStudentListener((user,pwd)->userController.connectCandidatInterne(user, pwd));
		connexionLayout.addForgotPasswordListener(()->{UI.getCurrent().addWindow(new CandidatIdOublieWindow());});
		connexionLayout.addCreateCompteListener(()->candidatController.createCompteMinima(false));
		vlConnexionWithCompte.addComponent(connexionLayout);
		
		/*Connecté mais sans compte candidat*/
		hlConnectedCreateCompte.setWidth(500, Unit.PIXELS);
		addComponent(hlConnectedCreateCompte);
		
		panelCreateCompte.setCaption(applicationContext.getMessage("accueilView.title.nocompte", null, UI.getCurrent().getLocale()));
		panelCreateCompte.addStyleName(StyleConstants.PANEL_COLORED);
		VerticalLayout vlCreateCompte = new VerticalLayout();
		vlCreateCompte.setSpacing(true);
		vlCreateCompte.setMargin(true);
		panelCreateCompte.setContent(vlCreateCompte);
		hlConnectedCreateCompte.addComponent(panelCreateCompte);

		logBtnNoCompte.setCaption(applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()));
        logBtnNoCompte.addClickListener(e -> {
        	candidatController.createCompteMinima(false);
		});
        vlCreateCompte.addComponent(logBtnNoCompte);
	}
	
	/** Change la langue de l'utilisateur et rafraichi les infos
	 * @param langue
	 */
	private void changeLangue(Langue langue){
		i18nController.changeLangue(langue);
		//UI.getCurrent().setLocale(new Locale(langue.getCodLangue()));
		labelTitle.setValue(title);
		panelCreateCompte.setCaption(applicationContext.getMessage("accueilView.title.nocompte", null, UI.getCurrent().getLocale()));
		logBtnNoCompte.setCaption(applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()));
		setTxtMessageAccueil();
		refreshLayoutConnexion();
		//((MainUI)UI.getCurrent()).constructMainMenu();
	}
	
	/**
	 * Rafrachi le layout de connexion
	 */
	private void refreshLayoutConnexion(){
		if (loadBalancingController.isLoadBalancingGestionnaireMode()){
			vlConnexionWithCompte.setVisible(false);
			connexionLayout.setClickShortcut(false);
			hlConnectedCreateCompte.setVisible(false);
			return;
		}
		if (!userController.isAnonymous()){
			vlConnexionWithCompte.setVisible(false);
			connexionLayout.setClickShortcut(false);
			if (!userController.isGestionnaire() && !userController.isCommissionMember() && !userController.isCandidat()){
				hlConnectedCreateCompte.setVisible(true);
			}else{
				hlConnectedCreateCompte.setVisible(false);
			}
			return;
		}else{			
			hlConnectedCreateCompte.setVisible(false);
			vlConnexionWithCompte.setVisible(true);
			connexionLayout.setClickShortcut(true);
		}
		refreshConnexionPanelWithCompte();
	}
	
	/**
	 * Rafraichi le panel de connexion avec un compte
	 */
	private void refreshConnexionPanelWithCompte(){			
		connexionLayout.updateLibelle(applicationContext.getMessage("accueilView.title.etu", new Object[]{applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale())}, UI.getCurrent().getLocale()),
				applicationContext.getMessage("accueilView.title.nonetu", new Object[]{applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale())}, UI.getCurrent().getLocale()),
				applicationContext.getMessage("accueilView.connect.cas", null, UI.getCurrent().getLocale()),applicationContext.getMessage("accueilView.connect.ec", null, UI.getCurrent().getLocale()),
				applicationContext.getMessage("accueilView.connect.user", null, UI.getCurrent().getLocale()),applicationContext.getMessage("accueilView.connect.mdp", null,UI.getCurrent().getLocale()),
				applicationContext.getMessage("compteMinima.id.oublie.title", null, UI.getCurrent().getLocale()),applicationContext.getMessage("btnConnect", null, UI.getCurrent().getLocale()),
				applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()));
	}
	
	/**
	 * @return le texte de message d'accueil
	 */
	private String setTxtMessageAccueil(){
		String txt = "";
		if (!userController.isAnonymous()){
			txt += applicationContext.getMessage("accueilView.welcome", new Object[]{userController.getCurrentUserName()}, UI.getCurrent().getLocale());			
			
			if (userController.isPersonnel()){
				txt += applicationContext.getMessage("accueilView.connected", new Object[]{userController.getCurrentUserLogin()}, UI.getCurrent().getLocale());
				txt += applicationContext.getMessage("accueilView.role", new Object[]{userController.getCurrentAuthentication().getAuthorities()}, UI.getCurrent().getLocale());
			}else if (userController.isCandidat()){
				txt += applicationContext.getMessage("accueilView.connected", new Object[]{userController.getCurrentNoDossierCptMinOrLogin()}, UI.getCurrent().getLocale());
				if (userController.isCandidatValid()){
					txt += applicationContext.getMessage("accueilView.cand.connected", null, UI.getCurrent().getLocale());
				}else{
					txt += applicationContext.getMessage("compteMinima.connect.valid.error", null, UI.getCurrent().getLocale());
				}
				
			}
		}else if (loadBalancingController.isLoadBalancingGestionnaireMode()){
			txt = applicationContext.getMessage("accueilView.app.gest", null, UI.getCurrent().getLocale());
		}
		if (txt!=null && !txt.equals("")){
			labelAccueil.setValue(txt);
			labelAccueil.setVisible(true);
		}else{
			labelAccueil.setVisible(false);
		}
		
		return txt;
	}
	
	/**
	 * Rafraichi la vue
	 */
	private void refreshView(){
		if (userController.isAnonymous()){
			title = applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale());
		}else{
			title = applicationContext.getMessage("main.menu.accueil.title", null, UI.getCurrent().getLocale());
		}
		labelTitle.setValue(title);
		setTxtMessageAccueil();
		refreshLayoutConnexion();
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		refreshView();
	}

}
