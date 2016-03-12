package fr.univlorraine.ecandidat.vaadin.form.siscol;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.combobox.FilteringMode;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement_;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** Combobox pour les etablissemnts
 * @author Kevin
 *
 */
public class ComboBoxEtablissement extends RequiredComboBox<SiScolEtablissement>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 2494175146537232455L;

	
	private BeanItemContainer<SiScolEtablissement> container;
	
	public ComboBoxEtablissement(String suggest) {
		super(false);
		container = new BeanItemContainer<SiScolEtablissement>(SiScolEtablissement.class,null);
		setContainerDataSource(container);
		setTextInputAllowed(true);
		setImmediate(true);
        setInputPrompt(suggest);
		setItemCaptionPropertyId(SiScolEtablissement_.libEtb.getName());
		setItemCaptionMode(ItemCaptionMode.PROPERTY);
		setPageLength(10);
	}

	/**
	 * @see com.vaadin.ui.ComboBox#buildFilter(java.lang.String, com.vaadin.shared.ui.combobox.FilteringMode)
	 */
	@Override
	protected Filter buildFilter(String filterString, FilteringMode filteringMode) {
		container.removeAllContainerFilters();
		container.addContainerFilter(new SimpleStringFilter(SiScolEtablissement_.libEtb.getName(), filterString, true, false));
		return null;
	}
	
	/** Met a jour la liste des etab
	 * @param liste
	 */
	public void setListEtablissement(List<SiScolEtablissement> liste){
		container.removeAllItems();
		if (liste!=null){
			container.addAll(liste);
		}
		
	}
}