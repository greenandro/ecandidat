package fr.univlorraine.ecandidat.views.windows;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Fenêtre de confirmation
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class ConfirmWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -6274038192734633032L;

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	/* Composants */
	private Button btnOui = new Button();
	private Button btnNon = new Button();

	/** Ajoute un listener sur le bouton oui
	 * @param clickListener
	 */
	public void addBtnOuiListener(ClickListener clickListener) {
		btnOui.addClickListener(clickListener);
	}

	/** Supprime un listener sur le bouton oui
	 * @param clickListener
	 */
	public void removeBtnOuiListener(ClickListener clickListener) {
		btnOui.removeClickListener(clickListener);
	}

	/** Ajoute un listener sur le bouton non
	 * @param clickListener
	 */
	public void addBtnNonListener(ClickListener clickListener) {
		btnNon.addClickListener(clickListener);
	}

	/** Supprime un listener sur le bouton non
	 * @param clickListener
	 */
	public void removeBtnNonListener(ClickListener clickListener) {
		btnNon.removeClickListener(clickListener);
	}

	/**
	 * Crée une fenêtre de confirmation avec un message et un titre par défaut
	 */
	public ConfirmWindow() {
		this(null, null);
	}

	/**
	 * Crée une fenêtre de confirmation avec un titre par défaut
	 * @param message
	 */
	public ConfirmWindow(String message) {
		this(message, null);
	}

	/**
	 * Crée une fenêtre de confirmation
	 * @param message
	 * @param titre
	 */
	public ConfirmWindow(String message, String titre) {
		/* Style */
		setWidth(400, Unit.PIXELS);
		setModal(true);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		if (titre == null) {
			titre = applicationContext.getMessage("confirmWindow.defaultTitle", null, UI.getCurrent().getLocale());
		}
		setCaption(titre);

		/* Texte */
		if (message == null) {
			message = applicationContext.getMessage("confirmWindow.defaultQuestion", null, UI.getCurrent().getLocale());
		}
		Label textLabel = new Label(message);
		layout.addComponent(textLabel);

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnNon.setCaption(applicationContext.getMessage("confirmWindow.btnNon", null, UI.getCurrent().getLocale()));
		btnNon.setIcon(FontAwesome.TIMES);
		btnNon.addClickListener(e -> close());
		buttonsLayout.addComponent(btnNon);
		buttonsLayout.setComponentAlignment(btnNon, Alignment.MIDDLE_LEFT);

		btnOui.setCaption(applicationContext.getMessage("confirmWindow.btnOui", null, UI.getCurrent().getLocale()));
		btnOui.setIcon(FontAwesome.CHECK);
		btnOui.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnOui.addClickListener(e -> close());
		buttonsLayout.addComponent(btnOui);
		buttonsLayout.setComponentAlignment(btnOui, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

}
