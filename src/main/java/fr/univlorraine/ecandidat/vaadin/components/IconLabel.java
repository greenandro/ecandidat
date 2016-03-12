package fr.univlorraine.ecandidat.vaadin.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

/**
 * Renvoi un label de Boolean sous forme d'icon check non-check 
 * @author Kevin Hergalant
 *
 */
public class IconLabel extends Label{

	/**serialVersionUID**/
	private static final long serialVersionUID = -2879012049228013806L;

	public IconLabel(Boolean value, Boolean alignCenter){
		super();
		if (value == null){
			return;
		}
		setContentMode(ContentMode.HTML);
		setValue(
				"<div style=width:100%;text-align:"+(alignCenter?"center":"left")+";>"+
				(value?FontAwesome.CHECK_SQUARE_O.getHtml():FontAwesome.SQUARE_O.getHtml())+
				"</div>"
				);
	}
}
