package fr.univlorraine.ecandidat.vaadin.form.combo;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les Bacs
 * @author Kevin Hergalant
 *
 */
public class ComboBoxBacOuEqu extends RequiredComboBox<SiScolBacOuxEqu>{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -1735636736386162950L;

	private BeanItemContainer<SiScolBacOuxEqu> container;
	
	private List<SiScolBacOuxEqu> listeSiScolBacOuxEqu;
	
	public ComboBoxBacOuEqu(List<SiScolBacOuxEqu> listeSiScolBacOuxEqu) {
		super(true);
		container = new BeanItemContainer<SiScolBacOuxEqu>(SiScolBacOuxEqu.class,null);
		setContainerDataSource(container);
		this.listeSiScolBacOuxEqu = listeSiScolBacOuxEqu;
	}
	
	
	/**Filtre le container
	 * @param annee
	 */
	public void filterListValue(Integer annee){
		container.removeAllItems();
		if (annee!=null){
			List<SiScolBacOuxEqu> newList = listeSiScolBacOuxEqu.stream().filter(e->e.getDaaFinVldBac()==null || Integer.valueOf(e.getDaaFinVldBac())>=annee).collect(Collectors.toList());
			container.addAll(newList);
			if (newList.size()>0){
				setValue(newList.get(0));
			}
		}		
	}
}
