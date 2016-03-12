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

import fr.univlorraine.ecandidat.controllers.CandidatParcoursController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxPresentation;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxCommune;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxDepartement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxEtablissement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxPays;

/**
 * Fenêtre d'édition de cursus externe
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CandidatCursusExterneWindow extends CandidatScolariteWindow {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 8279285838139858898L;
	
	public static final String[] FIELDS_ORDER = {
		CandidatCursusPostBac_.siScolPays.getName(),
		CandidatCursusPostBac_.siScolDepartement.getName(),
		CandidatCursusPostBac_.siScolCommune.getName(),
		CandidatCursusPostBac_.siScolEtablissement.getName(),
		CandidatCursusPostBac_.anneeUnivCursus.getName(),
		CandidatCursusPostBac_.siScolDipAutCur.getName(),
		CandidatCursusPostBac_.libCursus.getName(),
		CandidatCursusPostBac_.obtenuCursus.getName(),
		CandidatCursusPostBac_.siScolMention.getName()
	};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;
	@Resource
	private transient TableRefController tableRefController;

	/* Composants */
	private CustomBeanFieldGroup<CandidatCursusPostBac> fieldGroup;
	private Button btnEnregistrer;
	private Button btnAnnuler;
	private CursusPostBacWindowListener cursusPostBacWindowListener;
	/**
	 * Crée une fenêtre d'édition de cursus externe
	 * @param cursus à éditer
	 */
	public CandidatCursusExterneWindow(CandidatCursusPostBac cursus, Boolean nouveau) {
		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		if (nouveau){
			setCaption(applicationContext.getMessage("cursusexterne.window.new", null, UI.getCurrent().getLocale()));
		}else{
			setCaption(applicationContext.getMessage("cursusexterne.window.update", null, UI.getCurrent().getLocale()));
			//cursus.setObtenu(new SimpleBeanPresentation(cursus.getObtenuCursus()));
		}
		layout.addComponent(new Label(applicationContext.getMessage("cursusexterne.window.label", null, UI.getCurrent().getLocale())));
		
		
		/*Layout adresse*/		
		fieldGroup = new CustomBeanFieldGroup<CandidatCursusPostBac>(CandidatCursusPostBac.class,ConstanteUtils.TYP_FORM_CANDIDAT);
		fieldGroup.setItemDataSource(cursus);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			Field<?> field;
			if (fieldName.equals(CandidatCursusPostBac_.obtenuCursus.getName())){
				field = fieldGroup.buildAndBind(applicationContext.getMessage("cursusexterne." + fieldName, null, UI.getCurrent().getLocale()), fieldName,ComboBoxPresentation.class);
			}else{
				field = fieldGroup.buildAndBind(applicationContext.getMessage("cursusexterne." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
			}
			
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);
		}
		
		/*Initialisation des valeurs*/		
		ComboBoxPresentation cbPres = (ComboBoxPresentation) fieldGroup.getField(CandidatCursusPostBac_.obtenuCursus.getName());
		cbPres.setListe(tableRefController.getListeObtenuCursus());
		
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
				cursusPostBacWindowListener.btnOkClick(candidatParcoursController.saveCursusPostBac(cursus));
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
		ComboBoxPays comboBoxPays = (ComboBoxPays)fieldGroup.getField(CandidatCursusPostBac_.siScolPays.getName());
		/*Champs departement*/
		ComboBoxDepartement comboBoxDepartement = (ComboBoxDepartement)fieldGroup.getField(CandidatCursusPostBac_.siScolDepartement.getName());
		/*Champs commune*/
		ComboBoxCommune comboBoxCommune = (ComboBoxCommune)fieldGroup.getField(CandidatCursusPostBac_.siScolCommune.getName());
		/*Champs etablissement*/
		ComboBoxEtablissement comboBoxEtablissement = (ComboBoxEtablissement)fieldGroup.getField(CandidatCursusPostBac_.siScolEtablissement.getName());
		/*Champs annee d'obtention*/
		RequiredIntegerField fieldAnneeObt = (RequiredIntegerField)fieldGroup.getField(CandidatCursusPostBac_.anneeUnivCursus.getName());
		
		initForm(comboBoxPays, comboBoxDepartement, comboBoxCommune, comboBoxEtablissement, fieldAnneeObt, 
				cursus.getSiScolPays(), cursus.getSiScolDepartement(), cursus.getSiScolCommune(), cursus.getSiScolEtablissement());
	}
	
	/**
	 * Défini le 'CursusPostBacWindowListener' utilisé
	 * @param cursusPostBacWindowListener
	 */
	public void addCursusPostBacWindowListener(CursusPostBacWindowListener cursusPostBacWindowListener) {
		this.cursusPostBacWindowListener = cursusPostBacWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface CursusPostBacWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param candidatCursusPostBac 
		 */
		public void btnOkClick(CandidatCursusPostBac candidatCursusPostBac);

	}
}
