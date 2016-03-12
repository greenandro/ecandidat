package fr.univlorraine.ecandidat.views.windows;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CampagneController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.LocalDateTimeField;
import fr.univlorraine.ecandidat.vaadin.form.SearchAnneeUnivApoField;

/** 
 * Fenêtre d'édition de campagne
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class AdminCampagneWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 1789664007659398677L;

	public static final String[] FIELDS_ORDER = {Campagne_.codCamp.getName(),Campagne_.libCamp.getName(),Campagne_.datDebCamp.getName(),Campagne_.datFinCamp.getName(),Campagne_.datActivatPrevCamp.getName()};	

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient ParametreController parametreController;

	/* Composants */
	private CustomBeanFieldGroup<Campagne> fieldGroup;
	private Button btnEnregistrer;
	private Button btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de campagne
	 * @param campagne la campagne à éditer
	 * @param campagneAArchiver 
	 */
	public AdminCampagneWindow(Campagne campagne, Campagne campagneAArchiver) {
		String[] fieldsOrderToUse = FIELDS_ORDER;
		if (campagneAArchiver==null){	
			fieldsOrderToUse = (String[])ArrayUtils.removeElement(fieldsOrderToUse, Campagne_.datActivatPrevCamp.getName());
		}
		/* Style */
		setModal(true);
		setWidth(650,Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("campagne.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(Campagne.class);
		fieldGroup.setItemDataSource(campagne);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		
		
		for (String fieldName : fieldsOrderToUse) {
			String caption = applicationContext.getMessage("campagne.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field;			
			if (fieldName.equals(Campagne_.codCamp.getName()) && parametreController.getSIScolMode().equals(ConstanteUtils.SI_SCOL_APOGEE)){
				field = fieldGroup.buildAndBind(caption, fieldName,SearchAnneeUnivApoField.class);
			}else{
				field = fieldGroup.buildAndBind(caption, fieldName);
			}
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);
		}
		
		layout.addComponent(formLayout);
		
		if (campagneAArchiver!=null){
			LocalDateTimeField archivageField = (LocalDateTimeField) fieldGroup.getField(Campagne_.datActivatPrevCamp.getName());
			archivageField.setRequired(true);
			archivageField.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
			layout.addComponent(new Label("Campagne à archiver : "+campagneAArchiver.getLibCamp()));
		}		

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
				/*Si le code de profil existe dejà --> erreur*/
				if (!campagneController.isCodCampUnique((String) fieldGroup.getField(Campagne_.codCamp.getName()).getValue(), campagne.getIdCamp())){
					Notification.show(applicationContext.getMessage("window.error.cod.nonuniq", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}
				
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la campagne saisie */
				campagneController.saveCampagne(campagne, campagneAArchiver);
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
}
