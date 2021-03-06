package fr.univlorraine.ecandidat.vaadin.form.siscol;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.combobox.FilteringMode;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune_;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les communes
 * @author Kevin Hergalant
 *
 */
public class ComboBoxCommune extends RequiredComboBox<SiScolCommune>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 299220968012576093L;
	
	private BeanItemContainer<SiScolCommune> container;
	
	public ComboBoxCommune(String suggest) {
		super(false);
		container = new BeanItemContainer<SiScolCommune>(SiScolCommune.class,null);
		setContainerDataSource(container);
		setTextInputAllowed(true);
		setImmediate(true);
        setInputPrompt(suggest);
		setItemCaptionPropertyId(SiScolCommune_.libCom.getName());
		setItemCaptionMode(ItemCaptionMode.PROPERTY);
		setPageLength(10);
	}

	/**
	 * @see com.vaadin.ui.ComboBox#buildFilter(java.lang.String, com.vaadin.shared.ui.combobox.FilteringMode)
	 */
	@Override
	protected Filter buildFilter(String filterString, FilteringMode filteringMode) {
		container.removeAllContainerFilters();
		container.addContainerFilter(new SimpleStringFilter(SiScolCommune_.libCom.getName(), filterString, true, false));
		return null;
	}
	
	/** Met a jour la liste des communes
	 * @param liste
	 */
	public void setListCommune(List<SiScolCommune> liste){
		container.removeAllItems();
		if (liste!=null){
			container.addAll(liste);
		}
		
	}
}