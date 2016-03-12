package fr.univlorraine.ecandidat.vaadin.form.i18n;

import java.net.MalformedURLException;
import java.net.URL;

import com.vaadin.data.Validator;

import fr.univlorraine.ecandidat.entities.ecandidat.I18n;

/** Validateur de champs traduction pour URL
 * @author Kevin Hergalant
 *
 */
public class I18nUrlValidator implements Validator{

	/** serialVersionUID **/
	private static final long serialVersionUID = 3725747230208436366L;
	
	private String urlMalformedError = "";

	public I18nUrlValidator(String urlMalformedError) {
		this.urlMalformedError = urlMalformedError;
	}

	/**
	 * @see com.vaadin.data.Validator#validate(java.lang.Object)
	 */
	@Override
	public void validate(Object value) throws InvalidValueException {
		/*Si la valeur est null donc nouvelle, on sort*/
		if (value==null){
			return;
		}
		I18n objet = (I18n)value;		
		/*Parcourt de la liste de traductions*/
		objet.getI18nTraductions().forEach(e ->{
			try {
			    new URL(e.getValTrad());
			  }
			 catch (MalformedURLException m) {
			    throw new InvalidValueException(urlMalformedError);
			  }
		});
	}

}
