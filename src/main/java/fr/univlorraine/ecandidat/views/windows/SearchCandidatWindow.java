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
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne_;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat_;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Fenêtre de recherche de candidat
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class SearchCandidatWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -497298981780250180L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	
	public static final String[] PEOPLE_FIELDS_ORDER = {CompteMinima_.numDossierOpiCptMin.getName(),
		CompteMinima_.nomCptMin.getName(),
		CompteMinima_.prenomCptMin.getName(),
		CompteMinima_.loginCptMin.getName(),
		CompteMinima_.supannEtuIdCptMin.getName(),
		CompteMinima_.candidat.getName()+"."+Candidat_.nomPatCandidat.getName(),
		CompteMinima_.candidat.getName()+"."+Candidat_.prenomCandidat.getName(),
		CompteMinima_.campagne.getName()+"."+Campagne_.codCamp.getName()};

	/* Composants */
	private TextField searchBox;
	private Button btnSearch;
	private TableFormating tableResult;
	private BeanItemContainer<CompteMinima> container;
	private Button btnValider;
	private Button btnAnnuler;

	/*Listener*/
	private CompteMinimaListener compteMinimaListener;


	/**
	 * Crée une fenêtre de recherche de candidat
	 */
	public SearchCandidatWindow() {
		
		/* Style */
		setWidth(980, Unit.PIXELS);
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
		setCaption(applicationContext.getMessage("window.search.candidat.title", null, Locale.getDefault()));
		
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
		searchLayout.setSpacing(true);
		searchLayout.addComponent(searchBox);
		searchLayout.addComponent(btnSearch);
		
		
		
		/*Ajout des commandes*/
		layout.addComponent(searchLayout);
		
		/* Table de Resultat de recherche*/
		container = new BeanItemContainer<CompteMinima>(CompteMinima.class);
		container.addNestedContainerProperty(CompteMinima_.candidat.getName()+"."+Candidat_.nomPatCandidat.getName());
		container.addNestedContainerProperty(CompteMinima_.candidat.getName()+"."+Candidat_.prenomCandidat.getName());
		container.addNestedContainerProperty(CompteMinima_.campagne.getName()+"."+Campagne_.codCamp.getName());
		
		tableResult = new TableFormating(null, container);
		
		String[] columnHeadersHarp = new String[PEOPLE_FIELDS_ORDER.length];
		for (int fieldIndex = 0; fieldIndex < PEOPLE_FIELDS_ORDER.length; fieldIndex++){
			columnHeadersHarp[fieldIndex] = applicationContext.getMessage("cptMin."+PEOPLE_FIELDS_ORDER[fieldIndex], null, Locale.getDefault());
		}
		tableResult.setVisibleColumns((Object[])PEOPLE_FIELDS_ORDER);
		tableResult.setColumnHeaders(columnHeadersHarp);
		tableResult.setColumnCollapsingAllowed(true);
		tableResult.setColumnReorderingAllowed(true);
		tableResult.setSelectable(true);
		tableResult.setImmediate(true);
		tableResult.setSizeFull();
		tableResult.addItemSetChangeListener(e -> tableResult.sanitizeSelection());
		tableResult.addValueChangeListener(e -> {
			/* Le bouton d'ouverture est actif seulement si un CompteMinima est sélectionné. */
			boolean peopleIsSelected = tableResult.getValue() instanceof CompteMinima;
			btnValider.setEnabled(peopleIsSelected);
		});
		tableResult.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				tableResult.select(e.getItemId());
				btnValider.click();
			}
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
		
		btnValider = new Button(applicationContext.getMessage("btnOpen", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
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
	 * Effectue la recherche
	 */
	private void performSearch(){
		if (searchBox.getValue().equals(null) || searchBox.getValue().equals("") || searchBox.getValue().length()<ConstanteUtils.NB_MIN_CAR_CAND){
			Notification.show(applicationContext.getMessage("window.search.morethan", new Object[]{ConstanteUtils.NB_MIN_CAR_CAND}, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
		}else{
			container.removeAllItems();
			container.addAll(candidatController.getCptMinByFilter(searchBox.getValue()));
		}
	}
	
	/**
	 * Vérifie els donnée et si c'est ok, fait l'action (renvoie le PeopleLdap)
	 */
	private void performAction(){
		if (compteMinimaListener != null){
			if (tableResult.getValue()==null){
				Notification.show(applicationContext.getMessage("window.search.selectrow", null, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
				return;
			}else{
				CompteMinima cpt = (CompteMinima) tableResult.getValue();
				compteMinimaListener.btnOkClick(cpt);
				close();
			}					
		}
	}

	/**
	 * Défini le 'compteMinimaListener' utilisé
	 * @param compteMinimaListener
	 */
	public void addCompteMinimaListener(CompteMinimaListener compteMinimaListener) {
		this.compteMinimaListener = compteMinimaListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface CompteMinimaListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param cptMin le cptMin a renvoyer 
		 */
		public void btnOkClick(CompteMinima cptMin);

	}

}
