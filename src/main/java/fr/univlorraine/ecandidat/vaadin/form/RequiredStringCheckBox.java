package fr.univlorraine.ecandidat.vaadin.form;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Champs de check box Oui/Non
 * @author Kevin Hergalant
 *
 */
public class RequiredStringCheckBox extends CustomField<String> implements IRequiredField{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -4721685836823109589L;
	
	private boolean shouldHideError = true;
	private String requieredError;
	
	protected CheckBox field;
	
	protected String value;

	public RequiredStringCheckBox() {
		field = new CheckBox();
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
	 * @see com.vaadin.ui.AbstractField#addValueChangeListener(com.vaadin.data.Property.ValueChangeListener)
	 */
	@Override
	public void addValueChangeListener(ValueChangeListener listener) {
		field.addValueChangeListener(listener);
	}


	/**
	 * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(String newFieldValue) throws ReadOnlyException,
			ConversionException {
		if (newFieldValue==null){
			newFieldValue = ConstanteUtils.TYP_BOOLEAN_NO;
		}
		value = newFieldValue;		
		super.setValue(newFieldValue);
	}

	/**
	 * @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object)
	 */
	@Override
	protected void setInternalValue(String newFieldValue){
		if (newFieldValue==null){
			newFieldValue = ConstanteUtils.TYP_BOOLEAN_NO;
		}
		value = newFieldValue;
		super.setInternalValue(newFieldValue);	
	}

	/**
	 * @see com.vaadin.ui.AbstractField#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getValue() == null;
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

	/**
	 * @see com.vaadin.ui.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		if (field.getValue()==null || !field.getValue()){
			return ConstanteUtils.TYP_BOOLEAN_NO;
		}			
		return ConstanteUtils.TYP_BOOLEAN_YES;
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getInternalValue()
	 */
	@Override
	protected String getInternalValue() {
		if (field.getValue()==null || !field.getValue()){
			return ConstanteUtils.TYP_BOOLEAN_NO;
		}			
		return ConstanteUtils.TYP_BOOLEAN_YES;
	}
	
	/**
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		if (value==null || value.equals(ConstanteUtils.TYP_BOOLEAN_NO)){
			field.setValue(false);
		}else{
			field.setValue(true);
		}		
		return field;
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<String> getType() {
		return String.class;
	}

}