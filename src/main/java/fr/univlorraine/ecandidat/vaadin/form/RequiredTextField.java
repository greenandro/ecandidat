package fr.univlorraine.ecandidat.vaadin.form;

import com.vaadin.ui.TextField;

/**
 * Champs de text field customisé
 * @author Kevin Hergalant
 *
 */
public class RequiredTextField extends TextField implements IRequiredField{
		
	/** serialVersionUID **/
	private static final long serialVersionUID = 5534424253891575433L;

	private boolean shouldHideError = true;
	
	private String requieredError;

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