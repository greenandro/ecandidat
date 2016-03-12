package fr.univlorraine.ecandidat.views.windows;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.LangueController;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue_;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;

/**
 * Fenêtre d'édition de langue
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class AdminLangueWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 171149935897846930L;

	public static final String[] LANGUE_FIELDS_ORDER = {Langue_.codLangue.getName(), Langue_.libLangue.getName(), Langue_.tesLangue.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LangueController langueController;

	/* Composants */
	private CustomBeanFieldGroup<Langue> fieldGroup;
	private Button btnEnregistrer;
	private Button btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de langue
	 * @param langue la langue à éditer
	 */
	public AdminLangueWindow(Langue langue) {
		/* Style */
		setModal(true);
		setWidth(350,Unit.PIXELS);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("langue.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(Langue.class);
		fieldGroup.setItemDataSource(langue);
		FormLayout formLayout = new FormLayout();
		formLayout.setSpacing(true);
		formLayout.setSizeUndefined();
		for (String fieldName : LANGUE_FIELDS_ORDER) {
			formLayout.addComponent(fieldGroup.buildAndBind(applicationContext.getMessage("langue.table." + fieldName, null, UI.getCurrent().getLocale()), fieldName));
		}

		fieldGroup.getField(Langue_.codLangue.getName()).setReadOnly(true);
		fieldGroup.getField(Langue_.libLangue.getName()).setReadOnly(true);
		if (langue.getCodLangue().equals(NomenclatureUtils.LANGUE_FR)){
			fieldGroup.getField(Langue_.tesLangue.getName()).setEnabled(false);
		}

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
				/* Enregistre la langue saisie */
				langueController.saveLangue(langue);
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
