package fr.univlorraine.ecandidat.views;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Page de maintenance
 * @author Kevin Hergalant
 *
 */
@SpringView(name = MaintenanceView.NAME)
public class MaintenanceView extends VerticalLayout implements View {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 235172001741115594L;

	public static final String NAME = "maintenanceView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setMargin(true);
		setSpacing(true);

		/* Titre */
		Label title = new Label(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
		title.addStyleName(ValoTheme.LABEL_H2);
		addComponent(title);

		/* Texte */
		addComponent(new Label(applicationContext.getMessage(NAME + ".text", null, UI.getCurrent().getLocale()), ContentMode.HTML));
	}


	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
	}

}
