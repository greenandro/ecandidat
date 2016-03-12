package fr.univlorraine.ecandidat.vaadin.menu;

import java.io.Serializable;
import java.util.LinkedList;

import lombok.Data;
import lombok.ToString;

import com.vaadin.ui.Button;

/**
 * Class permettant d'avoir un menu et un sous menu dans l'UI
 * @author Kevin Hergalant
 *
 */
@Data
@ToString(exclude="btn")
public class Menu implements Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = -444837676017360049L;
	
	/*** La vue */
	private String view;
	
	/*** La liste des sous menu */
	private LinkedList<SubMenu> subMenu;

	/*** Le bouton associé */
	private Button btn;
	
	public Menu(String view, LinkedList<SubMenu> subMenu, Button btn) {
		super();
		this.view = view;
		this.subMenu = subMenu;
		this.btn = btn;
	}
	
	/** Verification si le menu possède des sous menus
	 * @return true si le menu possède deja le sous menu
	 */
	public Boolean hasSubMenu(){
		if (this.getSubMenu()!=null && this.getSubMenu().size()>0){
			return true;
		}else{
			return false;
		}
	}
}
