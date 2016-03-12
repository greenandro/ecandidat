package fr.univlorraine.ecandidat.views;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
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

import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des droitProfil
 * @author Kevin Hergalant
 *
 */
@SpringView(name = AdminDroitProfilView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class AdminDroitProfilView extends VerticalLayout implements View, EntityPushListener<DroitProfil>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 5323335563718769860L;

	public static final String NAME = "adminDroitProfilView";

	public static final String[] DROIT_PROFIL_FIELDS_ORDER = {DroitProfil_.codProfil.getName(),DroitProfil_.libProfil.getName(),"fonctionnalite"};
	
	/* Injections */
	@Resource
	private transient Environment environment;
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient DroitProfilController droitProfilController;

	/* Composants */
	private Button btnNouveauProfil = new Button(FontAwesome.PLUS);
	private Button btnEditProfil = new Button(FontAwesome.PENCIL);
	private Button btnSupprimerProfil = new Button(FontAwesome.TRASH_O);
	private BeanItemContainer<DroitProfil> containerProfil = new BeanItemContainer<DroitProfil>(DroitProfil.class);
	private TableFormating droitProfilTable = new TableFormating(null,containerProfil);
	
	@Resource
	private transient EntityPusher<DroitProfil> droitProfilEntityPusher;
	

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
		Label title = new Label(applicationContext.getMessage("droitprofil.title", null, UI.getCurrent().getLocale()));
		title.addStyleName(ValoTheme.LABEL_H2);
		addComponent(title);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		HorizontalLayout leftButtonsLayout = new HorizontalLayout();
		leftButtonsLayout.setSpacing(true);
		buttonsLayout.addComponent(leftButtonsLayout);
		buttonsLayout.setComponentAlignment(leftButtonsLayout, Alignment.MIDDLE_LEFT);


		btnNouveauProfil.setCaption(applicationContext.getMessage("droitprofil.btnNouveau", null, UI.getCurrent().getLocale()));
		btnNouveauProfil.addClickListener(e -> droitProfilController.editNewDroitProfil());
		leftButtonsLayout.addComponent(btnNouveauProfil);

		btnEditProfil.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEditProfil.setEnabled(false);
		btnEditProfil.addClickListener(e -> {
			if (droitProfilTable.getValue() instanceof DroitProfil) {
				droitProfilController.editDroitProfil((DroitProfil) droitProfilTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEditProfil);
		buttonsLayout.setComponentAlignment(btnEditProfil, Alignment.MIDDLE_CENTER);

		btnSupprimerProfil.setCaption(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()));
		btnSupprimerProfil.setEnabled(false);
		btnSupprimerProfil.addClickListener(e -> {
			if (droitProfilTable.getValue() instanceof DroitProfil) {
				droitProfilController.deleteDroitProfil((DroitProfil) droitProfilTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnSupprimerProfil);
		buttonsLayout.setComponentAlignment(btnSupprimerProfil, Alignment.MIDDLE_RIGHT);

		/* Table des batchs */		
		droitProfilTable.addGeneratedColumn("fonctionnalite", new ColumnGenerator() {
			
			/*** serialVersionUID*/
			private static final long serialVersionUID = 7461290324017459118L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final DroitProfil profil = (DroitProfil) itemId;
				if (profil.getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_ADMIN)){
					return applicationContext.getMessage("droitprofil.descriptif.admin", null, UI.getCurrent().getLocale());
				}else if (profil.getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE)){
					return applicationContext.getMessage("droitprofil.descriptif.scol", null, UI.getCurrent().getLocale());
				}else if (profil.getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_COMMISSION)){
					return applicationContext.getMessage("droitprofil.descriptif.commission", null, UI.getCurrent().getLocale());
				}
				String fonc = "";
				for (DroitProfilFonc droit : profil.getDroitProfilFoncs()){
					fonc += droit.getDroitFonctionnalite().getLibFonc();
					if (droit.getTemReadOnly()){
						fonc += " (LS)";
					}
					fonc += ", ";
				}
				if (!fonc.equals("")){
					fonc = fonc.substring(0, fonc.length()-2);
				}
				return new Label(fonc);
			}
		});
		droitProfilTable.setSizeFull();
		droitProfilTable.setVisibleColumns((Object[]) DROIT_PROFIL_FIELDS_ORDER);
		for (String fieldName : DROIT_PROFIL_FIELDS_ORDER) {
			droitProfilTable.setColumnHeader(fieldName, applicationContext.getMessage("droitprofil.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		droitProfilTable.setSortContainerPropertyId(DroitProfil_.codProfil.getName());
		droitProfilTable.setColumnCollapsingAllowed(true);
		droitProfilTable.setColumnReorderingAllowed(true);
		droitProfilTable.setSelectable(true);
		droitProfilTable.setImmediate(true);
		droitProfilTable.addItemSetChangeListener(e -> droitProfilTable.sanitizeSelection());
		droitProfilTable.addValueChangeListener(e -> {
			/* Les boutons d'édition, de programme et de lancement de batch sont actifs seulement si un droit est sélectionné. */
			boolean droitIsSelected = droitProfilTable.getValue() instanceof DroitProfil && ((DroitProfil) droitProfilTable.getValue()).getTemUpdatable();
			btnEditProfil.setEnabled(droitIsSelected);
			btnSupprimerProfil.setEnabled(droitIsSelected);
		});
		droitProfilTable.addItemClickListener(e -> {
			if (e.isDoubleClick() && droitProfilTable.getValue() instanceof DroitProfil && ((DroitProfil) droitProfilTable.getValue()).getTemUpdatable()) {
				droitProfilTable.select(e.getItemId());
				btnEditProfil.click();
			}
		});
		addComponent(droitProfilTable);
		setExpandRatio(droitProfilTable, 1);
		
		/* Inscrit la vue aux mises à jour de droitProfil */
		droitProfilEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		containerProfil.removeAllItems();
		containerProfil.addAll(droitProfilController.getDroitProfils());		
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
		/* Desinscrit la vue aux mises à jour de droitProfil */
		droitProfilEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(DroitProfil entity) {
		droitProfilTable.removeItem(entity);
		droitProfilTable.addItem(entity);
		droitProfilTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(DroitProfil entity) {
		droitProfilTable.removeItem(entity);
		droitProfilTable.addItem(entity);
		droitProfilTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(DroitProfil entity) {
		droitProfilTable.removeItem(entity);
	}
}
