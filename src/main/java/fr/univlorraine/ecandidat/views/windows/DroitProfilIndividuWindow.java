package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.DemoController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.IndividuController;
import fr.univlorraine.ecandidat.controllers.LdapController;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil_;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.services.ldap.PeopleLdap;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.CustomException;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Fenêtre de recherche d'individu Ldap
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class DroitProfilIndividuWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -5129251739942956341L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LdapController ldapController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient DemoController demoController;
	
	public static final String[] PEOPLE_FIELDS_ORDER = {"uid","supannCivilite","sn","givenName","displayName"};

	/* Composants */
	private VerticalLayout infoSuppLayout;
	private HorizontalLayout searchLayout;
	private TextField searchBox;
	private Label loginModification;
	private Button btnSearch;
	private TableFormating tableResult;
	private BeanItemContainer<PeopleLdap> peopleContainer;
	private Button btnValider;
	private Button btnAnnuler;
	private ComboBox cbDroitProfil;

	/*Variable*/
	protected Boolean isModificationMode = false;
	
	/*Listener*/
	private DroitProfilIndividuListener droitProfilIndividuListener;


	/**
	 * Constructeur de la fenêtre de profil
	 */
	public DroitProfilIndividuWindow(String type) {
		List<DroitProfil> listeProfilDispo = droitProfilController.getListDroitProfilByType(type);
		
		/* Style */
		setWidth(830, Unit.PIXELS);
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
		setCaption(applicationContext.getMessage("window.search.people.title", null, Locale.getDefault()));
		
		/*Commande layout*/
		HorizontalLayout commandeLayout = new HorizontalLayout();
		commandeLayout.setWidth(100,Unit.PERCENTAGE);
		
		/* Recherche */		
		searchBox = new TextField();
		searchBox.addShortcutListener(new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {

			/** serialVersionUID **/
			private static final long serialVersionUID = 4119756957960484247L;

			@Override
		    public void handleAction(Object sender, Object target) {
		    	performSearch();
		    }
		});
		
		loginModification = new Label("",ContentMode.HTML);
		loginModification.setVisible(false);
		layout.addComponent(loginModification);

		btnSearch = new Button(applicationContext.getMessage("window.search", null, Locale.getDefault()));
		btnSearch.addClickListener(e->performSearch());
		
		searchLayout = new HorizontalLayout();
		searchLayout.setSpacing(true);
		searchLayout.addComponent(searchBox);
		searchLayout.addComponent(btnSearch);
		
		/*DroitLayout*/
		HorizontalLayout droitLayout = new HorizontalLayout();
		droitLayout.setSpacing(true);
		Label labelDroit  = new Label(applicationContext.getMessage("window.search.profil", null, Locale.getDefault()));
		droitLayout.addComponent(labelDroit);
		droitLayout.setComponentAlignment(labelDroit, Alignment.MIDDLE_RIGHT);

		BeanItemContainer<DroitProfil> container = new BeanItemContainer<DroitProfil>(DroitProfil.class,listeProfilDispo);
		cbDroitProfil = new ComboBox();
		cbDroitProfil.setTextInputAllowed(false);
		cbDroitProfil.setContainerDataSource(container);
		cbDroitProfil.setNullSelectionAllowed(false);
		cbDroitProfil.setImmediate(true);
		cbDroitProfil.setItemCaptionPropertyId(DroitProfil_.codProfil.getName());
		cbDroitProfil.setValue(listeProfilDispo.get(0));
		
		droitLayout.addComponent(cbDroitProfil);
		droitLayout.setComponentAlignment(labelDroit, Alignment.MIDDLE_LEFT);
		
		/*Login Apogee pour les gestionnaires*/
		infoSuppLayout = new VerticalLayout();
		infoSuppLayout.setSpacing(true);
		infoSuppLayout.setVisible(false);
		Label labelInfoSuppLayout = new Label(applicationContext.getMessage("window.search.people.option", null, Locale.getDefault()),ContentMode.HTML);
		labelInfoSuppLayout.addStyleName(ValoTheme.LABEL_H4);
		labelInfoSuppLayout.addStyleName(ValoTheme.LABEL_COLORED);
		infoSuppLayout.addComponent(labelInfoSuppLayout);
		infoSuppLayout.setVisible(false);
		
		
		/*Ajout des commandes*/
		commandeLayout.addComponent(searchLayout);
		commandeLayout.addComponent(droitLayout);		
		layout.addComponent(commandeLayout);
		
		/* Table de Resultat de recherche*/
		peopleContainer = new BeanItemContainer<PeopleLdap>(PeopleLdap.class);
		tableResult = new TableFormating(null, peopleContainer);
		
		String[] columnHeadersHarp = new String[PEOPLE_FIELDS_ORDER.length];
		for (int fieldIndex = 0; fieldIndex < PEOPLE_FIELDS_ORDER.length; fieldIndex++){
			columnHeadersHarp[fieldIndex] = applicationContext.getMessage("window.search.people."+PEOPLE_FIELDS_ORDER[fieldIndex], null, Locale.getDefault());
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
			/* Le bouton d'enregistrement est actif seulement si un people est sélectionné. */
			boolean peopleIsSelected = tableResult.getValue() instanceof PeopleLdap;
			btnValider.setEnabled(peopleIsSelected);
		});
		
		HorizontalLayout tableLayout = new HorizontalLayout();
		tableLayout.setSpacing(true);
		tableLayout.setSizeFull();
		infoSuppLayout.setSizeUndefined();
		tableLayout.addComponent(tableResult);
		//tableLayout.setExpandRatio(tableResult, 1.0f);
		tableLayout.addComponent(infoSuppLayout);
		tableLayout.setExpandRatio(tableResult, 1.0f);
		
		layout.addComponent(tableLayout);
		layout.setExpandRatio(tableLayout, 1.0f);

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new Button(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);
		
		btnValider = new Button(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
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
	
	/** Passe en mode modif
	 * @param droitProfilInd le profil a modifier
	 */
	protected void switchToModifMode(DroitProfilInd droitProfilInd){
		isModificationMode = true;
		setCaption(applicationContext.getMessage("window.search.people.title.mod", null, Locale.getDefault()));
		setWidth(350, Unit.PIXELS);
		searchLayout.setVisible(false);
		loginModification.setVisible(true);
		tableResult.setVisible(false);
		btnValider.setEnabled(true);
		
		cbDroitProfil.setValue(droitProfilInd.getDroitProfil());
		loginModification.setValue("Login : <b>"+droitProfilInd.getIndividu().getLoginInd()+"</b>");		
	}
	
	/** ajoute une option au layout d'options
	 * @param c le composant
	 */
	protected void addOption(Component c){
		addOption(c, Alignment.MIDDLE_RIGHT);
	}
	
	/** ajoute une option alignée au layout d'options
	 * @param c le composant
	 */
	protected void addOption(Component c, Alignment align){
		infoSuppLayout.addComponent(c);
		infoSuppLayout.setComponentAlignment(c, align);
		if (!infoSuppLayout.isVisible()){
			infoSuppLayout.setVisible(true);
		}
		center();
	}
	
	
	/**
	 * Effectue la recherche
	 */
	private void performSearch(){
		if (searchBox.getValue().equals(null) || searchBox.getValue().equals("") || searchBox.getValue().length()<ConstanteUtils.NB_MIN_CAR_PERS){
			Notification.show(applicationContext.getMessage("window.search.morethan", new Object[]{ConstanteUtils.NB_MIN_CAR_PERS}, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
		}else{
			peopleContainer.removeAllItems();
			if (demoController.getDemoMode()){
				peopleContainer.addAll(demoController.findListIndividuLdapDemo());
			}else{
				peopleContainer.addAll(ldapController.getPeopleByFilter(searchBox.getValue()));
			}			
		}
	}
	
	/**
	 * Vérifie les données et si c'est ok, fait l'action du listener
	 */
	protected void performAction(){
		if (droitProfilIndividuListener != null && checkData()){
			Individu individu = getIndividu();
			DroitProfil droit = getDroitProfil();
			if (individu!=null && droit!=null){
				droitProfilIndividuListener.btnOkClick(individu,droit);
				close();
			}				
		}
	}
	
	/**
	 * @return true si les données sont bonnes
	 */
	protected Boolean checkData(){
		Object valPeople = tableResult.getValue();
		Object valDroit = cbDroitProfil.getValue();
		if (!isModificationMode && (valPeople==null || !(valPeople instanceof PeopleLdap))){
			Notification.show(applicationContext.getMessage("window.search.selectrow", null, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
			return false;
		}else if (valDroit==null || !(valDroit instanceof DroitProfil)){
			Notification.show(applicationContext.getMessage("window.search.noright", null, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
			return false;
		}else{
			return true;
		}
	}
	
	/** Renvoi l'individu construit a partir du people Ldap
	 * @return l'individu
	 */
	protected Individu getIndividu(){
		if (isModificationMode){
			return null;
		}else{
			PeopleLdap people = (PeopleLdap) tableResult.getValue();
			Individu individu = new Individu(people);
			try {
				individuController.validateIndividuBean(individu);
				return individu;
			} catch (CustomException e) {
				Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
				return null;
			}
		}		
	}
	
	protected DroitProfil getDroitProfil(){
		DroitProfil droit = (DroitProfil) cbDroitProfil.getValue();
		return droit;
	}
	
	/**  Défini le 'DroitProfilIndividuListener' utilisé
	 * @param droitProfilIndividuListener
	 */
	public void addDroitProfilIndividuListener(DroitProfilIndividuListener droitProfilIndividuListener) {
		this.droitProfilIndividuListener = droitProfilIndividuListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface DroitProfilIndividuListener extends Serializable {
		
		/** Appelé lorsque Oui est cliqué.
		 * @param individu
		 * @param droit
		 */
		public void btnOkClick(Individu individu, DroitProfil droit);

	}

}
