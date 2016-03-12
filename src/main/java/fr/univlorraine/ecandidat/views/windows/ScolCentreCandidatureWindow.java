package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;

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

import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.IndividuController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature_;
import fr.univlorraine.ecandidat.repositories.DroitProfilRepository;
import fr.univlorraine.ecandidat.vaadin.components.CustomTabSheet;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredCheckBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextArea;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxTypeDecision;

/**
 * Fenêtre d'édition de centreCandidature
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class ScolCentreCandidatureWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 1840053666365576939L;

	public static final String[] FIELDS_ORDER_1 = {CentreCandidature_.codCtrCand.getName(),CentreCandidature_.libCtrCand.getName(),CentreCandidature_.tesCtrCand.getName()};
	public static final String[] FIELDS_ORDER_2 = {
		CentreCandidature_.typeDecisionFav.getName(),
		CentreCandidature_.temListCompCtrCand.getName(),
		CentreCandidature_.typeDecisionFavListComp.getName(),
		CentreCandidature_.nbMaxVoeuxCtrCand.getName(),
		CentreCandidature_.temDematCtrCand.getName(),
		CentreCandidature_.infoCompCtrCand.getName()};
	public static final String[] FIELDS_ORDER_3 = {
		CentreCandidature_.datDebDepotCtrCand.getName(),
		CentreCandidature_.datFinDepotCtrCand.getName(),
		CentreCandidature_.datAnalyseCtrCand.getName(),
		CentreCandidature_.datRetourCtrCand.getName(),
		CentreCandidature_.datJuryCtrCand.getName(),
		CentreCandidature_.datPubliCtrCand.getName(),
		CentreCandidature_.datConfirmCtrCand.getName()};
	

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient DroitProfilRepository droitProfilRepository;
	
	
	/* Composants */
	private RecordCtrCandWindowListener recordCtrCandWindowListener;
	private CustomBeanFieldGroup<CentreCandidature> fieldGroup;
	private Button btnEnregistrer;
	private Button btnAnnuler;
	private CustomTabSheet sheet;

	/**
	 * Crée une fenêtre d'édition de centreCandidature
	 * @param centreCandidature la centreCandidature à éditer
	 */
	public ScolCentreCandidatureWindow(CentreCandidature centreCandidature, Boolean isAdmin) {
		/* Style */
		setModal(true);
		setWidth(700,Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		//layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("ctrCand.window", null, UI.getCurrent().getLocale()));
		
		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(CentreCandidature.class);
		fieldGroup.setItemDataSource(centreCandidature);
		
		/*Tabsheet*/
		sheet = new CustomTabSheet(fieldGroup, applicationContext.getMessage("validation.tabsheet", null, UI.getCurrent().getLocale()));
		sheet.setImmediate(true);
		sheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);	
		//sheet.addStyleName(StyleConstants.RESIZE_MAX_WIDTH);
		sheet.setSizeFull();
		sheet.addSelectedTabChangeListener(e->center());
		sheet.addGroupField(0, FIELDS_ORDER_1);
		sheet.addGroupField(1, FIELDS_ORDER_2);
		sheet.addGroupField(2, FIELDS_ORDER_3);
		layout.addComponent(sheet);
		
		/*Layout des param généraux*/
		FormLayout layoutParamGen = new FormLayout();
		layoutParamGen.setSizeFull();
		layoutParamGen.setSpacing(true);
		layoutParamGen.setMargin(true);		
		sheet.addTab(layoutParamGen, applicationContext.getMessage("ctrCand.window.sheet.gen", null, UI.getCurrent().getLocale()));
		
		/*Layout des param par défaut*/
		FormLayout layoutParamDefault = new FormLayout();
		layoutParamDefault.setSizeFull();
		layoutParamDefault.setSpacing(true);
		layoutParamDefault.setMargin(true);
		sheet.addTab(layoutParamDefault, applicationContext.getMessage("ctrCand.window.sheet.def", null, UI.getCurrent().getLocale()));		
		
		/*Layout des param par défaut*/
		FormLayout layoutParamDateDefault = new FormLayout();
		layoutParamDateDefault.setSizeFull();
		layoutParamDateDefault.setSpacing(true);
		layoutParamDateDefault.setMargin(true);
		sheet.addTab(layoutParamDateDefault, applicationContext.getMessage("ctrCand.window.sheet.datedef", null, UI.getCurrent().getLocale()));
		
		/* Formulaire */

		for (String fieldName : FIELDS_ORDER_1) {
			String caption = applicationContext.getMessage("ctrCand.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);			
			layoutParamGen.addComponent(field);
			if (!isAdmin){
				field.setEnabled(false);
			}
		}
		
		for (String fieldName : FIELDS_ORDER_2) {
			String caption = applicationContext.getMessage("ctrCand.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field;
			if (fieldName.equals(CentreCandidature_.infoCompCtrCand.getName())){
				field = fieldGroup.buildAndBind(caption, fieldName, RequiredTextArea.class);
			}else{
				field = fieldGroup.buildAndBind(caption, fieldName);
			}
			field.setWidth(100, Unit.PERCENTAGE);			
			layoutParamDefault.addComponent(field);
		}
		
		for (String fieldName : FIELDS_ORDER_3) {
			String caption = applicationContext.getMessage("ctrCand.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);			
			layoutParamDateDefault.addComponent(field);
		}
		
		ComboBoxTypeDecision cbTypeDecisionFavListComp = (ComboBoxTypeDecision)fieldGroup.getField(CentreCandidature_.typeDecisionFavListComp.getName());
		RequiredCheckBox checkBoxListComp = (RequiredCheckBox)fieldGroup.getField(CentreCandidature_.temListCompCtrCand.getName());
		checkBoxListComp.addValueChangeListener(e->{
			cbTypeDecisionFavListComp.setBoxNeeded(checkBoxListComp.getValue(),centreCandidature.getTypeDecisionFavListComp());
		});
		cbTypeDecisionFavListComp.setBoxNeeded(checkBoxListComp.getValue(),centreCandidature.getTypeDecisionFavListComp());
		

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
				/*Efface les erreurs des onglets*/
				sheet.effaceErrorSheet();				
				
				/*Si le code de profil existe dejà --> erreur*/
				if (!centreCandidatureController.isCodCtrCandUnique((String) fieldGroup.getField(CentreCandidature_.codCtrCand.getName()).getValue(), centreCandidature.getIdCtrCand())){
					Notification.show(applicationContext.getMessage("window.error.cod.nonuniq", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}
				
				fieldGroup.preCommit();
				if (!fieldGroup.isValid()){
					sheet.validateSheet();
					return;
				}
				
				/* Valide la saisie */
				fieldGroup.commit();
				
				CentreCandidature centreCandidatureSave = centreCandidatureController.saveCentreCandidature(centreCandidature);
				if (recordCtrCandWindowListener != null){
					recordCtrCandWindowListener.btnOkClick(centreCandidatureSave);
				}
				/* Ferme la fenêtre */
				close();	
			}			
			catch (CommitException ce) {
				sheet.getSheetOnError(ce.getInvalidFields());
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * Défini le 'RecordCtrCandWindowListener' utilisé
	 * @param recordCtrCandWindowListener
	 */
	public void addRecordCtrCandWindowListener(RecordCtrCandWindowListener recordCtrCandWindowListener) {
		this.recordCtrCandWindowListener = recordCtrCandWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui.
	 */
	public interface RecordCtrCandWindowListener extends Serializable {

		/** Appelé lorsque Oui est cliqué.
		 * @param saveCentreCandidature
		 */
		public void btnOkClick(CentreCandidature saveCentreCandidature);

	}
}
