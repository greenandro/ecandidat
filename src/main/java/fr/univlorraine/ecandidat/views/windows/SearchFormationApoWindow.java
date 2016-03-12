package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.FormationController;
import fr.univlorraine.ecandidat.controllers.IndividuController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.siscol.Vet;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Fenêtre de recherche de formation apogee
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class SearchFormationApoWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -1777247785495796621L;
		
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormationController formationController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient UserController userController;

	public static final String[] FIELDS_ORDER = {"id.codEtpVet","id.codVrsVet","libVet","id.codCge","libTypDip"};
	
	/* Composants */
	private TextField searchBox;
	private Button btnSearch;
	private BeanItemContainer<Vet> vetContainer;
	private TableFormating tableResult;
	private Button btnValider;
	private Button btnAnnuler;

	/*Listener*/
	private VetListener vetListener;


	/**
	 * Crée une fenêtre de recherche de formaiton apogée
	 * @param idCtrCand
	 */
	public SearchFormationApoWindow(Integer idCtrCand) {
		/* Style */
		setWidth(850, Unit.PIXELS);
		setHeight(480, Unit.PIXELS);
		setModal(true);
		setResizable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setHeight(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);

		/* Titre */
		setCaption(applicationContext.getMessage("window.search.vet.title", null, Locale.getDefault()));
		
		/* Recherche */
		HorizontalLayout searchLayout = new HorizontalLayout();
		searchBox = new TextField();
		searchBox.addShortcutListener(new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {

			/** serialVersionUID **/
			private static final long serialVersionUID = 4119756957960484247L;

			@Override
		    public void handleAction(Object sender, Object target) {
		    	performSearch();
		    }
		});

		btnSearch = new Button(applicationContext.getMessage("window.search", null, Locale.getDefault()));
		btnSearch.addClickListener(e->performSearch());
		Label labelLimit = new Label(applicationContext.getMessage("formation.window.apo.limit", new Object[]{ConstanteUtils.NB_MAX_RECH_FORM}, Locale.getDefault()));
		
		searchLayout.setSpacing(true);
		searchLayout.addComponent(searchBox);
		searchLayout.setComponentAlignment(searchBox, Alignment.MIDDLE_LEFT);
		searchLayout.addComponent(btnSearch);
		searchLayout.setComponentAlignment(btnSearch, Alignment.MIDDLE_LEFT);
		searchLayout.addComponent(labelLimit);
		searchLayout.setComponentAlignment(labelLimit, Alignment.MIDDLE_LEFT);
		
		layout.addComponent(searchLayout);
		
		/* Table de Resultat de recherche*/
		vetContainer = new BeanItemContainer<Vet>(Vet.class);
		vetContainer.addNestedContainerProperty("id.codEtpVet");
		vetContainer.addNestedContainerProperty("id.codVrsVet");
		vetContainer.addNestedContainerProperty("id.codCge");
		tableResult = new TableFormating(null,vetContainer);		
		tableResult.setVisibleColumns((Object[])FIELDS_ORDER);
		//tableResult.setSortContainerPropertyId(AnneeUni_.codAnu.getName());
		tableResult.setSortAscending(false);
		for (String fieldName : FIELDS_ORDER) {
			tableResult.setColumnHeader(fieldName, applicationContext.getMessage("vet."+fieldName, null, UI.getCurrent().getLocale()));
		}
		tableResult.setColumnCollapsingAllowed(true);
		tableResult.setColumnReorderingAllowed(true);
		tableResult.setSelectable(true);
		tableResult.setImmediate(true);
		tableResult.setSizeFull();
		tableResult.addItemSetChangeListener(e -> tableResult.sanitizeSelection());
		tableResult.addValueChangeListener(e -> {
			/* Le bouton d'enregistrement est actif seulement si un anneeUni est sélectionné. */
			boolean vetIsSelected = tableResult.getValue() instanceof Vet;
			btnValider.setEnabled(vetIsSelected);
		});
		
		layout.addComponent(tableResult);
		layout.setExpandRatio(tableResult, 1.0f);

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new Button(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);
		
		btnValider = new Button(applicationContext.getMessage("btnValid", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnValider.setEnabled(false);
		btnValider.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnValider.addClickListener(e -> {
			performAction();
		});
		buttonsLayout.addComponent(btnValider);
		buttonsLayout.setComponentAlignment(btnValider, Alignment.MIDDLE_RIGHT);
		

		/* Centre la fenêtre */
		center();
	}
	
	/**
	 * Vérifie els donnée et si c'est ok, fait l'action (renvoie le AnneeUni)
	 */
	private void performAction(){
		if (vetListener != null){
			if (tableResult.getValue()==null){
				Notification.show(applicationContext.getMessage("window.search.selectrow", null, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
				return;
			}else{
				Vet vet = (Vet) tableResult.getValue();
				vetListener.btnOkClick(vet);
				close();
			}					
		}
	}
	
	/**
	 * Effectue la recherche
	 * @param codCgeUserApo 
	 * @param codCgeUser 
	 */
	private void performSearch(){
		if (searchBox.getValue().equals(null) || searchBox.getValue().equals("") || searchBox.getValue().length()<ConstanteUtils.NB_MIN_CAR_FORM){
			Notification.show(applicationContext.getMessage("window.search.morethan", new Object[]{ConstanteUtils.NB_MIN_CAR_FORM}, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
		}else{
			vetContainer.removeAllItems();
			try {
				vetContainer.addAll(formationController.getVetByCGE(searchBox.getValue()));
			} catch (SiScolException e) {
				Notification.show(applicationContext.getMessage("siscol.connect.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				close();
			}
		}
	}

	/**
	 * Défini le 'VetListener' utilisé
	 * @param vetListener
	 */
	public void addVetListener(VetListener vetListener) {
		this.vetListener = vetListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface VetListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param vet la vet a renvoyer
		 */
		public void btnOkClick(Vet vet);

	}

}
