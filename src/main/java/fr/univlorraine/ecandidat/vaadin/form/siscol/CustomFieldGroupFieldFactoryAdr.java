package fr.univlorraine.ecandidat.vaadin.form.siscol;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;


/** FieldGroupFactory utilisé dans l'application pour l'adresse siscol
 * Permet d'utiliser le bon composant pour le bon type
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CustomFieldGroupFieldFactoryAdr extends DefaultFieldGroupFieldFactory{

	/** serialVersionUID **/
	private static final long serialVersionUID = 3606113268042858350L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TableRefController tableRefController;

	/**
	 * @see com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory#createField(java.lang.Class, java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {
		/*Le type du champs est un entier*/
		if (fieldType==RequiredIntegerField.class){	
			return fieldType.cast(new RequiredIntegerField());
		}
		/*La valeur est siScolPays*/
		else if (dataType==SiScolPays.class){
			return fieldType.cast(new ComboBoxPays(tableRefController.getListPaysEnService(),applicationContext.getMessage("adresse.siScolPays.suggest", null,  UI.getCurrent().getLocale())));
		}	
		/*La valeur est SiScolCommune*/
		else if (dataType==SiScolCommune.class){
			return fieldType.cast(new ComboBoxCommune(applicationContext.getMessage("adresse.commune.suggest", null,  UI.getCurrent().getLocale())));
		}
		
		/*Sinon, le champs est un simple TextField*/
		else{	
			return fieldType.cast(new RequiredTextField());
		}
	}
	
}
