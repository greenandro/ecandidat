package fr.univlorraine.ecandidat.views.windows;

import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CommissionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextArea;
import fr.univlorraine.ecandidat.vaadin.form.siscol.AdresseForm;

/**
 * Fenêtre d'édition de commission
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CtrCandCommissionWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 8279285838139858898L;

	public static final String[] FIELDS_ORDER = {Commission_.codComm.getName(),Commission_.libComm.getName(),Commission_.tesComm.getName(),
		Commission_.mailComm.getName(),Commission_.telComm.getName(),Commission_.faxComm.getName(),Commission_.commentRetourComm.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CommissionController commissionController;

	/* Composants */
	private TabSheet sheet;
	private CustomBeanFieldGroup<Commission> fieldGroup;
	private CustomBeanFieldGroup<Adresse> fieldGroupAdresse;
	private Button btnEnregistrer;
	private Button btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de commission
	 * @param commission la commission à éditer
	 */
	public CtrCandCommissionWindow(Commission commission) {
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
		setCaption(applicationContext.getMessage("commission.window", null, UI.getCurrent().getLocale()));

		/*Tabsheet*/
		sheet = new TabSheet();		
		sheet.setImmediate(true);
		sheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		//sheet.addStyleName(StyleConstants.RESIZE_MAX_WIDTH);
		sheet.setSizeFull();
		sheet.addSelectedTabChangeListener(e->center());
		layout.addComponent(sheet);
		
		/*Layout des param généraux*/
		FormLayout layoutParamGen = new FormLayout();
		layoutParamGen.setSizeFull();
		layoutParamGen.setSpacing(true);
		layoutParamGen.setMargin(true);		
		sheet.addTab(layoutParamGen, applicationContext.getMessage("commission.window.sheet.gen", null, UI.getCurrent().getLocale()));
		
		/*Layout adresse*/
		VerticalLayout vlAdresse = new VerticalLayout();
		vlAdresse.setSpacing(true);
		vlAdresse.setMargin(true);
		Button btnImport = new Button(applicationContext.getMessage("commission.window.import.adr", null, UI.getCurrent().getLocale()));
		btnImport.addClickListener(e->{
			SearchCommissionWindow scw = new SearchCommissionWindow(commission.getCentreCandidature());
			scw.addCommissionListener(comm->fieldGroupAdresse.setItemDataSource(comm.getAdresse()));
			UI.getCurrent().addWindow(scw);
		});
		vlAdresse.addComponent(btnImport);
		vlAdresse.setComponentAlignment(btnImport, Alignment.MIDDLE_CENTER);
		
		
		fieldGroupAdresse = new CustomBeanFieldGroup<Adresse>(Adresse.class,ConstanteUtils.TYP_FORM_ADR);
		fieldGroupAdresse.setItemDataSource(commission.getAdresse());
		AdresseForm adresseForm = new AdresseForm(fieldGroupAdresse, true);
		vlAdresse.addComponent(adresseForm);
		vlAdresse.setExpandRatio(adresseForm, 1);
		sheet.addTab(vlAdresse, applicationContext.getMessage("commission.window.sheet.adr", null, UI.getCurrent().getLocale()));
		
		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(Commission.class);
		fieldGroup.setItemDataSource(commission);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			String caption = applicationContext.getMessage("commission.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field;
			if (fieldName.equals(Commission_.commentRetourComm.getName())){
				field = fieldGroup.buildAndBind(caption, fieldName, RequiredTextArea.class);
			}else{
				field = fieldGroup.buildAndBind(caption, fieldName);
			}
			
			
			
			field.setWidth(100, Unit.PERCENTAGE);
			if (fieldName.equals(Commission_.mailComm.getName())){
				field.addValidator(new EmailValidator(applicationContext.getMessage("validation.error.mail", null, Locale.getDefault())));
			}			
			if (fieldName.equals(Commission_.telComm.getName()) || fieldName.equals(Commission_.faxComm.getName())){
				field.addValidator(new RegexpValidator(ConstanteUtils.regExNoTel,applicationContext.getMessage("validation.error.tel", null, Locale.getDefault())));
			}
			layoutParamGen.addComponent(field);
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
			/*Efface les erreurs des onglets*/
			effaceErrorSheet();
			/*Si le code existe dejà --> erreur*/
			if (!commissionController.isCodCommUnique((String) fieldGroup.getField(Commission_.codComm.getName()).getValue(), commission.getIdComm())){
				Notification.show(applicationContext.getMessage("window.error.cod.nonuniq", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}			
			
			fieldGroupAdresse.preCommit();
			fieldGroup.preCommit();
			
			if (!fieldGroup.isValid()){
				displayErrorSheet(true,0);
			}
			
			if (!fieldGroupAdresse.isValid()){
				displayErrorSheet(true,1);
			}
			
			try {
				/* Valide la saisie de l'adresse*/
				fieldGroupAdresse.commit();
			} catch (CommitException ce) {
				displayErrorSheet(true,1);
				return;
			}
			try {			
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la commission saisie */
				commissionController.saveCommission(commission,fieldGroupAdresse.getItemDataSource().getBean());
				/* Ferme la fenêtre */
				close();
			} catch (CommitException ce) {
				//adresseForm.discard();
				displayErrorSheet(true,0);
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}
	
	/**
	 * Supprime les eventuelles erreur des onglets
	 */
	private void effaceErrorSheet(){
		displayErrorSheet(false,0);
		displayErrorSheet(false,1);	
	}
	
	/**Affiche les erreur d'un sheet avec un point exclam en logo
	 * @param findError
	 * @param tabOrder
	 */
	private void displayErrorSheet(Boolean findError, Integer tabOrder){
		if (findError){
			sheet.getTab(tabOrder).setIcon(FontAwesome.EXCLAMATION);
		}else{
			sheet.getTab(tabOrder).setIcon(null);
		}
	}
}
