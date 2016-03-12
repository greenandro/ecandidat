package fr.univlorraine.ecandidat.views.windows;

import java.time.LocalDate;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxCommune;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxDepartement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxEtablissement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxPays;

/**
 * Fenêtre d'édition de parcours scolaire (bac, cursus, etc..), utilisé pour factoriser la saisie d'adresse 
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CandidatScolariteWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 1194254013030949385L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TableRefController tableRefController;
	
	/**
	 * Crée une fenêtre d'édition de parcours scolaire
	 */
	public CandidatScolariteWindow() {
		/* Style */
		setModal(true);
		setWidth(550,Unit.PIXELS);
		setResizable(true);
		setClosable(true);
	}
	
	/** initialise le formulaire
	 * @param comboBoxPays
	 * @param comboBoxDepartement
	 * @param comboBoxCommune
	 * @param comboBoxEtablissement
	 * @param fieldAnneeObt
	 * @param pays
	 * @param departement
	 * @param commune
	 * @param etablissement
	 */
	protected void initForm(ComboBoxPays comboBoxPays, ComboBoxDepartement comboBoxDepartement, ComboBoxCommune comboBoxCommune, ComboBoxEtablissement comboBoxEtablissement, RequiredIntegerField fieldAnneeObt,
			SiScolPays pays, SiScolDepartement departement, SiScolCommune commune, SiScolEtablissement etablissement){
		/*Champs d'année*/
		Integer anneeN1 = LocalDate.now().getYear()+1;
		String conversionError = applicationContext.getMessage("validation.parse.annee", null, UI.getCurrent().getLocale());
		fieldAnneeObt.setConversionError(conversionError);
		fieldAnneeObt.removeAllValidators();
		fieldAnneeObt.addValidator(value->{
			if (value==null){
				return;
			}
			Integer integerValue = null;
			try{
				integerValue = Integer.valueOf(value.toString());				
			}catch (Exception e){
				throw new InvalidValueException(conversionError);
			}
			if (value!=null && (integerValue<1900 || integerValue>anneeN1)){
				throw new InvalidValueException(conversionError);
			}
		});
		fieldAnneeObt.setMaxLength(4);
		
		/*ajout des listeners*/
		/*Champs pays*/
		comboBoxPays.addValueChangeListener(e-> {
			SiScolPays paysBox = (SiScolPays) e.getProperty().getValue();
			initPays(paysBox, comboBoxDepartement, comboBoxCommune, comboBoxEtablissement);
		});
		/*champs departement*/
		comboBoxDepartement.addValueChangeListener(e-> {
			SiScolDepartement departementBox = (SiScolDepartement) e.getProperty().getValue();
			initDepartement(departementBox, comboBoxCommune);
		});
		
		/*champs departement*/
		comboBoxCommune.addValueChangeListener(e-> {
			SiScolCommune communeBox = (SiScolCommune) e.getProperty().getValue();
			initCommune(communeBox, comboBoxEtablissement);
		});
				
		/*init pays*/
		if (pays == null){
			comboBoxPays.setValue(tableRefController.getPaysFrance());
		}else{
			comboBoxPays.setValue(pays);
			initPays(pays, comboBoxDepartement, comboBoxCommune, comboBoxEtablissement);
			/*init dept*/
			if (departement != null){
				comboBoxDepartement.setValue(departement);
				initDepartement(departement, comboBoxCommune);
				/*init commune*/
				if (commune != null){
					comboBoxCommune.setValue(commune);
					initCommune(commune, comboBoxEtablissement);
					/*init etablissement*/
					if (etablissement != null){
						comboBoxEtablissement.setValue(etablissement);
					}
				}
			}
		}
	}

	/**Initialise les champs lors du changement de pays
	 * @param pays
	 * @param comboBoxDepartement
	 * @param comboBoxCommune
	 * @param comboBoxEtablissement
	 */
	private void initPays(SiScolPays pays, ComboBoxDepartement comboBoxDepartement, ComboBoxCommune comboBoxCommune, ComboBoxEtablissement comboBoxEtablissement){
		if (pays!=null && pays.getCodPay().equals(ConstanteUtils.PAYS_CODE_FRANCE)){
			changeRequired(comboBoxDepartement,true);
			comboBoxDepartement.setVisible(true);
			comboBoxDepartement.setEnabled(true);

			changeRequired(comboBoxCommune,true);
			comboBoxCommune.setVisible(true);
			comboBoxCommune.setEnabled(false);
			
			changeRequired(comboBoxEtablissement,true);
			comboBoxEtablissement.setVisible(true);
			comboBoxEtablissement.setEnabled(false);
		}else{
			changeRequired(comboBoxDepartement,false);
			comboBoxDepartement.setVisible(false);
			comboBoxDepartement.setValue(null);

			changeRequired(comboBoxCommune,false);
			comboBoxCommune.setVisible(false);
			comboBoxCommune.setValue(null);
			
			changeRequired(comboBoxEtablissement,false);
			comboBoxEtablissement.setVisible(false);
			comboBoxCommune.setValue(null);
		}			
	}
	
	/**Initialise les champs lors du changement de departement
	 * @param siScolDepartement
	 * @param communeField
	 */
	private void initDepartement(SiScolDepartement siScolDepartement, ComboBoxCommune communeField){
		communeField.setValue(null);
		communeField.setListCommune(null);
		if (siScolDepartement == null){
			communeField.setEnabled(false);
			return;
		}
		
		List<SiScolCommune> listeCommune = tableRefController.listeCommuneByDepartement(siScolDepartement);
		if (listeCommune.size()>0){
			communeField.setListCommune(listeCommune);
			communeField.setEnabled(true);
			if (listeCommune.size()==1){
				communeField.setValue(listeCommune.get(0));
			}
		}else{
			communeField.setEnabled(false);
		}
	}
	
	/**Initialise les champs lors du changement de commune
	 * @param commune
	 * @param comboBoxEtablissement
	 */
	private void initCommune(SiScolCommune commune,	ComboBoxEtablissement comboBoxEtablissement) {
		comboBoxEtablissement.setValue(null);
		comboBoxEtablissement.setListEtablissement(null);
		if (commune == null){
			comboBoxEtablissement.setEnabled(false);
			return;
		}
		
		List<SiScolEtablissement> listeEtab = tableRefController.listeEtablissementByCommune(commune);
		if (listeEtab.size()>0){
			comboBoxEtablissement.setListEtablissement(listeEtab);
			comboBoxEtablissement.setEnabled(true);
			if (listeEtab.size()==1){
				comboBoxEtablissement.setValue(listeEtab.get(0));
			}
		}else{
			comboBoxEtablissement.setEnabled(false);
		}
	}
	
	/** Change l'etat obligatoire d'un champs
	 * @param field
	 * @param isRequired
	 */
	private void changeRequired(Field<?> field, Boolean isRequired){
		field.setRequired(isRequired);
		if (isRequired){
			field.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
		}else{
			field.setRequiredError(null);
		}
	}
}