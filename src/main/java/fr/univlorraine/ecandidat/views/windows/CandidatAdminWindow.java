package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;

/**
 * Fenêtre d'édition de compte a minima par un admin
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CandidatAdminWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -8599557648673448835L;

	public static final String[] FIELDS_ORDER = {CompteMinima_.nomCptMin.getName(),CompteMinima_.prenomCptMin.getName(),CompteMinima_.mailPersoCptMin.getName(),
		CompteMinima_.loginCptMin.getName(), CompteMinima_.supannEtuIdCptMin.getName(), CompteMinima_.temValidCptMin.getName(), CompteMinima_.temValidMailCptMin.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;

	/* Composants */
	private CustomBeanFieldGroup<CompteMinima> fieldGroup;
	private CandidatAdminWindowListener candidatAdminWindowListener;
	private Button btnEnregistrer;
	private Button btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de cptMin
	 * @param cptMin la cptMin à éditer
	 */
	public CandidatAdminWindow(CompteMinima cptMin) {
		/* Style */
		setModal(true);
		setWidth(550,Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("candidat.admin.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(CompteMinima.class);
		fieldGroup.setItemDataSource(cptMin);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			String caption = applicationContext.getMessage("compteMinima.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			if (fieldName.equals(CompteMinima_.mailPersoCptMin.getName())){
				field.addValidator(new EmailValidator(applicationContext.getMessage("validation.error.mail", null, Locale.getDefault())));
			}
			formLayout.addComponent(field);
		}
		
		RequiredTextField loginField = (RequiredTextField)fieldGroup.getField(CompteMinima_.loginCptMin.getName());
		RequiredTextField etuIdField = (RequiredTextField)fieldGroup.getField(CompteMinima_.supannEtuIdCptMin.getName());

		layout.addComponent(formLayout);

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new Button(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnEnregistrer = new Button(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);		
		btnEnregistrer.addClickListener(e -> {
			try {
				/*Si le code existe dejà --> erreur*/				
				if (candidatController.isLoginPresent(loginField.getValue(), cptMin) || candidatController.isSupannEtuIdPresent(etuIdField.getValue(), cptMin)){
					return;
				}
		
				/* Valide la saisie */
				fieldGroup.commit();
				
				/* Enregistre la cptMin saisie */
				candidatAdminWindowListener.btnOkClick(cptMin);
				
				/* Ferme la fenêtre */
				close();
			} catch (CommitException ce) {
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}
	
	/**
	 * Défini le 'CandidatAdminWindowListener' utilisé
	 * @param candidatAdminWindowListener
	 */
	public void addAdresseWindowListener(CandidatAdminWindowListener candidatAdminWindowListener) {
		this.candidatAdminWindowListener = candidatAdminWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface CandidatAdminWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param cptMin 
		 */
		public void btnOkClick(CompteMinima cptMin);

	}
}
