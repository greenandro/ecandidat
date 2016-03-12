package fr.univlorraine.ecandidat.vaadin.form.combo;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les MotivationAvis
 * @author Kevin Hergalant
 *
 */
public class ComboBoxMotivationAvis extends RequiredComboBox<MotivationAvis>{	
	/** serialVersionUID **/
	private static final long serialVersionUID = -3823108837648597992L;
	
	private BeanItemContainer<MotivationAvis> container;
	private String error;
	
	
	public ComboBoxMotivationAvis(List<MotivationAvis> listeMotivation,String error) {
		super(true);
		this.error = error;
		container = new BeanItemContainer<MotivationAvis>(MotivationAvis.class,listeMotivation);
		setContainerDataSource(container);
	}
	
	/** SI la box n'est pas utilisé ou utilisé
	 * @param need
	 * @param motiv 
	 */
	public void setBoxNeeded(Boolean need, MotivationAvis motiv){
		if (need){
			this.setVisible(true);
			this.setRequired(true);
			this.setRequiredError(error);
			this.setNullSelectionAllowed(false);
			if (motiv!=null){
				setValue(motiv);
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