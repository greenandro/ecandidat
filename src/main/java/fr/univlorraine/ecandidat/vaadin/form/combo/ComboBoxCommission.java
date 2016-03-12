package fr.univlorraine.ecandidat.vaadin.form.combo;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les Commissions
 * @author Kevin Hergalant
 *
 */
public class ComboBoxCommission extends RequiredComboBox<Commission>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -6162803324051983163L;
	
	BeanItemContainer<Commission> container;
	
	
	public ComboBoxCommission() {
		super(true);
		container = new BeanItemContainer<Commission>(Commission.class);
		setContainerDataSource(container);
	}

	/**Filtre le container
	 * @param liste
	 */
	public void filterListValue(List<Commission> liste){
		container.removeAllItems();
		container.addAll(liste);	
	}
}