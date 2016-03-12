package fr.univlorraine.ecandidat.vaadin.form;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.ui.TextField;

/**
 * Champs de text field d'integer customisé
 * @author Kevin Hergalant
 *
 */
public class RequiredIntegerField extends TextField implements IRequiredField{
	/** serialVersionUID **/
	private static final long serialVersionUID = 8665082481048433512L;

	private boolean shouldHideError = true;
	
	protected String requieredError;
	
	
	/**
	 * Constructeur
	 */
	public RequiredIntegerField() {
		super();		
		setConverter(new StringToIntegerConverter() {
            private static final long serialVersionUID = -1725520453911073564L;
 
            @Override
            protected NumberFormat getFormat(Locale locale) {
                return new DecimalFormat("#");
            }
        });
		addValidator(value->{
			if (value==null){
				return;
			}
			Integer integerValue = null;
			try{
				integerValue = Integer.valueOf(value.toString());				
			}catch (Exception e){
				throw new InvalidValueException(getConversionError());
			}
			if (value!=null && integerValue<0){
				throw new InvalidValueException(getConversionError());
			}
		});
	}

	/**
	 * @see com.vaadin.ui.AbstractField#shouldHideErrors()
	 */
	@Override
	protected boolean shouldHideErrors() {
		Boolean hide = shouldHideError;
		shouldHideError = false;
		return hide;
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#preCommit()
	 */
	@Override
	public void preCommit() {
		shouldHideError = false;
		super.setRequiredError(this.requieredError);
		if (isEmpty()){
			fireValueChange(false);
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#initField(java.lang.Boolean)
	 */
	@Override
	public void initField(Boolean immediate) {
		setImmediate(immediate);
		super.setRequiredError(null);
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setRequiredError(java.lang.String)
	 */
	@Override
	public void setRequiredError(String requiredMessage) {
		this.requieredError = requiredMessage;
	}
}