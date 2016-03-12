package fr.univlorraine.ecandidat.views;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.FaqController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.views.windows.FaqWindow;


/**
 * Page d'assistance
 * @author Kevin Hergalant
 *
 */
@SpringView(name = AssistanceView.NAME)
public class AssistanceView extends VerticalLayout implements View {

	/** serialVersionUID **/
	private static final long serialVersionUID = 4359194703029079044L;

	public static final String NAME = "assistanceView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient Environment environment;
	@Resource
	private transient UserController userController;
	@Resource
	private transient FaqController faqController;

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		Boolean isPersonnel = userController.isPersonnel();
		/* Style */
		setMargin(true);
		setSpacing(true);

		/* Titre */
		Label title = new Label(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
		title.addStyleName(ValoTheme.LABEL_H2);
		addComponent(title);
		
		Boolean find = false;

		/* Accès à la faq */
		if (faqController.getFaq().size()>0){
			Button docFaq = new Button(applicationContext.getMessage(NAME + ".btnFaq", null, UI.getCurrent().getLocale()), FontAwesome.QUESTION_CIRCLE);
			docFaq.addClickListener(e->{
				UI.getCurrent().addWindow(new FaqWindow());
			});
			docFaq.addStyleName(ValoTheme.BUTTON_LINK);
			addComponent(docFaq);
			find = true;
		}		
		
		/* Accès à la documentation */
		String urlDoc = null;
		if (isPersonnel){
			urlDoc = environment.getRequiredProperty("assistance.documentation.url");
		}else{
			Boolean isEn = false;
			Locale locale = UI.getCurrent().getLocale();
			if (locale != null){
				String cod = locale.getLanguage();
				String urlDocEn = environment.getRequiredProperty("assistance.documentation.url.candidat.en");
				if (urlDocEn!=null && !urlDocEn.equals("") && cod!=null && cod.equals("en")){
					urlDoc = urlDocEn;
					addComponent(getButton(applicationContext.getMessage(NAME + ".btnDoc", null, UI.getCurrent().getLocale()), urlDoc ,FontAwesome.FILE_TEXT));
					isEn = true;
				}
				
			}			
			if (!isEn){
				urlDoc = environment.getRequiredProperty("assistance.documentation.url.candidat");				
			}
		}
		
		if (urlDoc!=null && !urlDoc.equals("")){
			addComponent(getButton(applicationContext.getMessage(NAME + ".btnDoc", null, UI.getCurrent().getLocale()), urlDoc ,FontAwesome.FILE_TEXT));
			find = true;
		}

		/* Envoyer un ticket */
		if (isPersonnel){
			String urlHelpDesk = environment.getRequiredProperty("assistance.helpdesk.url");
			if (urlHelpDesk!=null && !urlHelpDesk.equals("")){			
				addComponent(getButton(applicationContext.getMessage(NAME + ".btnHelpdesk", null, UI.getCurrent().getLocale()), urlHelpDesk ,FontAwesome.AMBULANCE));
				find = true;
			}
		}
		
		
		

		/* Envoyer un mail */
		String mailContact = environment.getRequiredProperty("assistance.contact.mail");
		if (mailContact!=null && !mailContact.equals("")){
			addComponent(getButton(applicationContext.getMessage(NAME + ".btnContact", new Object[] {environment.getRequiredProperty("assistance.contact.mail")}, UI.getCurrent().getLocale()), "mailto: " + mailContact, FontAwesome.ENVELOPE));
			find = true;
		}
		
		if (!find){
			addComponent(new Label(applicationContext.getMessage("assistanceView.noDoc", null, UI.getCurrent().getLocale()),ContentMode.HTML));
		}
	}

	/**
	 * @param caption
	 * @param bwo
	 * @param icon
	 * @return un bouton pour l'assistance
	 */
	private Button getButton(String caption, String bwo, com.vaadin.server.Resource icon){
		BrowserWindowOpener browser = new BrowserWindowOpener(bwo);
		Button btn = new Button(caption, icon);
		btn.addStyleName(ValoTheme.BUTTON_LINK);
		browser.extend(btn);
		return btn;
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
	}

}
