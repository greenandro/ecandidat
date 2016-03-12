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
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CandidatParcoursController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxBacOuEqu;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxCommune;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxDepartement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxEtablissement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxPays;

/**
 * Fenêtre d'édition du bac
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CandidatBacWindow extends CandidatScolariteWindow {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 8279285838139858898L;
	
	public static final String[] FIELDS_ORDER = {
		CandidatBacOuEqu_.anneeObtBac.getName(),
		CandidatBacOuEqu_.siScolBacOuxEqu.getName(),				
		CandidatBacOuEqu_.siScolMentionNivBac.getName(),
		CandidatBacOuEqu_.siScolPays.getName(),
		CandidatBacOuEqu_.siScolDepartement.getName(),
		CandidatBacOuEqu_.siScolCommune.getName(),		
		CandidatBacOuEqu_.siScolEtablissement.getName()
	};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;
	@Resource
	private transient TableRefController tableRefController;

	/* Composants */
	private CustomBeanFieldGroup<CandidatBacOuEqu> fieldGroup;
	private Button btnEnregistrer;
	private Button btnAnnuler;
	private ComboBoxBacOuEqu comboBoxBacOuEqu;
	private RequiredIntegerField fieldAnneeObt;
	private BacWindowListener bacWindowListener;
	/**
	 * Crée une fenêtre d'édition de bac
	 * @param bacOuEqu le bac à éditer
	 */
	public CandidatBacWindow(CandidatBacOuEqu bacOuEqu, Boolean isEdition) {
		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("infobac.window", null, UI.getCurrent().getLocale()));
		
		
		/*Label explicatif*/
		Label labelExplicatif = new Label(applicationContext.getMessage("infobac.explication.annee", null, UI.getCurrent().getLocale()));
		labelExplicatif.setSizeUndefined();
		labelExplicatif.addStyleName(ValoTheme.LABEL_TINY);
		labelExplicatif.addStyleName(StyleConstants.LABEL_MORE_BOLD);
		labelExplicatif.addStyleName(StyleConstants.LABEL_ITALIC);
		layout.addComponent(labelExplicatif);
		layout.setComponentAlignment(labelExplicatif, Alignment.BOTTOM_CENTER);

		
		/*Layout adresse*/		
		fieldGroup = new CustomBeanFieldGroup<CandidatBacOuEqu>(CandidatBacOuEqu.class,ConstanteUtils.TYP_FORM_CANDIDAT);
		fieldGroup.setItemDataSource(bacOuEqu);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			Field<?> field = fieldGroup.buildAndBind(applicationContext.getMessage("infobac." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);
		}
		
		
		
		layout.addComponent(formLayout);
		layout.setExpandRatio(formLayout, 1);
		
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
				/* Valide la saisie de l'adresse*/
				fieldGroup.commit();
				/* Enregistre la formation saisie */
				bacWindowListener.btnOkClick(candidatParcoursController.saveBac(bacOuEqu));
				/* Ferme la fenêtre */
				close();
			} catch (CommitException ce) {			
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
		
		/*Champs pays*/
		ComboBoxPays comboBoxPays = (ComboBoxPays)fieldGroup.getField(CandidatBacOuEqu_.siScolPays.getName());
		/*Champs departement*/
		ComboBoxDepartement comboBoxDepartement = (ComboBoxDepartement)fieldGroup.getField(CandidatBacOuEqu_.siScolDepartement.getName());
		/*Champs commune*/
		ComboBoxCommune comboBoxCommune = (ComboBoxCommune)fieldGroup.getField(CandidatBacOuEqu_.siScolCommune.getName());
		/*Champs etablissement*/
		ComboBoxEtablissement comboBoxEtablissement = (ComboBoxEtablissement)fieldGroup.getField(CandidatBacOuEqu_.siScolEtablissement.getName());
		/*Champs bac*/
		comboBoxBacOuEqu = (ComboBoxBacOuEqu)fieldGroup.getField(CandidatBacOuEqu_.siScolBacOuxEqu.getName());
		/*Champs annee d'obtention*/
		fieldAnneeObt = (RequiredIntegerField)fieldGroup.getField(CandidatBacOuEqu_.anneeObtBac.getName());
		fieldAnneeObt.setRequired(true);
		fieldAnneeObt.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
		
		initForm(comboBoxPays, comboBoxDepartement, comboBoxCommune, comboBoxEtablissement, fieldAnneeObt, 
				bacOuEqu.getSiScolPays(), bacOuEqu.getSiScolDepartement(), bacOuEqu.getSiScolCommune(), bacOuEqu.getSiScolEtablissement());
		
		fieldAnneeObt.addValueChangeListener(e->{
			filterListSeries();
		});
		
		if (isEdition){
			filterListSeries();
			comboBoxBacOuEqu.setValue(bacOuEqu.getSiScolBacOuxEqu());			
		}
	}
	
	/**
	 * Filtre la liste des series
	 */
	private void filterListSeries(){
		if (fieldAnneeObt.isValid()){
			try{
				comboBoxBacOuEqu.filterListValue(Integer.valueOf(fieldAnneeObt.getValue()));
			}catch(Exception ex){
				
			}	
		}else{
			comboBoxBacOuEqu.filterListValue(null);
		}
	}
	
	/**
	 * Défini le 'BacWindowListener' utilisé
	 * @param bacWindowListener
	 */
	public void addBacWindowListener(BacWindowListener bacWindowListener) {
		this.bacWindowListener = bacWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface BacWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param candidatBacOuEqu 
		 */
		public void btnOkClick(CandidatBacOuEqu candidatBacOuEqu);

	}
}
