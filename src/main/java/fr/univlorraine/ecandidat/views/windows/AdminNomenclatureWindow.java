package fr.univlorraine.ecandidat.views.windows;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.NomenclatureController;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;

/**
 * Fenêtre de verification de nomenclature
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class AdminNomenclatureWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 3811169913754125884L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient NomenclatureController nomenclatureController;

	/* Composants */
	private Button btnReload;
	private Button btnForceReload;
	private Button btnAnnuler;

	/**
	 * Crée une fenêtre de verification de nomenclature
	 */
	public AdminNomenclatureWindow() {
		/* Style */
		setModal(true);
		setWidth(700,Unit.PIXELS);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("nomenclature.window", null, UI.getCurrent().getLocale()));
		
		Boolean isNeedToReload = nomenclatureController.isNomenclatureToReload();
		
		Label labelInfo = new Label();
		layout.addComponent(labelInfo);

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new Button(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);
		
		btnForceReload = new Button(applicationContext.getMessage("parametre.maj.nomenclature.forcebtn", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnForceReload.addClickListener(e -> {
			/* Enregistre la nomenclature saisie */
			nomenclatureController.majNomenclature(false);
			/* Ferme la fenêtre */
			close();
		});
		buttonsLayout.addComponent(btnForceReload);
		buttonsLayout.setComponentAlignment(btnForceReload, Alignment.MIDDLE_CENTER);

		btnReload = new Button(applicationContext.getMessage("parametre.maj.nomenclature.btn", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnReload.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnReload.addClickListener(e -> {
			/* Enregistre la nomenclature saisie */
			nomenclatureController.majNomenclature(false);
			/* Ferme la fenêtre */
			close();
		});
		buttonsLayout.addComponent(btnReload);
		buttonsLayout.setComponentAlignment(btnReload, Alignment.MIDDLE_RIGHT);
		
		Version vCourante = nomenclatureController.getNomenclatureVersionCourante();
		Version vDb = nomenclatureController.getNomenclatureVersionDb();
		
		if (isNeedToReload){
			labelInfo.setValue(applicationContext.getMessage("parametre.maj.nomenclature.nok", new Object[]{vDb.getValVersion(),vCourante.getValVersion()}, UI.getCurrent().getLocale()));
			btnReload.setEnabled(true);
			
		}else{
			labelInfo.setValue(applicationContext.getMessage("parametre.maj.nomenclature.ok", new Object[]{vDb.getValVersion()}, UI.getCurrent().getLocale()));
			btnReload.setEnabled(false);
		}

		/* Centre la fenêtre */
		center();
	}

}
