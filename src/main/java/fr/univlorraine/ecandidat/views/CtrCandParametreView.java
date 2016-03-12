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
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des parametres du centre de candidature
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CtrCandParametreView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandParametreView extends VerticalLayout implements View, EntityPushListener<CentreCandidature>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 6636733484208381133L;

	public static final String NAME = "ctrCandParametreView";

	public static final String[] FIELDS_ORDER = {SimpleTablePresentation.champsTitle,SimpleTablePresentation.champsValue};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient EntityPusher<CentreCandidature> centreCandidatureEntityPusher;
	
	/* Composants */
	private CentreCandidature ctrCand;
	private BeanItemContainer<SimpleTablePresentation> containerReadOnly = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	private BeanItemContainer<SimpleTablePresentation> container = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	
	

	/* Composants */

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
		SecurityCtrCandFonc securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_PARAM);
		if (securityCtrCandFonc==null || securityCtrCandFonc.getIdCtrCand()==null || securityCtrCandFonc.getReadOnly()==null){			
			addComponent(new Label(applicationContext.getMessage("erreurView.title", null, UI.getCurrent().getLocale())));
			return;
		}
		((MainUI)UI.getCurrent()).checkConcordanceCentreCandidature(securityCtrCandFonc.getIdCtrCand());
		ctrCand = centreCandidatureController.getCentreCandidature(securityCtrCandFonc.getIdCtrCand());
		
		/* Titre */
		Label titleParam = new Label(applicationContext.getMessage("ctrCand.parametre.title", new Object[]{ctrCand.getLibCtrCand()}, UI.getCurrent().getLocale()));
		titleParam.addStyleName(ValoTheme.LABEL_H2);
		addComponent(titleParam);
		
		/*Descriptif*/
		
		Label titleParamDesc = new Label(applicationContext.getMessage("ctrCand.parametre.title.desc", null, UI.getCurrent().getLocale()));
		titleParamDesc.addStyleName(ValoTheme.LABEL_H3);
		addComponent(titleParamDesc);

		containerReadOnly = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
		TableFormating paramReadOnlyTable = new TableFormating(null, containerReadOnly);
		paramReadOnlyTable.addBooleanColumn(SimpleTablePresentation.champsValue,false);
		paramReadOnlyTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		paramReadOnlyTable.setColumnCollapsingAllowed(false);
		paramReadOnlyTable.setColumnReorderingAllowed(false);
		paramReadOnlyTable.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		paramReadOnlyTable.setSelectable(false);
		paramReadOnlyTable.setImmediate(true);
		paramReadOnlyTable.setPageLength(3);
		paramReadOnlyTable.setColumnWidth(SimpleTablePresentation.champsTitle, 300);
		paramReadOnlyTable.setCellStyleGenerator((components, itemId, columnId)->{
			if (columnId!=null && columnId.equals(SimpleTablePresentation.champsTitle)){
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		addComponent(paramReadOnlyTable);
		
		/*Parametres*/
		
		/* Boutons */		
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);
		
		Label titleParamParam = new Label(applicationContext.getMessage("ctrCand.parametre.title.param", null, UI.getCurrent().getLocale()));
		titleParamParam.setSizeUndefined();		
		titleParamParam.addStyleName(ValoTheme.LABEL_H3);
		buttonsLayout.addComponent(titleParamParam);
		buttonsLayout.setComponentAlignment(titleParamParam, Alignment.MIDDLE_CENTER);

		Button btnEdit = new Button(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.addClickListener(e -> {
			centreCandidatureController.editCentreCandidature(ctrCand,false);
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setExpandRatio(btnEdit, 1);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);
		
		TableFormating paramTable = new TableFormating(null, container);
		paramTable.addBooleanColumn(SimpleTablePresentation.champsValue,false);
		paramTable.setSizeFull();
		paramTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		paramTable.setColumnCollapsingAllowed(false);
		paramTable.setColumnReorderingAllowed(false);
		paramTable.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		paramTable.setSelectable(false);
		paramTable.setImmediate(true);
		paramTable.setPageLength(11);
		paramTable.setColumnWidth(SimpleTablePresentation.champsTitle, 300);
		paramTable.setCellStyleGenerator((components, itemId, columnId)->{
			if (columnId!=null && columnId.equals(SimpleTablePresentation.champsTitle)){
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		addComponent(paramTable);
		
		miseAJourContainer(ctrCand);
		setExpandRatio(paramTable, 1);
		
		/*Gestion du readOnly*/
		if (!securityCtrCandFonc.getReadOnly()){
			buttonsLayout.setVisible(true);
		}else{
			buttonsLayout.setVisible(false);
		}
		
		/* Inscrit la vue aux mises à jour de centreCandidature */
		centreCandidatureEntityPusher.registerEntityPushListener(this);
	}
	
	/** Met a jour le container
	 * @param ctrCand
	 */
	private void miseAJourContainer(CentreCandidature ctrCand){
		containerReadOnly.removeAllItems();		
		container.removeAllItems();
		if (ctrCand != null){
			containerReadOnly.addAll(centreCandidatureController.getListPresentation(ctrCand,true));
			container.addAll(centreCandidatureController.getListPresentation(ctrCand,false));
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
		
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(CentreCandidature entity) {
		if (ctrCand!=null && entity.getIdCtrCand() == ctrCand.getIdCtrCand()){
			ctrCand = entity;
			miseAJourContainer(entity);
		}		
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(CentreCandidature entity) {
		miseAJourContainer(null);
	}
}
