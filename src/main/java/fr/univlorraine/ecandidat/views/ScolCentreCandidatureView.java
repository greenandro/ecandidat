package fr.univlorraine.ecandidat.views;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil_;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire_;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des centre de candidatures par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolCentreCandidatureView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolCentreCandidatureView extends VerticalLayout implements View, EntityPushListener<CentreCandidature>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 6636733484208381133L;

	public static final String NAME = "scolCentreCandidatureView";

	public static final String[] FIELDS_ORDER = {CentreCandidature_.codCtrCand.getName(),CentreCandidature_.libCtrCand.getName(),CentreCandidature_.tesCtrCand.getName(), CentreCandidature_.nbMaxVoeuxCtrCand.getName(),CentreCandidature_.temDematCtrCand.getName()};
	public static final String[] FIELDS_ORDER_GEST_APOGEE = {
		Gestionnaire_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName(),
		Gestionnaire_.loginApoGest.getName(),
		Gestionnaire_.siScolCentreGestion.getName()+"."+SiScolCentreGestion_.libCge.getName(),
		Gestionnaire_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.libelleInd.getName(),
		Gestionnaire_.droitProfilInd.getName()+"."+DroitProfilInd_.droitProfil.getName()+"."+DroitProfil_.libProfil.getName()};
	
	public static final String[] FIELDS_ORDER_GEST_SI_SCOL = {
		Gestionnaire_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName(),
		Gestionnaire_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.libelleInd.getName(),
		Gestionnaire_.droitProfilInd.getName()+"."+DroitProfilInd_.droitProfil.getName()+"."+DroitProfil_.libProfil.getName()};
	
	public String[] FIELDS_ORDER_GEST;
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient EntityPusher<CentreCandidature> centreCandidatureEntityPusher;

	/* Composants */
	private TableFormating centreCandidatureTable = new TableFormating();
	private TableFormating centreCandidatureGestTable = new TableFormating();
	private Button btnNewGest = new Button(FontAwesome.PLUS);
	private Label labelCtrCandGest = new Label();

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);
		
		if (parametreController.getSIScolMode().equals(ConstanteUtils.SI_SCOL_APOGEE)){
			FIELDS_ORDER_GEST = FIELDS_ORDER_GEST_APOGEE;
		}else{
			FIELDS_ORDER_GEST = FIELDS_ORDER_GEST_SI_SCOL;
		}
				
		/*Table des centres de candidatures*/
		VerticalLayout ctrCandLayout = new VerticalLayout();
		ctrCandLayout.setSizeFull();
		ctrCandLayout.setSpacing(true);
		
		/* Titre */
		Label titleParam = new Label(applicationContext.getMessage("ctrCand.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(ValoTheme.LABEL_H2);
		ctrCandLayout.addComponent(titleParam);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		ctrCandLayout.addComponent(buttonsLayout);


		Button btnNew = new Button(applicationContext.getMessage("ctrCand.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			centreCandidatureController.editNewCentreCandidature();
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);


		Button btnEdit = new Button(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (centreCandidatureTable.getValue() instanceof CentreCandidature) {
				centreCandidatureController.editCentreCandidature((CentreCandidature) centreCandidatureTable.getValue(),true);
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);
		
		Button btnDelete = new Button(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (centreCandidatureTable.getValue() instanceof CentreCandidature) {
				centreCandidatureController.deleteCentreCandidature((CentreCandidature) centreCandidatureTable.getValue());
			}			
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);


		/* Table des centreCandidatures */
		BeanItemContainer<CentreCandidature> container = new BeanItemContainer<CentreCandidature>(CentreCandidature.class, centreCandidatureController.getCentreCandidatures());
		centreCandidatureTable.setContainerDataSource(container);
		centreCandidatureTable.addBooleanColumn(CentreCandidature_.tesCtrCand.getName());
		centreCandidatureTable.addBooleanColumn(CentreCandidature_.temDematCtrCand.getName());
		
		centreCandidatureTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			centreCandidatureTable.setColumnHeader(fieldName, applicationContext.getMessage("ctrCand.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		centreCandidatureTable.setSortContainerPropertyId(CentreCandidature_.codCtrCand.getName());
		centreCandidatureTable.setColumnCollapsingAllowed(true);
		centreCandidatureTable.setColumnReorderingAllowed(true);
		centreCandidatureTable.setSelectable(true);
		centreCandidatureTable.setImmediate(true);
		centreCandidatureTable.addItemSetChangeListener(e -> centreCandidatureTable.sanitizeSelection());
		centreCandidatureTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de centreCandidature sont actifs seulement si une centreCandidature est sélectionnée. */
			boolean centreCandidatureIsSelected = centreCandidatureTable.getValue() instanceof CentreCandidature;
			btnEdit.setEnabled(centreCandidatureIsSelected);
			btnDelete.setEnabled(centreCandidatureIsSelected);
			btnNewGest.setEnabled(centreCandidatureIsSelected);
			if (centreCandidatureIsSelected){
				majGestionnaireTable((CentreCandidature) centreCandidatureTable.getValue());
			}else{
				majGestionnaireTable(null);
			}
			
		});
		centreCandidatureTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				centreCandidatureTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		ctrCandLayout.addComponent(centreCandidatureTable);
		ctrCandLayout.setExpandRatio(centreCandidatureTable, 1);
		centreCandidatureTable.setSizeFull();
		addComponent(ctrCandLayout);
		setExpandRatio(ctrCandLayout, 3);
		
		/*Gestionnaires*/
		VerticalLayout ctrCandGestLayout = new VerticalLayout();
		ctrCandGestLayout.setSizeFull();
		ctrCandGestLayout.setSpacing(true);
		
		/* Titre */		
		HorizontalLayout layoutCtrGestLabel = new HorizontalLayout();
		layoutCtrGestLabel.setSpacing(true);
		Label titleGest = new Label(applicationContext.getMessage("ctrCand.title.gest", null, UI.getCurrent().getLocale()));
		titleGest.addStyleName(ValoTheme.LABEL_H3);
		layoutCtrGestLabel.addComponent(titleGest);
		layoutCtrGestLabel.setComponentAlignment(titleGest, Alignment.BOTTOM_LEFT);
		
		labelCtrCandGest.setValue(applicationContext.getMessage("ctrCand.gest.noctrCand", null, UI.getCurrent().getLocale()));
		labelCtrCandGest.addStyleName(ValoTheme.LABEL_SMALL);
		layoutCtrGestLabel.addComponent(labelCtrCandGest);
		layoutCtrGestLabel.setComponentAlignment(labelCtrCandGest, Alignment.BOTTOM_LEFT);
		
		ctrCandGestLayout.addComponent(layoutCtrGestLabel);
		
		/* Boutons */
		HorizontalLayout buttonsLayoutGest = new HorizontalLayout();
		buttonsLayoutGest.setWidth(100, Unit.PERCENTAGE);
		buttonsLayoutGest.setSpacing(true);
		ctrCandGestLayout.addComponent(buttonsLayoutGest);


		btnNewGest.setCaption(applicationContext.getMessage("droitprofilind.btnNouveauGest", null, UI.getCurrent().getLocale()));
		btnNewGest.setEnabled(false);
		btnNewGest.addClickListener(e -> {
			centreCandidatureController.addProfilToGestionnaire((CentreCandidature) centreCandidatureTable.getValue());
		});
		buttonsLayoutGest.addComponent(btnNewGest);
		buttonsLayoutGest.setComponentAlignment(btnNewGest, Alignment.MIDDLE_LEFT);

		/*Edit profil*/
		Button btnEditGest = new Button(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEditGest.setEnabled(false);
		btnEditGest.addClickListener(e -> {
			if (centreCandidatureGestTable.getValue() instanceof Gestionnaire) {
				centreCandidatureController.updateProfilToGestionnaire((Gestionnaire) centreCandidatureGestTable.getValue());
			}		
		});
		buttonsLayoutGest.addComponent(btnEditGest);
		buttonsLayoutGest.setComponentAlignment(btnEditGest, Alignment.MIDDLE_CENTER);
		
		/*Delete profil*/
		Button btnDeleteGest = new Button(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDeleteGest.setEnabled(false);
		btnDeleteGest.addClickListener(e -> {
			if (centreCandidatureGestTable.getValue() instanceof Gestionnaire) {
				centreCandidatureController.deleteProfilToGestionnaire((Gestionnaire) centreCandidatureGestTable.getValue());
			}		
		});
		buttonsLayoutGest.addComponent(btnDeleteGest);
		buttonsLayoutGest.setComponentAlignment(btnDeleteGest, Alignment.MIDDLE_RIGHT);


		/* Table des gestionnaires */
		BeanItemContainer<Gestionnaire> containerGest = new BeanItemContainer<Gestionnaire>(Gestionnaire.class);
		containerGest.addNestedContainerProperty(Gestionnaire_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName());
		containerGest.addNestedContainerProperty(Gestionnaire_.siScolCentreGestion.getName()+"."+SiScolCentreGestion_.libCge.getName());		
		containerGest.addNestedContainerProperty(Gestionnaire_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.libelleInd.getName());
		containerGest.addNestedContainerProperty(Gestionnaire_.droitProfilInd.getName()+"."+DroitProfilInd_.droitProfil.getName()+"."+DroitProfil_.libProfil.getName());
		centreCandidatureGestTable.setContainerDataSource(containerGest);
		centreCandidatureGestTable.setVisibleColumns((Object[]) FIELDS_ORDER_GEST);
		for (String fieldName : FIELDS_ORDER_GEST) {
			centreCandidatureGestTable.setColumnHeader(fieldName, applicationContext.getMessage("droit." + fieldName, null, UI.getCurrent().getLocale()));
		}
		centreCandidatureGestTable.setSortContainerPropertyId(Gestionnaire_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName());
		centreCandidatureGestTable.setColumnCollapsingAllowed(true);
		centreCandidatureGestTable.setColumnReorderingAllowed(true);
		centreCandidatureGestTable.setSelectable(true);
		centreCandidatureGestTable.setImmediate(true);
		centreCandidatureGestTable.addItemSetChangeListener(e -> centreCandidatureGestTable.sanitizeSelection());
		centreCandidatureGestTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de centreCandidature sont actifs seulement si une centreCandidature est sélectionnée. */
			boolean gestIsSelected = centreCandidatureGestTable.getValue() instanceof Gestionnaire;
			btnDeleteGest.setEnabled(gestIsSelected);
			btnEditGest.setEnabled(gestIsSelected);
		});
		
		centreCandidatureGestTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				centreCandidatureGestTable.select(e.getItemId());
				btnEditGest.click();
			}
		});
		
		ctrCandGestLayout.addComponent(centreCandidatureGestTable);
		ctrCandGestLayout.setExpandRatio(centreCandidatureGestTable, 1);
		centreCandidatureGestTable.setSizeFull();
		addComponent(ctrCandGestLayout);
		setExpandRatio(ctrCandGestLayout, 2);
		
		
		/* Inscrit la vue aux mises à jour de centreCandidature */
		centreCandidatureEntityPusher.registerEntityPushListener(this);
	}
	
	/** Met à jour la table des gestionnaires
	 * @param ctr
	 */
	private void majGestionnaireTable(CentreCandidature ctr){
		centreCandidatureGestTable.removeAllItems();
		if (ctr != null){
			labelCtrCandGest.setValue(applicationContext.getMessage("ctrCand.gest.ctrCand", new Object[]{ctr.getLibCtrCand()}, UI.getCurrent().getLocale()));
			centreCandidatureGestTable.addItems(ctr.getGestionnaires());
		}else{
			labelCtrCandGest.setValue(applicationContext.getMessage("ctrCand.gest.noctrCand", null, UI.getCurrent().getLocale()));
		}
	}
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de centreCandidature */
		centreCandidatureEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(CentreCandidature entity) {
		centreCandidatureTable.removeItem(entity);
		centreCandidatureTable.addItem(entity);
		centreCandidatureTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(CentreCandidature entity) {
		CentreCandidature ctrSelected = null;
		if (centreCandidatureTable.getValue() instanceof CentreCandidature) {
			ctrSelected = (CentreCandidature) centreCandidatureTable.getValue();
		}
		centreCandidatureTable.removeItem(entity);
		centreCandidatureTable.addItem(entity);
		centreCandidatureTable.sort();
		if (ctrSelected != null && entity.getIdCtrCand()==ctrSelected.getIdCtrCand()){
			centreCandidatureTable.select(entity);
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(CentreCandidature entity) {
		centreCandidatureTable.removeItem(entity);
	}
}
