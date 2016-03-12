package fr.univlorraine.ecandidat.vaadin.form.i18n;

import com.vaadin.data.Validator;

import fr.univlorraine.ecandidat.entities.ecandidat.I18n;

/** Validateur de champs traduction
 * @author Kevin Hergalant
 *
 */
public class I18nValidator implements Validator{

	/** serialVersionUID **/
	private static final long serialVersionUID = 3725747230208436366L;
	
	private String errorOneMissing = "";
	private String errorSameLang = "";

	public I18nValidator(String errorOneMissing, String errorSameLang) {
		this.errorOneMissing = errorOneMissing;
		this.errorSameLang = errorSameLang;
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
			/*Verif qu'il ne manque pas une traduc*/
			if (e.getValTrad()==null || e.getValTrad().trim().equals("")){
				throw new InvalidValueException(errorOneMissing);
			}
			/*Verif qu'une langue n'est pas appelée deux fois*/
			if (objet.getI18nTraductions().stream().filter(x -> x.getLangue().getCodLangue().equals(e.getLangue().getCodLangue())).count()>1){				
				throw new InvalidValueException(errorSameLang+"("+e.getLangue().getCodLangue()+")");
			}
		});
	}

}
