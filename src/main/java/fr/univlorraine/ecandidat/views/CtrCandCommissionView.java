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

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.controllers.CommissionController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CommissionMembre;
import fr.univlorraine.ecandidat.entities.ecandidat.CommissionMembre_;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd_;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu_;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des commissions du centre de candidature
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CtrCandCommissionView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandCommissionView extends VerticalLayout implements View, EntityPushListener<Commission>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 5704332879028671027L;

	public static final String NAME = "ctrCandCommissionView";

	public static final String[] FIELDS_ORDER = {Commission_.codComm.getName(),Commission_.libComm.getName(),Commission_.tesComm.getName()};
	public static final String[] FIELDS_ORDER_MEMBRE = {
		CommissionMembre_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName(),
		CommissionMembre_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.libelleInd.getName(),
		CommissionMembre_.temIsPresident.getName()};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CommissionController commissionController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	protected transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient EntityPusher<Commission> commissionEntityPusher;

	/* Composants */
	private CentreCandidature ctrCand;
	
	private TableFormating commissionTable = new TableFormating();
	private TableFormating commissionMembreTable = new TableFormating();
	private Button btnNewMembre  = new Button(FontAwesome.PLUS);
	private Label labelMembre = new Label();

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);
		
		/*Récupération du centre de canidature en cours*/
		SecurityCtrCandFonc securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_COMMISSION);
		if (securityCtrCandFonc==null || securityCtrCandFonc.getIdCtrCand()==null || securityCtrCandFonc.getReadOnly()==null){			
			addComponent(new Label(applicationContext.getMessage("erreurView.title", null, UI.getCurrent().getLocale())));
			return;
		}
		((MainUI)UI.getCurrent()).checkConcordanceCentreCandidature(securityCtrCandFonc.getIdCtrCand());
		ctrCand = centreCandidatureController.getCentreCandidature(securityCtrCandFonc.getIdCtrCand());
			
		/*Table des centres de candidatures*/
		VerticalLayout commissionLayout = new VerticalLayout();
		commissionLayout.setSizeFull();
		commissionLayout.setSpacing(true);
		
		/* Titre */
		Label titleParam = new Label(applicationContext.getMessage("commission.title", new Object[]{ctrCand.getLibCtrCand()}, UI.getCurrent().getLocale()));
		titleParam.addStyleName(ValoTheme.LABEL_H2);
		commissionLayout.addComponent(titleParam);
		

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		commissionLayout.addComponent(buttonsLayout);


		Button btnNew = new Button(applicationContext.getMessage("commission.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			commissionController.editNewCommission(ctrCand);
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);


		Button btnEdit = new Button(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (commissionTable.getValue() instanceof Commission) {
				commissionController.editCommission((Commission) commissionTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);
		
		Button btnDelete = new Button(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (commissionTable.getValue() instanceof Commission) {
				commissionController.deleteCommission((Commission) commissionTable.getValue());
			}			
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);


		/* Table des commissions */		
		BeanItemContainer<Commission> container = new BeanItemContainer<Commission>(Commission.class, commissionController.getCommissionsByCtrCand(ctrCand,securityCtrCandFonc.getIsGestAllCommission(),securityCtrCandFonc.getListeIdCommission()));
		commissionTable.setContainerDataSource(container);
		commissionTable.addBooleanColumn(Commission_.tesComm.getName());
		commissionTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			commissionTable.setColumnHeader(fieldName, applicationContext.getMessage("commission.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		commissionTable.setSortContainerPropertyId(Commission_.codComm.getName());
		commissionTable.setColumnCollapsingAllowed(true);
		commissionTable.setColumnReorderingAllowed(true);
		commissionTable.setSelectable(true);
		commissionTable.setImmediate(true);
		commissionTable.addItemSetChangeListener(e -> commissionTable.sanitizeSelection());
		commissionTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de commission sont actifs seulement si une commission est sélectionnée. */
			boolean commissionIsSelected = commissionTable.getValue() instanceof Commission;
			btnEdit.setEnabled(commissionIsSelected);
			btnDelete.setEnabled(commissionIsSelected);
			btnNewMembre.setEnabled(commissionIsSelected);
			if (commissionIsSelected){
				majMembreTable((Commission) commissionTable.getValue());
			}else{
				majMembreTable(null);
			}
			
		});		
		
		commissionLayout.addComponent(commissionTable);
		commissionLayout.setExpandRatio(commissionTable, 1);
		commissionTable.setSizeFull();
		addComponent(commissionLayout);
		setExpandRatio(commissionLayout, 3);
		
		/*Commission Membre*/
		VerticalLayout commissionMembreLayout = new VerticalLayout();
		commissionMembreLayout.setSizeFull();
		commissionMembreLayout.setSpacing(true);
		
		/* Titre */
		HorizontalLayout layoutMembreLabel = new HorizontalLayout();
		layoutMembreLabel.setSpacing(true);
		Label titleMembre = new Label(applicationContext.getMessage("commission.title.membre", null, UI.getCurrent().getLocale()));
		titleMembre.addStyleName(ValoTheme.LABEL_H3);
		layoutMembreLabel.addComponent(titleMembre);
		layoutMembreLabel.setComponentAlignment(titleMembre, Alignment.BOTTOM_LEFT);
		
		labelMembre.setValue(applicationContext.getMessage("commission.membre.nocomm", null, UI.getCurrent().getLocale()));
		labelMembre.addStyleName(ValoTheme.LABEL_SMALL);
		layoutMembreLabel.addComponent(labelMembre);
		layoutMembreLabel.setComponentAlignment(labelMembre, Alignment.BOTTOM_LEFT);
		
		commissionMembreLayout.addComponent(layoutMembreLabel);
		
		/* Boutons */
		HorizontalLayout buttonsLayoutMembre = new HorizontalLayout();
		buttonsLayoutMembre.setWidth(100, Unit.PERCENTAGE);
		buttonsLayoutMembre.setSpacing(true);
		commissionMembreLayout.addComponent(buttonsLayoutMembre);


		btnNewMembre.setCaption(applicationContext.getMessage("droitprofilind.btnNouveauMembre", null, UI.getCurrent().getLocale()));
		btnNewMembre.setEnabled(false);
		btnNewMembre.addClickListener(e -> {
			commissionController.addProfilToMembre((Commission) commissionTable.getValue());
		});
		buttonsLayoutMembre.addComponent(btnNewMembre);
		buttonsLayoutMembre.setComponentAlignment(btnNewMembre, Alignment.MIDDLE_LEFT);

		/*Edit profil*/
		Button btnEditMembre = new Button(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEditMembre.setEnabled(false);
		btnEditMembre.addClickListener(e -> {
			if (commissionMembreTable.getValue() instanceof CommissionMembre) {
				commissionController.updateProfilToMembre((CommissionMembre) commissionMembreTable.getValue());
			}		
		});
		buttonsLayoutMembre.addComponent(btnEditMembre);
		buttonsLayoutMembre.setComponentAlignment(btnEditMembre, Alignment.MIDDLE_CENTER);
		
		/*Delete profil*/
		
		Button btnDeleteMembre = new Button(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDeleteMembre.setEnabled(false);
		btnDeleteMembre.addClickListener(e -> {
			if (commissionMembreTable.getValue() instanceof CommissionMembre) {
				commissionController.deleteProfilToMembre((CommissionMembre) commissionMembreTable.getValue());
			}		
		});
		buttonsLayoutMembre.addComponent(btnDeleteMembre);
		buttonsLayoutMembre.setComponentAlignment(btnDeleteMembre, Alignment.MIDDLE_RIGHT);


		/* Table des CommissionMembre */
		BeanItemContainer<CommissionMembre> containerMembre = new BeanItemContainer<CommissionMembre>(CommissionMembre.class);
		containerMembre.addNestedContainerProperty(CommissionMembre_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName());
		containerMembre.addNestedContainerProperty(CommissionMembre_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.libelleInd.getName());
		commissionMembreTable.setContainerDataSource(containerMembre);
		commissionMembreTable.addBooleanColumn(CommissionMembre_.temIsPresident.getName());		
		commissionMembreTable.setVisibleColumns((Object[]) FIELDS_ORDER_MEMBRE);
		for (String fieldName : FIELDS_ORDER_MEMBRE) {
			commissionMembreTable.setColumnHeader(fieldName, applicationContext.getMessage("droit." + fieldName, null, UI.getCurrent().getLocale()));
		}
		commissionMembreTable.setSortContainerPropertyId(CommissionMembre_.droitProfilInd.getName()+"."+DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName());
		commissionMembreTable.setColumnCollapsingAllowed(true);
		commissionMembreTable.setColumnReorderingAllowed(true);
		commissionMembreTable.setSelectable(true);
		commissionMembreTable.setImmediate(true);
		commissionMembreTable.addItemSetChangeListener(e -> commissionMembreTable.sanitizeSelection());
		commissionMembreTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de commission sont actifs seulement si une commission est sélectionnée. */
			boolean membreIsSelected = commissionMembreTable.getValue() instanceof CommissionMembre;
			btnDeleteMembre.setEnabled(membreIsSelected);
			btnEditMembre.setEnabled(membreIsSelected);
		});
		
		commissionMembreLayout.addComponent(commissionMembreTable);
		commissionMembreLayout.setExpandRatio(commissionMembreTable, 1);
		commissionMembreTable.setSizeFull();
		addComponent(commissionMembreLayout);
		setExpandRatio(commissionMembreLayout, 2);
		
		/*Gestion du readOnly*/
		if (!securityCtrCandFonc.getReadOnly()){
			commissionTable.addItemClickListener(e -> {
				if (e.isDoubleClick()) {
					commissionTable.select(e.getItemId());
					btnEdit.click();
				}
			});
			commissionMembreTable.addItemClickListener(e -> {
				if (e.isDoubleClick()) {
					commissionMembreTable.select(e.getItemId());
					btnEditMembre.click();
				}
			});
			buttonsLayout.setVisible(true);
			buttonsLayoutMembre.setVisible(true);
		}else{
			buttonsLayout.setVisible(false);
			buttonsLayoutMembre.setVisible(false);
		}
		
		
		/* Inscrit la vue aux mises à jour de commission */
		commissionEntityPusher.registerEntityPushListener(this);
	}
	
	/** Met à jour la table des CommissionMembre
	 * @param ctr
	 */
	private void majMembreTable(Commission commission){
		commissionMembreTable.removeAllItems();
		if (commission != null){
			labelMembre.setValue(applicationContext.getMessage("commission.membre.comm", new Object[]{commission.getLibComm()}, UI.getCurrent().getLocale()));
			commissionMembreTable.addItems(commission.getCommissionMembres());
		}else{
			labelMembre.setValue(applicationContext.getMessage("commission.membre.nocomm", null, UI.getCurrent().getLocale()));
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
		/* Désinscrit la vue des mises à jour de commission */
		commissionEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Commission entity) {
		if (ctrCand!=null && entity.getCentreCandidature().getIdCtrCand()==ctrCand.getIdCtrCand()){
			commissionTable.removeItem(entity);
			commissionTable.addItem(entity);
			commissionTable.sort();
		}		
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Commission entity) {
		Commission commSelected = null;
		if (commissionTable.getValue() instanceof Commission) {
			commSelected = (Commission) commissionTable.getValue();
		}
		//if (entity.getCentreCandidature().equals(o))/*TODO*/
		if (ctrCand!=null && entity.getCentreCandidature().getIdCtrCand()==ctrCand.getIdCtrCand()){
			commissionTable.removeItem(entity);
			commissionTable.addItem(entity);
			commissionTable.sort();
		}
		
		if (commSelected != null && entity.getIdComm()==commSelected.getIdComm()){
			commissionTable.select(entity);
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Commission entity) {
		if (ctrCand!=null && entity.getCentreCandidature().getIdCtrCand()==ctrCand.getIdCtrCand()){
			commissionTable.removeItem(entity);
		}		
	}
}
