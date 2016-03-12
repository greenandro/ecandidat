package fr.univlorraine.ecandidat.vaadin.form.combo;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les Mail
 * @author Kevin Hergalant
 *
 */
public class ComboBoxMail extends RequiredComboBox<Mail>{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -2134510504279019663L;	
	
	private BeanItemContainer<Mail> container;
	
	private List<Mail> listeMail;
	
	public ComboBoxMail(List<Mail> listeMail) {
		super(true);
		container = new BeanItemContainer<Mail>(Mail.class,null);
		setContainerDataSource(container);
		this.listeMail = listeMail;
	}
	
	
	/**Filtre le container
	 * @param typeAvis
	 */
	public void filterListValue(TypeAvis typeAvis){
		container.removeAllItems();
		List<Mail> newList = listeMail.stream().filter(e->e.getTypeAvis().equals(typeAvis)).collect(Collectors.toList());
		container.addAll(newList);
		if (newList.size()>0){
			setValue(newList.get(0));
		}
	}
}
