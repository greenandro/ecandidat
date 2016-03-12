package fr.univlorraine.ecandidat.views;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.ConnexionLayout;
import fr.univlorraine.ecandidat.views.windows.CandidatIdOublieWindow;

/**
 * Page de gestion du compte a minima du candidat
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CandidatCompteMinimaView.NAME)
public class CandidatCompteMinimaView extends VerticalLayout implements View {

	/** serialVersionUID **/
	private static final long serialVersionUID = -1892026915407604201L;

	public static final String NAME = "candidatCompteMinimaView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient I18nController i18nController;
	
	private Label restResult = new Label();
	private Label labelTitle = new Label();
	private Label labelAccueil = new Label();
	private String restResultParam;
	private ConnexionLayout connexionLayout = new ConnexionLayout();

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setMargin(true);
		setSpacing(true);

		
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
			flagDef.addClickListener(e->updateLangue(langueDef));
			flagDef.addStyleName(StyleConstants.CLICKABLE);
			hlLangue.addComponent(flagDef);
			hlLangue.setComponentAlignment(flagDef, Alignment.MIDDLE_CENTER);
			tableRefController.getLangueEnService().forEach(langue->{
				Image flag = new Image(null, new ThemeResource("images/flags/"+langue.getCodLangue()+".png"));
				flag.addClickListener(e->updateLangue(langue));
				flag.addStyleName(StyleConstants.CLICKABLE);
				hlLangue.addComponent(flag);
				hlLangue.setComponentAlignment(flag, Alignment.MIDDLE_CENTER);
				
			});
		}

		addComponent(hlLangue);
		
		
		restResult.setContentMode(ContentMode.HTML);
		restResult.addStyleName(StyleConstants.LABEL_MORE_BOLD);
		restResult.addStyleName(StyleConstants.LABEL_COLORED);
		restResult.setValue("");
		addComponent(restResult);
		
		/* Texte */		
		labelAccueil.setValue("");
		labelAccueil.setContentMode(ContentMode.HTML);		
		addComponent(labelAccueil);
		
		connexionLayout.setWidth(500, Unit.PIXELS);
		connexionLayout.addCasListener(()->userController.connectCAS());
		connexionLayout.addStudentListener((user,pwd)->userController.connectCandidatInterne(user, pwd));
		connexionLayout.addForgotPasswordListener(()->{UI.getCurrent().addWindow(new CandidatIdOublieWindow());});
		addComponent(connexionLayout);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		userController.validSecurityUserCptMin();
		restResultParam = event.getParameters();
		if (restResultParam==null || restResultParam.equals("") || restResultParam.equals(ConstanteUtils.REST_VALID_ERROR)){
			restResultParam = ConstanteUtils.REST_VALID_ERROR;
		}
		if (userController.isCandidat() && (restResultParam.equals(ConstanteUtils.REST_VALID_ALREADY_VALID) || restResultParam.equals(ConstanteUtils.REST_VALID_SUCCESS))){
			connexionLayout.setVisible(false);			
			((MainUI) UI.getCurrent()).constructMainMenu();
		}else if (restResultParam.equals(ConstanteUtils.REST_VALID_ALREADY_VALID) || restResultParam.equals(ConstanteUtils.REST_VALID_SUCCESS)){
			connexionLayout.setVisible(true);
		}else{
			connexionLayout.setVisible(false);
		}
		updateLangue(tableRefController.getLangueDefault());
	}
	
	/**
	 * Internationalisation-->calcul du texte a afficher
	 */
	private void updateLangue(Langue langue){
		i18nController.changeLangue(langue);
		labelTitle.setValue(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
		
		restResult.setValue(applicationContext.getMessage("compteMinima.valid."+restResultParam, null, UI.getCurrent().getLocale()));
		String txtAccueil = "";
		if (userController.isCandidat() && (restResultParam.equals(ConstanteUtils.REST_VALID_ALREADY_VALID) || restResultParam.equals(ConstanteUtils.REST_VALID_SUCCESS))){
			txtAccueil += applicationContext.getMessage("accueilView.connected", new Object[]{userController.getCurrentUserLogin()}, UI.getCurrent().getLocale());
			txtAccueil += applicationContext.getMessage("accueilView.cand.connected", null, UI.getCurrent().getLocale());
		}else if (restResultParam.equals(ConstanteUtils.REST_VALID_ALREADY_VALID) || restResultParam.equals(ConstanteUtils.REST_VALID_SUCCESS)){
			txtAccueil += applicationContext.getMessage("accueilView.connect.cas", null, UI.getCurrent().getLocale());
		}
		if (!txtAccueil.equals("")){
			labelAccueil.setValue(txtAccueil);
			labelAccueil.setVisible(true);
		}else{
			labelAccueil.setVisible(false);
		}
		
		
		
		connexionLayout.updateLibelle(applicationContext.getMessage("accueilView.title.etu", new Object[]{applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale())}, UI.getCurrent().getLocale()),				
				applicationContext.getMessage("accueilView.title.nonetu", new Object[]{applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale())}, UI.getCurrent().getLocale()),
				applicationContext.getMessage("accueilView.connect.cas", null, UI.getCurrent().getLocale()),applicationContext.getMessage("accueilView.connect.ec", null, UI.getCurrent().getLocale()),
				applicationContext.getMessage("accueilView.connect.user", null, UI.getCurrent().getLocale()),applicationContext.getMessage("accueilView.connect.mdp", null,UI.getCurrent().getLocale()),
				applicationContext.getMessage("compteMinima.id.oublie.title", null, UI.getCurrent().getLocale()),applicationContext.getMessage("btnConnect", null, UI.getCurrent().getLocale()),
				applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()));
	}
}
