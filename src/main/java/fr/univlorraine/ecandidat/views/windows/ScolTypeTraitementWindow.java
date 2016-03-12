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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.NomenclatureTypeController;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement_;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nField;

/**
 * Fenêtre d'édition de typeTraitement
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class ScolTypeTraitementWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -7545182245578292532L;

	public static final String[] FIELDS_ORDER = {TypeTraitement_.codTypTrait.getName(),TypeTraitement_.libTypTrait.getName(),TypeTraitement_.i18nLibTypTrait.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient NomenclatureTypeController nomenclatureTypeController;

	/* Composants */
	private CustomBeanFieldGroup<TypeTraitement> fieldGroup;
	private Button btnEnregistrer;
	private Button btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de typeTraitement
	 * @param typeTraitement la typeTraitement à éditer
	 */
	public ScolTypeTraitementWindow(TypeTraitement typeTraitement) {
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
		setCaption(applicationContext.getMessage("typeTraitement.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(TypeTraitement.class);
		fieldGroup.setItemDataSource(typeTraitement);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			String caption = applicationContext.getMessage("typeTraitement.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);			
			formLayout.addComponent(field);
			if (fieldName.equals(TypeTraitement_.i18nLibTypTrait.getName())){
				field.setEnabled(true);
			}else{
				field.setEnabled(false);
			}
		}
		
		((I18nField)fieldGroup.getField(TypeTraitement_.i18nLibTypTrait.getName())).addCenterListener(e-> {if(e){center();}});

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
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la typeTraitement saisie */
				nomenclatureTypeController.saveTypeTraitement(typeTraitement);
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
