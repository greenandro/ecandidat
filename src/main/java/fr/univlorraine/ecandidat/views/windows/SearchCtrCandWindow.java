package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature_;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Fenêtre de recherche de centre de candidature
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class SearchCtrCandWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 3475563233611742318L;
		
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CentreCandidatureController centreCandidatureController;
	
	public static final String[] FIELDS_ORDER = {CentreCandidature_.codCtrCand.getName(),CentreCandidature_.libCtrCand.getName()};

	/* Composants */
	private TableFormating tableResult;
	private Button btnValider;
	private Button btnAnnuler;

	/*Listener*/
	private CentreCandidatureListener centreCandidatureListener;


	/**
	 * Crée une fenêtre de recherche de centre de candidature
	 */
	public SearchCtrCandWindow() {
		/* Style */
		setWidth(740, Unit.PIXELS);
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
		setCaption(applicationContext.getMessage("ctrCand.window.search.title", null, Locale.getDefault()));
		
		/* Table de Resultat de recherche*/
		List<CentreCandidature> listeCentreCandidature = centreCandidatureController.getListCentreCandidature();
		tableResult = new TableFormating(null, new BeanItemContainer<CentreCandidature>(CentreCandidature.class,listeCentreCandidature));
		
		tableResult.setVisibleColumns((Object[])FIELDS_ORDER);
		tableResult.setSortContainerPropertyId(CentreCandidature_.codCtrCand.getName());
		tableResult.setSortAscending(true);
		for (String fieldName : FIELDS_ORDER) {
			tableResult.setColumnHeader(fieldName, applicationContext.getMessage("ctrCand.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		tableResult.setColumnCollapsingAllowed(true);
		tableResult.setColumnReorderingAllowed(true);
		tableResult.setSelectable(true);
		tableResult.setImmediate(true);
		tableResult.setSizeFull();
		tableResult.addItemSetChangeListener(e -> tableResult.sanitizeSelection());
		tableResult.addValueChangeListener(e -> {
			/* Le bouton d'enregistrement est actif seulement si un ctrcand est sélectionné. */
			boolean ctrIsSelected = tableResult.getValue() instanceof CentreCandidature;
			btnValider.setEnabled(ctrIsSelected);
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
	 * Vérifie els donnée et si c'est ok, fait l'action (renvoie le CentreCandidature)
	 */
	private void performAction(){
		if (centreCandidatureListener != null){
			if (tableResult.getValue()==null){
				Notification.show(applicationContext.getMessage("window.search.selectrow", null, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
				return;
			}else{
				CentreCandidature ctrCand = (CentreCandidature) tableResult.getValue();
				centreCandidatureListener.btnOkClick(ctrCand);
				close();
			}					
		}
	}

	/**
	 * Défini le 'CentreCandidatureListener' utilisé
	 * @param centreCandidatureListener
	 */
	public void addCentreCandidatureListener(CentreCandidatureListener centreCandidatureListener) {
		this.centreCandidatureListener = centreCandidatureListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface CentreCandidatureListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param centre le CentreCandidature a renvoyer 
		 */
		public void btnOkClick(CentreCandidature centre);

	}

}
