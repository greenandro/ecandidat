package fr.univlorraine.ecandidat.vaadin.components;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.UserError;
import com.vaadin.ui.Field;
import com.vaadin.ui.TabSheet;

import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.IRequiredField;

/**
 * Classe de TabSheet customisée
 * @author Kevin Hergalant
 *
 */
public class CustomTabSheet extends TabSheet{

	/*** serialVersionUID */
	private static final long serialVersionUID = -1182761874718809403L;
	
	private Map<Integer,String[]> mapFieldOrder;
	private CustomBeanFieldGroup<?> fieldGroup;
	private String errorMessage;
	
	public CustomTabSheet(CustomBeanFieldGroup<?> fieldGroup, String errorMessage){
		super();
		this.fieldGroup = fieldGroup;
		mapFieldOrder = new HashMap<Integer, String[]>();
		this.errorMessage = errorMessage;
	}
	
	public void addGroupField(Integer key,String[] value){
		mapFieldOrder.put(key, value);
	}
	
	
	/**
	 * Supprime les eventuelles erreur des onglets
	 */
	public void effaceErrorSheet(){
		mapFieldOrder.forEach((k,v)->{
			displayErrorSheet(false,k);
		});
	}
	
	/** Affiche les erreurs pour tout le tableau 
	 * @param fieldError
	 */
	public void getSheetOnError(Map<Field<?>, InvalidValueException> fieldError){
		if (fieldError == null){
			return;
		}
		mapFieldOrder.forEach((k,v)->{
			sheetHasError(fieldError,v,k);
		});
	}
	
	/**
	 * @return la liste des champs en erreur
	 */
	public Map<Field<?>, InvalidValueException> getFieldError(){
		Map<Field<?>, InvalidValueException> map = new HashMap<Field<?>, InvalidValueException>();
		for (Field<?> field : fieldGroup.getFields()) {
            try {
            	field.validate();
            } catch (InvalidValueException e) {
                map.put(field, e);
            }
        }
		return map;
	}
	
	/**
	 * @return la liste des champs en erreur
	 */
	public Map<Field<?>, InvalidValueException> getFieldsError(String[] fields){
		Map<Field<?>, InvalidValueException> map = new HashMap<Field<?>, InvalidValueException>();
		for (String fieldName : fields) {
			Field<?> field = fieldGroup.getField(fieldName);
			try {
				IRequiredField reqField = (IRequiredField) field;
            	reqField.preCommit();
            	field.validate();
            } catch (InvalidValueException e) {
                map.put(field, e);
            }
		}
		return map;
	}
	
	/**
	 * Valide les sheets
	 */
	public void validateSheet(){
		getSheetOnError(getFieldError());
	}
	
	/** Verifie qu'il y a une erreur dans un sheet
	 * @param fieldError
	 * @param FIELDS_ORDER
	 * @param tabOrder
	 */
	private void sheetHasError(Map<Field<?>, InvalidValueException> fieldError, String[] FIELDS_ORDER, Integer tabOrder){
		Boolean findError = false;
		for (int i = 0; i < FIELDS_ORDER.length; i++) {
		    if (fieldError.get(fieldGroup.getField(FIELDS_ORDER[i])) != null) {
				findError = true;
			}
		}
		displayErrorSheet(findError,tabOrder);
	}
	
	/**Affiche les erreur d'un sheet avec un point exclam en logo
	 * @param findError
	 * @param tabOrder
	 */
	public void displayErrorSheet(Boolean findError, Integer tabOrder){
		if (findError){
			this.getTab(tabOrder).setComponentError(new UserError(errorMessage));
			//this.getTab(tabOrder).setIcon(FontAwesome.EXCLAMATION);
		}else{
			//this.getTab(tabOrder).setIcon(null);
			this.getTab(tabOrder).setComponentError(null);		
		}
	}

}
