package fr.univlorraine.ecandidat.vaadin.form;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.views.windows.SearchAnneeUnivApoWindow;


/**
 * Champs de recherche d'annee univ
 * @author Kevin Hergalant
 *
 */
public class SearchAnneeUnivApoField extends CustomField<String> implements IRequiredField{

	/** serialVersionUID **/
	private static final long serialVersionUID = 3994791458997281136L;
	
	/*Variable pour le champs et les msg d'erreur*/
	private boolean shouldHideError = true;
	private String requieredError;
	
	private HorizontalLayout layout;
	private TextField anneeField;
	private Button btnSearch;
	
	/**
	 * Constructeur, initialisation du champs
	 * @param libelleBtnFind 
	 */
	public SearchAnneeUnivApoField(String libelleBtnFind) {
		super();
		layout = new HorizontalLayout();
		layout.setSpacing(true);
		anneeField = new TextField();
		anneeField.addValueChangeListener(e->showOrHideError());
		anneeField.setNullRepresentation("");
		anneeField.setReadOnly(true);
		btnSearch = new Button(libelleBtnFind,FontAwesome.SEARCH);
		btnSearch.addClickListener(e->{
			SearchAnneeUnivApoWindow window = new SearchAnneeUnivApoWindow();
			window.addAnneeUniListener(a->changeFieldValue(a));
			UI.getCurrent().addWindow(window);
		});
		layout.addComponent(anneeField);
		layout.addComponent(btnSearch);
	}	
	

	/**
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {		
		return layout;
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	/** Change la valeur
	 * @param value
	 */
	private void changeFieldValue(String value){
		anneeField.setReadOnly(false);
		anneeField.setValue(value);
		anneeField.setReadOnly(true);
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object)
	 */
	@Override
	protected void setInternalValue(String newFieldValue){
		super.setInternalValue(newFieldValue);
		changeFieldValue(newFieldValue);
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(String newFieldValue) throws ReadOnlyException,
			ConversionException {
		super.setInternalValue(newFieldValue);
		changeFieldValue(newFieldValue);
	}
	
	/**
	 * Montre ou cache l'erreur
	 */
	private void showOrHideError(){
		fireValueChange(false);
		if (isRequired()){
			if (anneeField.getValue()==null){
				anneeField.addStyleName(StyleConstants.FIELD_ERROR);
			}else{
				anneeField.removeStyleName(StyleConstants.FIELD_ERROR);
			}
		}		
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		return anneeField.getValue();
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getInternalValue()
	 */
	@Override
	protected String getInternalValue() {
		return anneeField.getValue();
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
	 * @see com.vaadin.ui.AbstractField#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return anneeField.getValue()==null || anneeField.getValue().equals("");
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#preCommit()
	 */
	@Override
	public void preCommit() {
		showOrHideError();
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
