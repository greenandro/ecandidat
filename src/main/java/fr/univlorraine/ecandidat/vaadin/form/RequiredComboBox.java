package fr.univlorraine.ecandidat.vaadin.form;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/** Classe modèle pour les comboBox custo
 * @author Kevin Hergalant
 *
 * @param <T>
 */
public class RequiredComboBox<T> extends ComboBox implements IRequiredField{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -3237294229939193762L;

	private boolean shouldHideError = true;

	protected String requieredError;
	
	/** Constructeur
	 * @param useGenericLabel
	 */
	public RequiredComboBox(Boolean useGenericLabel){	
		setNullSelectionAllowed(false);
		setTextInputAllowed(false);
		setImmediate(true);
		setWidth(100, Unit.PERCENTAGE);
		setPageLength(0);
		if (useGenericLabel){
			setItemCaptionPropertyId(ConstanteUtils.GENERIC_LIBELLE);
		}
	}
	
	/** Constructeur
	 * @param listeComboBox
	 * @param type
	 */
	public RequiredComboBox(List<T> listeComboBox, Class<T> type){
		this(true);
		setContainerDataSource(new BeanItemContainer<T>(type,listeComboBox));
	}
	
	/** Constructeur
	 */
	public RequiredComboBox(){
		this(true);
	}
	
	/** Change le container
	 * @param listeComboBox
	 * @param type
	 */
	public void setContainerDataSource(List<T> listeComboBox, Class<T> type){
		setContainerDataSource(new BeanItemContainer<T>(type,listeComboBox));
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
	 * @see com.vaadin.ui.AbstractSelect#isEmpty()
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
}
