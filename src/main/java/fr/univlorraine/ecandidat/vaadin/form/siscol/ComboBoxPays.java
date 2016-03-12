package fr.univlorraine.ecandidat.vaadin.form.siscol;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.combobox.FilteringMode;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays_;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les pays
 * @author Kevin Hergalant
 *
 */
public class ComboBoxPays extends RequiredComboBox<SiScolPays>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 299220968012576093L;
	
	private BeanItemContainer<SiScolPays> container;
	
	public ComboBoxPays(List<SiScolPays> listeSiScolPays, String suggest) {
		super(false);
		container = new BeanItemContainer<SiScolPays>(SiScolPays.class,listeSiScolPays);
		setContainerDataSource(container);
		setTextInputAllowed(true);
		setImmediate(true);
        setInputPrompt(suggest);
        setItemCaptionPropertyId(SiScolPays_.libPay.getName());
		setItemCaptionMode(ItemCaptionMode.PROPERTY);
		setPageLength(10);
	}

	/**
	 * @see com.vaadin.ui.ComboBox#buildFilter(java.lang.String, com.vaadin.shared.ui.combobox.FilteringMode)
	 */
	@Override
	protected Filter buildFilter(String filterString,
			FilteringMode filteringMode) {
		container.removeAllContainerFilters();
		container.addContainerFilter(new SimpleStringFilter(SiScolPays_.libPay.getName(), filterString, true, false));
		return null;
	}
	
	/**
	 * Change le libellé affiché et le suggest en nationalité
	 * @param suggest 
	 */
	public void setToNationalite(String suggest){
		setItemCaptionPropertyId(SiScolPays_.libNat.getName());
		setInputPrompt(suggest);
	}
	
	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox#preCommit()
	 */
	@Override
	public void preCommit() {
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox#initField(java.lang.Boolean)
	 */
	@Override
	public void initField(Boolean immediate) {
		
	}
}