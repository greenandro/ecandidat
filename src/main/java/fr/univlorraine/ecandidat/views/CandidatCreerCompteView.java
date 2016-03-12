package fr.univlorraine.ecandidat.views;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;

/**
 * Page de creation de compte du candidat
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CandidatCreerCompteView.NAME)
public class CandidatCreerCompteView extends VerticalLayout implements View {

	/** serialVersionUID **/
	private static final long serialVersionUID = -1892026915407604201L;

	public static final String NAME = "candidatCreerCompteView";

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
	private transient I18nController i18nController;
	
	private Label labelTitle = new Label();
	private Label labelAccueil = new Label();
	private HorizontalLayout hlConnectedCreateCompte = new HorizontalLayout();
	private Panel panelIsStudent = new Panel();
	private Panel panelNotStudent = new Panel();
	private Panel panelCreateCompte = new Panel();
	private VerticalLayout vlConnexionIsStudent = new VerticalLayout();
	private VerticalLayout vlConnexionNotStudent = new VerticalLayout();
	private Button logBtnNoCompte = new Button(FontAwesome.SIGN_OUT);

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		//candidatController.nettoyageCptMinInvalides();
		/* Style */
		setMargin(true);
		setSpacing(true);		

		/* Titre */
		HorizontalLayout hlLangue = new HorizontalLayout();
		hlLangue.setWidth(100, Unit.PERCENTAGE);
		hlLangue.setSpacing(true);
		
		/*Le titre*/
		labelTitle.setValue(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
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
		labelAccueil.setValue("");
		labelAccueil.setContentMode(ContentMode.HTML);
		
		addComponent(labelAccueil);
		
		if (campagneController.getCampagneActive()==null){
			addComponent(new Label(applicationContext.getMessage("accueilView.nocampagne", null, UI.getCurrent().getLocale())));
			return;
		}
		

		/*Connexion CAS*/
		panelIsStudent.setCaption(applicationContext.getMessage("accueilView.title.etu", new Object[]{applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale())}, UI.getCurrent().getLocale()));
		panelIsStudent.setWidth(500, Unit.PIXELS);
		panelIsStudent.addStyleName(StyleConstants.PANEL_COLORED);		
		vlConnexionIsStudent.setSpacing(true);
		vlConnexionIsStudent.setMargin(true);
		panelIsStudent.setContent(vlConnexionIsStudent);
		addComponent(panelIsStudent);
		
		/*Creation sans compte cas*/
		panelNotStudent.setCaption(applicationContext.getMessage("accueilView.title.nonetu", new Object[]{applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale())}, UI.getCurrent().getLocale()));
		panelNotStudent.setWidth(500, Unit.PIXELS);
		panelNotStudent.addStyleName(StyleConstants.PANEL_COLORED);		
		vlConnexionNotStudent.setSpacing(true);
		vlConnexionNotStudent.setMargin(true);
		panelNotStudent.setContent(vlConnexionNotStudent);
		addComponent(panelNotStudent);
		
		
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
		labelTitle.setValue(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
		panelCreateCompte.setCaption(applicationContext.getMessage("accueilView.title.nocompte", null, UI.getCurrent().getLocale()));
		logBtnNoCompte.setCaption(applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()));
		setTxtMessageAccueil();
		refreshLayoutConnexion();
	}
	
	/**
	 * Rafrachi le layout de connexion
	 */
	private void refreshLayoutConnexion(){
		if (!userController.isAnonymous()){
			panelIsStudent.setVisible(false);
			panelNotStudent.setVisible(false);
			if (!userController.isGestionnaire() && !userController.isCandidat()){
				hlConnectedCreateCompte.setVisible(true);
			}else{
				hlConnectedCreateCompte.setVisible(false);
			}
			return;
		}else{
			hlConnectedCreateCompte.setVisible(false);
			panelNotStudent.setVisible(true);
			panelIsStudent.setVisible(true);
		}
		refreshConnexionPanelStudent();
		refreshConnexionPanelNotStudent();
	}
	
	/**
	 * Rafraichi le panel de connexion sans compte
	 */
	private void refreshConnexionPanelStudent(){
		vlConnexionIsStudent.removeAllComponents();
		
		Button logBtn = new Button(applicationContext.getMessage("btnConnect", null, UI.getCurrent().getLocale()), FontAwesome.SIGN_OUT);
		logBtn.addClickListener(e -> {
			userController.connectCAS();
		});
		
		
		HorizontalLayout hlConnect = new HorizontalLayout();
		hlConnect.setSpacing(true);
		Label labelConnect = new Label(applicationContext.getMessage("accueilView.connect.cas", null, UI.getCurrent().getLocale()));
		hlConnect.addComponent(labelConnect);
		hlConnect.setComponentAlignment(labelConnect, Alignment.MIDDLE_LEFT);
		hlConnect.addComponent(logBtn);
		hlConnect.setComponentAlignment(logBtn, Alignment.MIDDLE_CENTER);
		
		vlConnexionIsStudent.addComponent(hlConnect);
	}
	
	/**
	 * Rafraichi le panel de connexion sans compte
	 */
	private void refreshConnexionPanelNotStudent(){
		vlConnexionNotStudent.removeAllComponents();		
		
        Button logBtnNoCompte = new Button(applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()), FontAwesome.SIGN_OUT);
        logBtnNoCompte.addClickListener(e -> {
        	candidatController.createCompteMinima(false);
		});
        vlConnexionNotStudent.addComponent(logBtnNoCompte);
	}
	
	/**
	 * @return le texte de message d'accueil
	 */
	private String setTxtMessageAccueil(){
		String txt = "";
		if (!userController.isAnonymous()){
			txt += applicationContext.getMessage("accueilView.welcome", null, UI.getCurrent().getLocale());
			txt += applicationContext.getMessage("accueilView.connected", new Object[]{userController.getCurrentUserName()}, UI.getCurrent().getLocale());
			if (userController.isGestionnaire()){
				txt += applicationContext.getMessage("accueilView.role", new Object[]{userController.getCurrentAuthentication().getAuthorities()}, UI.getCurrent().getLocale());
			}else if (userController.isCandidat()){
				txt += applicationContext.getMessage("accueilView.cand.connected", null, UI.getCurrent().getLocale());
			}
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
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		setTxtMessageAccueil();
		refreshLayoutConnexion();
	}

}
