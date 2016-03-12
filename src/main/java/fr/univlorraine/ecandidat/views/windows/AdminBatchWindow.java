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

import fr.univlorraine.ecandidat.controllers.BatchController;
import fr.univlorraine.ecandidat.entities.ecandidat.Batch;
import fr.univlorraine.ecandidat.entities.ecandidat.Batch_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxJourMoisAnnee;

/** 
 * Fenêtre d'édition de batch
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class AdminBatchWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -8212886557264076581L;

	public static final String[] BATCH_FIELDS_ORDER = {Batch_.codBatch.getName(),Batch_.tesBatch.getName(), Batch_.fixeHourBatch.getName(), Batch_.fixeDayBatch.getName(), Batch_.fixeMonthBatch.getName(), Batch_.fixeYearBatch.getName() ,
		Batch_.temLundiBatch.getName(),Batch_.temMardiBatch.getName(),Batch_.temMercrBatch.getName(),Batch_.temJeudiBatch.getName(),Batch_.temVendrediBatch.getName(), Batch_.temSamediBatch.getName(),Batch_.temDimanBatch.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient BatchController batchController;

	/* Composants */
	private CustomBeanFieldGroup<Batch> fieldGroup;
	private Button btnEnregistrer;
	private Button btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de batch
	 * @param batch le batch à éditer
	 */
	public AdminBatchWindow(Batch batch) {
		/* Style */
		setModal(true);
		setWidth(500,Unit.PIXELS);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("batch.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(Batch.class);		
		fieldGroup.setItemDataSource(batch);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : BATCH_FIELDS_ORDER) {
			String caption = applicationContext.getMessage("batch.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field;
			if (fieldName.equals(Batch_.fixeMonthBatch.getName())){
				field = fieldGroup.buildAndBind(caption, fieldName,ComboBoxJourMoisAnnee.class);
				((ComboBoxJourMoisAnnee)field).changeTypeNativeSelect(ConstanteUtils.TYPE_MOIS);
			}else if (fieldName.equals(Batch_.fixeDayBatch.getName())){
				field = fieldGroup.buildAndBind(caption, fieldName,ComboBoxJourMoisAnnee.class);
				((ComboBoxJourMoisAnnee)field).changeTypeNativeSelect(ConstanteUtils.TYPE_JOUR);
			}else if (fieldName.equals(Batch_.fixeYearBatch.getName())){
				field = fieldGroup.buildAndBind(caption, fieldName,ComboBoxJourMoisAnnee.class);
				((ComboBoxJourMoisAnnee)field).changeTypeNativeSelect(ConstanteUtils.TYPE_ANNEE);
			}			
			else{
				field = fieldGroup.buildAndBind(caption, fieldName);
			}
			
			if (!fieldName.equals(Batch_.fixeHourBatch.getName())){
				field.setWidth(100, Unit.PERCENTAGE);
			}else{
				field.setSizeUndefined();
			}
			formLayout.addComponent(field);
		}

		fieldGroup.getField(Batch_.codBatch.getName()).setReadOnly(true);

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
				/* Enregistre le batch saisi */
				batchController.saveBatch(batch);
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
