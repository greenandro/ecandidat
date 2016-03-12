package fr.univlorraine.ecandidat.views.windows;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.NomenclatureController;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision_;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxMail;

/**
 * Fenêtre d'édition de typeDecision
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
@SuppressWarnings("unchecked")
public class ScolTypeDecisionWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -3263611528152369691L;

	public static final String[] FIELDS_ORDER = {TypeDecision_.codTypDec.getName(), TypeDecision_.libTypDec.getName(),TypeDecision_.typeAvis.getName(),TypeDecision_.mail.getName()
		,TypeDecision_.tesTypDec.getName(),TypeDecision_.temDeverseOpiTypDec.getName(),TypeDecision_.temDefinitifTypDec.getName(),TypeDecision_.i18nLibTypDec.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient NomenclatureController nomenclatureController;

	/* Composants */
	private CustomBeanFieldGroup<TypeDecision> fieldGroup;
	private Button btnEnregistrer;
	private Button btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de typeDecision
	 * @param typeDecision la typeDecision à éditer
	 */
	public ScolTypeDecisionWindow(TypeDecision typeDecision) {
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
		setCaption(applicationContext.getMessage("typeDec.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(TypeDecision.class);
		fieldGroup.setItemDataSource(typeDecision);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			//LocalDateTime deb = LocalDateTime.now();
			String caption = applicationContext.getMessage("typeDec.table." + fieldName, null, UI.getCurrent().getLocale());
			/*if (fieldName.equals(TypeDecision_.i18nLibTypDec.getName()) &&
					(nomenclatureController.getLangueEnService().size()>0 
							|| 
					(typeDecision.getI18nLibTypDec()!=null && (typeDecision.getI18nLibTypDec().getI18nTraductions().size()>1)))){
				field = fieldGroup.buildAndBind(caption, fieldName, I18nField.class);
				((I18nField)field).addCenterListener(e-> {if(e){center();}});
			}else{
				field = fieldGroup.buildAndBind(caption, fieldName);
			}*/
			Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			
			field.setWidth(100, Unit.PERCENTAGE);			
			formLayout.addComponent(field);
		}
		
		if (typeDecision.getTemModelTypDec()){
			fieldGroup.getField(TypeDecision_.codTypDec.getName()).setEnabled(false);
			fieldGroup.getField(TypeDecision_.libTypDec.getName()).setEnabled(false);
			fieldGroup.getField(TypeDecision_.tesTypDec.getName()).setEnabled(false);
			fieldGroup.getField(TypeDecision_.typeAvis.getName()).setEnabled(false);
		}
		
		RequiredComboBox<TypeAvis> cbAvis = (RequiredComboBox<TypeAvis>)fieldGroup.getField(TypeDecision_.typeAvis.getName());		
		ComboBoxMail cbMail = (ComboBoxMail)fieldGroup.getField(TypeDecision_.mail.getName());
		if (cbAvis.getValue()!=null){
			cbMail.filterListValue((TypeAvis)cbAvis.getValue());
		}
		cbAvis.addValueChangeListener(e->{
			cbMail.filterListValue((TypeAvis)cbAvis.getValue());
		});

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
				/*Si le code de profil existe dejà --> erreur*/
				if (!typeDecisionController.isCodTypeDecUnique((String) fieldGroup.getField(TypeDecision_.codTypDec.getName()).getValue(), typeDecision.getIdTypDec())){
					Notification.show(applicationContext.getMessage("window.error.cod.nonuniq", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}				
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la typeDecision saisie */
				typeDecisionController.saveTypeDecision(typeDecision);
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
