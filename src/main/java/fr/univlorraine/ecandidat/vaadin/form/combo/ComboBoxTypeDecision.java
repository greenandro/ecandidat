package fr.univlorraine.ecandidat.vaadin.form.combo;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les Types de Decision
 * @author Kevin Hergalant
 *
 */
public class ComboBoxTypeDecision extends RequiredComboBox<TypeDecision>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 299220968012576093L;
	
	private BeanItemContainer<TypeDecision> container;
	private List<TypeDecision> listeTypDec;
	private String error;
	
	
	public ComboBoxTypeDecision(List<TypeDecision> listeTypDec,String error) {
		super(true);
		this.listeTypDec = listeTypDec;
		this.error = error;
		container = new BeanItemContainer<TypeDecision>(TypeDecision.class,listeTypDec);
		setContainerDataSource(container);
	}

	/**Filtre le container
	 * @param typeAvis
	 */
	public void filterListValue(TypeAvis typeAvis){
		container.removeAllItems();
		List<TypeDecision> newList = listeTypDec.stream().filter(e->e.getTypeAvis().equals(typeAvis)).collect(Collectors.toList());
		container.addAll(newList);	
	}
	
	/** SI la box n'est pas utilisé ou utilisé
	 * @param need
	 * @param typeDecision 
	 */
	public void setBoxNeeded(Boolean need, TypeDecision typeDecision){
		if (need){
			this.setVisible(true);
			this.setRequired(true);
			this.setRequiredError(error);
			this.setNullSelectionAllowed(false);
			if (typeDecision!=null){
				setValue(typeDecision);
			}
		}else{
			this.setVisible(false);
			this.setRequired(false);
			this.setRequiredError(null);
			this.setNullSelectionAllowed(true);
			this.setValue(null);
		}
	}
}