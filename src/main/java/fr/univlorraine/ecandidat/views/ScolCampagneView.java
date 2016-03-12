package fr.univlorraine.ecandidat.views;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CampagneController;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des campagnes par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolCampagneView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolCampagneView extends VerticalLayout implements View, EntityPushListener<Campagne>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -5028458455076407921L;

	public static final String NAME = "scolCampagneView";

	public static final String[] FIELDS_ORDER = {Campagne_.codCamp.getName(),Campagne_.libCamp.getName(),Campagne_.datDebCamp.getName(),Campagne_.datFinCamp.getName(),
		Campagne_.tesCamp.getName(),Campagne_.datActivatPrevCamp.getName(),Campagne_.datActivatEffecCamp.getName(),Campagne_.datArchivCamp.getName()
		,"datDestructPrevCamp",Campagne_.datDestructEffecCamp.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient EntityPusher<Campagne> campagneEntityPusher;
	@Resource
	private transient DateTimeFormatter formatterDateTime;
	/* Composants */
	private TableFormating campagneTable = new TableFormating();

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);
		
		/* Titre */
		Label titleParam = new Label(applicationContext.getMessage("campagne.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(ValoTheme.LABEL_H2);
		addComponent(titleParam);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);


		Button btnNew = new Button(applicationContext.getMessage("campagne.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			campagneController.editNewCampagne();
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);


		Button btnEdit = new Button(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (campagneTable.getValue() instanceof Campagne) {
				campagneController.editCampagne((Campagne) campagneTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);
		
		Button btnDelete = new Button(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (campagneTable.getValue() instanceof Campagne) {
				campagneController.deleteCampagne((Campagne) campagneTable.getValue());
			}			
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);


		/* Table des campagnes */
		BeanItemContainer<Campagne> container = new BeanItemContainer<Campagne>(Campagne.class, campagneController.getCampagnes());
		campagneTable.setContainerDataSource(container);
		campagneTable.addGeneratedColumn("datDestructPrevCamp", new ColumnGenerator() {
			
			/**serialVersionUID**/
			private static final long serialVersionUID = 2879199368184203393L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				LocalDateTime date = campagneController.getDateDestructionDossier((Campagne) itemId);
				if (date!=null){
					return formatterDateTime.format(date);
				}						
				return null; 
			}
		});
		
		
		campagneTable.addBooleanColumn(Campagne_.tesCamp.getName());
		campagneTable.setSizeFull();
		campagneTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			campagneTable.setColumnHeader(fieldName, applicationContext.getMessage("campagne.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		campagneTable.setSortContainerPropertyId(Campagne_.codCamp.getName());
		campagneTable.setSortAscending(false);
		campagneTable.setColumnCollapsingAllowed(true);
		campagneTable.setColumnReorderingAllowed(true);
		campagneTable.setSelectable(true);
		campagneTable.setImmediate(true);
		campagneTable.addItemSetChangeListener(e -> campagneTable.sanitizeSelection());
		campagneTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de campagne sont actifs seulement si une campagne est sélectionnée. */
			boolean campagneIsSelected = campagneTable.getValue() instanceof Campagne;
			btnEdit.setEnabled(campagneIsSelected);
			btnDelete.setEnabled(campagneIsSelected);
		});
		campagneTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				campagneTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(campagneTable);
		setExpandRatio(campagneTable, 1);
		
		/* Inscrit la vue aux mises à jour de campagne */
		campagneEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de campagne */
		campagneEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Campagne entity) {
		campagneTable.removeItem(entity);
		campagneTable.addItem(entity);
		campagneTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Campagne entity) {
		campagneTable.removeItem(entity);
		campagneTable.addItem(entity);
		campagneTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Campagne entity) {
		campagneTable.removeItem(entity);
	}
}
