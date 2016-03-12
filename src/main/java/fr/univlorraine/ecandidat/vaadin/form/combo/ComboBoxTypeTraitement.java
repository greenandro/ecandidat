package fr.univlorraine.ecandidat.vaadin.form.combo;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les Types de Traitement
 * @author Kevin Hergalant
 *
 */
public class ComboBoxTypeTraitement extends RequiredComboBox<TypeTraitement>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -250664903792186515L;
	
	private BeanItemContainer<TypeTraitement> container;
	
	private List<TypeTraitement> listeTypeTraitement;
	
	
	public ComboBoxTypeTraitement(List<TypeTraitement> listeTypeTraitement) {
		super(true);
		container = new BeanItemContainer<TypeTraitement>(TypeTraitement.class,listeTypeTraitement);
		setContainerDataSource(container);		
		this.listeTypeTraitement = listeTypeTraitement;
	}
	
	/**
	 * Filtre la combo
	 */
	public void filterFinal(){
		container.removeAllItems();
		List<TypeTraitement> newList = 	listeTypeTraitement.stream().filter(e->e.getTemFinalTypTrait().equals(true)).collect(Collectors.toList());
		container.addAll(newList);
	}
}