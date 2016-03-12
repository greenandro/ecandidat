package fr.univlorraine.ecandidat.vaadin.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;

/**
 * Class d'Item de menu accordeon
 * @author Kevin Hergalant
 *
 */
public class AccordionItemMenu extends VerticalLayout {

	/** serialVersionUID **/
	private static final long serialVersionUID = 6121796222355774584L;

	private VerticalLayout vlButton;

	/**
	 * Constructeur
	 * 
	 * @param title
	 * @param parent
	 */
	public AccordionItemMenu(String title, AccordionMenu parent,
			Boolean isExpandable) {
		super();
		setWidth(100, Unit.PERCENTAGE);
		/*
		 * Les labels n'etant pas cliquable, on passe par un layout
		 * intermediaire
		 */
		VerticalLayout layoutClickable = new VerticalLayout();

		layoutClickable.setWidth(100, Unit.PERCENTAGE);

		/* Label */
		Label label = new Label(title);
		label.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
		label.setSizeUndefined();
		layoutClickable.addComponent(label);
		layoutClickable.addStyleName(StyleConstants.VALO_MENUACCORDEON);
		addComponent(layoutClickable);
		if (isExpandable) {
			layoutClickable.addStyleName(StyleConstants.CLICKABLE);			
			layoutClickable.addLayoutClickListener(e -> {
				parent.changeItem((String) getData());
			});
		}
		vlButton = new VerticalLayout();
		vlButton.addStyleName(StyleConstants.VALO_MENUACCORDEON);
		vlButton.setWidth(100, Unit.PERCENTAGE);
		addComponent(vlButton);
	}

	public AccordionItemMenu(String title, AccordionMenu parent) {
		this(title, parent, true);

	}

	/**
	 * Ajout d'un bouton a l'item de menu
	 * 
	 * @param btn
	 */
	public void addButton(Button btn) {
		/*
		 * CssLayout layoutButton = new CssLayout();
		 * layoutButton.addComponent(btn); layoutButton.setWidth(100,
		 * Unit.PERCENTAGE); layoutButton.addStyleName(StyleConstants.VALO_MY_MENU_MAX_WIDTH);
		 */
		vlButton.addComponent(btn);
	}
	
	/**
	 * Ajout d'un bouton a l'item de menu
	 * 
	 * @param btn
	 */
	public void addButton(Button btn, Alignment a) {
		vlButton.addComponent(btn);
		vlButton.setComponentAlignment(btn, a);
	}

	/**
	 * Rend les bouton invisible ou visibles
	 * 
	 * @param visible
	 */
	public void setButtonVisible(Boolean visible) {
		vlButton.setVisible(visible);
	}

	/**
	 * Supprime tout les boutons d'un menu
	 */
	public void removeAllButtons() {
		vlButton.removeAllComponents();
	}

	/**
	 * @return le nombre de bouton de l'item
	 */
	public Integer getNbButton() {
		return vlButton.getComponentCount();
	}

	/** Supprime les boutons
	 * @param button
	 */
	public void removeAllButtons(Button button, Button button2) {
		List<Component> listeToDelete = new ArrayList<Component>();
		Iterator<Component> i = vlButton.iterator();
		while (i.hasNext()) {
			Component c = i.next();
			if (!c.equals(button) && !c.equals(button2)){
				listeToDelete.add(c);			
			}			
		}
		listeToDelete.forEach(e->vlButton.removeComponent(e));
	}

	/*public void hideOrShowAll(Boolean visible) {
		Iterator<Component> i = vlButton.iterator();
		while (i.hasNext()) {
			Component c = i.next();
			c.setVisible(visible);
		}
	}*/
}
