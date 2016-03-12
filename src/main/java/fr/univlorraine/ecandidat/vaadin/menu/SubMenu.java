package fr.univlorraine.ecandidat.vaadin.menu;

import java.io.Serializable;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Label;

import lombok.Data;
import lombok.ToString;

/**
 * Class permettant d'avoir un menu et un sous menu dans l'UI
 * @author Kevin Hergalant
 *
 */
@Data
@ToString(exclude={"icon","label"})
public class SubMenu implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 156153180626476710L;
	
	/*** La vue */
	private String vue;
	
	/*** L'icone */
	private FontAwesome icon;
	
	/*** Le label du tabSheet */
	private Label label;
	
	public SubMenu(String vue, FontAwesome icon) {
		super();
		this.vue = vue;
		this.icon = icon;
		this.label = new Label();
		this.label.setData(this.vue);
	}
}
