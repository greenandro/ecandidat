package fr.univlorraine.ecandidat.vaadin.form.siscol;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.combobox.FilteringMode;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement_;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les departements
 * @author Kevin Hergalant
 *
 */
public class ComboBoxDepartement extends RequiredComboBox<SiScolDepartement>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -6228803739439963326L;
	
	private BeanItemContainer<SiScolDepartement> container;
	
	public ComboBoxDepartement(List<SiScolDepartement> listeSiScolDepartement, String suggest) {
		super(true);
		container = new BeanItemContainer<SiScolDepartement>(SiScolDepartement.class,listeSiScolDepartement);
		setContainerDataSource(container);
		setTextInputAllowed(true);
		setImmediate(true);
        setInputPrompt(suggest);
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
		SimpleStringFilter libFilter = new SimpleStringFilter(SiScolDepartement_.libDep.getName(), filterString, true, false);
		SimpleStringFilter codFilter = new SimpleStringFilter(SiScolDepartement_.codDep.getName(), filterString, true, false);		
		container.addContainerFilter(new Or(libFilter,codFilter));
		return null;
	}
}