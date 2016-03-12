package fr.univlorraine.ecandidat.vaadin.menu;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;

/** Class d'accordeon pour les menus
 * @author Kevin Hergalant
 *
 */
public class AccordionMenu extends VerticalLayout{ 
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 4819725284839402315L;
	
	private List<AccordionItemMenu> listChildren;
	private String itemId;

	public AccordionMenu(){
		super();
		listChildren = new ArrayList<AccordionItemMenu>();
	}
	
	/** AJout d'un item de menu
	 * @param menuItem
	 * @param id
	 */
	public void addItemMenu(AccordionItemMenu menuItem, String id){
		setWidth(100, Unit.PERCENTAGE);
		menuItem.setData(id);
		menuItem.addStyleName(StyleConstants.VALO_MENUACCORDEON);
		listChildren.add(menuItem);
		this.addComponent(menuItem);		
	}

	/** Appelé lors du clic sur un item
	 * @param id
	 */
	public void changeItem(String id) {
		//on place l'id de l'item en cours sur l'id clique
		itemId = id;
		listChildren.forEach(e->{
			if (e.getData().equals(id)){
				e.setButtonVisible(true);
			}else{
				e.setButtonVisible(false);
			}
		});
	}
	
	/**
	 * A la premiere construction on ouvre le 1er item
	 */
	public void selectFirst(){
		if (listChildren!=null && listChildren.size()>0){
			changeItem((String) listChildren.get(0).getData());
		}
	}

	/** Retourne l'id de l'item selectionne en cours
	 * @return l'id de l'item
	 */
	public String getItemId() {
		return itemId;
	}
	
}
