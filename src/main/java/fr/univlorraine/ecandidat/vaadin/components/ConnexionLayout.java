package fr.univlorraine.ecandidat.vaadin.components;

import java.io.Serializable;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;

/**
 * Layout de connexion pour un anonymous
 * @author Kevin Hergalant
 *
 */
public class ConnexionLayout extends VerticalLayout {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3178844375518329434L;
	
	/** Listeners */
	private CasListener casListener;
	private StudentListener studentListener;
	private ForgotPasswordListener forgotPasswordListener;
	private CreateCompteListener createCompteListener;
	
	/*Composants*/
	private Button logBtn = new Button(FontAwesome.SIGN_OUT);
	private Button logBtnEc = new Button(FontAwesome.SIGN_OUT);
	private Button passBtn = new Button(FontAwesome.KEY);
	private Button createBtn = new Button(FontAwesome.MAGIC);
	
	public void addCasListener(CasListener casListener){
		this.casListener = casListener;
		logBtn.setVisible(true);
	}
	
	public void addStudentListener(StudentListener studentListener){
		this.studentListener = studentListener;
		logBtnEc.setVisible(true);
	}
	
	public void addForgotPasswordListener(ForgotPasswordListener forgotPasswordListener){
		this.forgotPasswordListener = forgotPasswordListener;
		passBtn.setVisible(true);
	}
	
	public void addCreateCompteListener(CreateCompteListener createCompteListener){
		this.createCompteListener = createCompteListener;
		createBtn.setVisible(true);
	}
	
	public ConnexionLayout(){
		init();
	}
	
	public void updateLibelle(String titleEtu, String titleNonEtu, String connectCas, String connectEc, 
			String connectUser, String connectMdp, String identifiantOublie, String btnConnect, String createAccount){
		panelStudent.setCaption(titleEtu);
		panelNotStudent.setCaption(titleNonEtu);
		labelConnect.setValue(connectCas);
		logBtn.setCaption(btnConnect);
		createBtn.setCaption(createAccount);
		passBtn.setCaption(identifiantOublie);
		logBtnEc.setCaption(btnConnect);
		password.setCaption(connectMdp);
		password.setInputPrompt(connectMdp);
		labelEc.setValue(connectEc);
		user.setCaption(connectUser);
		user.setInputPrompt(connectUser);
	}
	
	private Panel panelStudent = new Panel();
	private Panel panelNotStudent = new Panel();
	private Label labelConnect = new Label();
	private PasswordField password = new PasswordField();
	private TextField user = new TextField();
	private Label labelEc = new Label("",ContentMode.HTML);
	
	public void init(){
		setSpacing(true);
		
		VerticalLayout vlStudent = new VerticalLayout();
		vlStudent.setSpacing(true);
		vlStudent.setMargin(true);
		VerticalLayout vlNotStudent = new VerticalLayout();
		vlNotStudent.setSpacing(true);
		vlNotStudent.setMargin(true);
		panelStudent.setContent(vlStudent);
		panelStudent.addStyleName(StyleConstants.PANEL_COLORED);
		panelNotStudent.setContent(vlNotStudent);
		panelNotStudent.addStyleName(StyleConstants.PANEL_COLORED);
		this.addComponent(panelStudent);
		this.addComponent(panelNotStudent);
		
		HorizontalLayout hlConnect = new HorizontalLayout();
		hlConnect.setSpacing(true);
		hlConnect.addComponent(labelConnect);
		hlConnect.setComponentAlignment(labelConnect, Alignment.MIDDLE_LEFT);
		
		/*Connexion CAS*/
		logBtn.setVisible(false);
		hlConnect.addComponent(logBtn);
		hlConnect.setComponentAlignment(logBtn, Alignment.MIDDLE_CENTER);
		logBtn.addClickListener(e -> {
			if (casListener!=null){
				casListener.connectCAS();
			}			
		});
		
		vlStudent.addComponent(hlConnect);
		
		/*Connexion eCandidat*/
		vlNotStudent.addComponent(labelEc);
		user.setWidth(200, Unit.PIXELS);
		user.setRequired(true);		
		user.setValue("");
		//user.setValue("1QJ5A59F");
		
		// Create the password input field
		password.setWidth(200, Unit.PIXELS);
		password.setRequired(true);
		password.setValue("");
		password.setNullRepresentation("");
		
		//password.setValue("123456");
		
		vlNotStudent.addComponent(user);
		vlNotStudent.addComponent(password);
		

		logBtnEc.setVisible(false);
	    logBtnEc.addClickListener(e -> {
	    	if (studentListener!=null){
	    		studentListener.connectStudent(user.getValue(), password.getValue());
	    	}		    	
		});
	    vlNotStudent.addComponent(logBtnEc);

		passBtn.setVisible(false);
	    passBtn.addStyleName(ValoTheme.BUTTON_LINK);
	    passBtn.addStyleName(ValoTheme.BUTTON_SMALL);
	    vlNotStudent.addComponent(passBtn);
	    passBtn.addClickListener(e -> {
	    	if (forgotPasswordListener!=null){
	    		forgotPasswordListener.forgot();
	    	}
		});
	
		createBtn.setVisible(false);
		createBtn.addStyleName(ValoTheme.BUTTON_LINK);
		createBtn.addStyleName(ValoTheme.BUTTON_SMALL);
	    vlNotStudent.addComponent(createBtn);
	    createBtn.addClickListener(e -> {
	    	if (createCompteListener!=null){
	    		createCompteListener.createCompte();
	    	}		    	
		});
	}
	
	/**
	 * @param login
	 */
	public void setLogin(String login){
		if (login !=null && !login.equals("")){
			user.setValue(login);
			password.setValue("123");
		}else{
			user.setValue("");
			password.setValue("");
		}		
	}
	
	
	

	/** AJoute ou enleve le shortcut
	 * @param hasShortcut
	 */
	public void setClickShortcut(boolean hasShortcut) {
		if (hasShortcut){
			logBtnEc.setClickShortcut(KeyCode.ENTER);
		}else{
			logBtnEc.removeClickShortcut();
		}
	}



	/**
	 * Interface pour les listeners du bouton cas.
	 */
	public interface CasListener extends Serializable {

		/**
		 * Appelé lorsque cas est cliqué.
		 */
		public void connectCAS();

	}
	
	/**
	 * Interface pour les listeners du bouton de connexion.
	 */
	public interface StudentListener extends Serializable {

		/**
		 * Appelé lorsque le bouton de connexion est cliqué.
		 */
		public void connectStudent(String user, String pwd);

	}
	
	/**
	 * Interface pour les listeners du bouton d'oublie.
	 */
	public interface ForgotPasswordListener extends Serializable {

		/**
		 * Appelé lorsque cas est cliqué.
		 */
		public void forgot();

	}
	
	/**
	 * Interface pour les listeners du bouton de creation.
	 */
	public interface CreateCompteListener extends Serializable {

		/**
		 * Appelé lorsque cas est cliqué.
		 */
		public void createCompte();

	}
}
