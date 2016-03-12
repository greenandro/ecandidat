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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil_;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion d'affectation des droitProfil
 * @author Kevin Hergalant
 *
 */
@SpringView(name = AdminDroitProfilIndView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class AdminDroitProfilIndView extends VerticalLayout implements View, EntityPushListener<DroitProfilInd>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -6698697558567782631L;

	public static final String NAME = "adminDroitProfilIndView";

	public static final String[] USER_PROFIL_FIELDS_ORDER = {DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName(),DroitProfilInd_.individu.getName()+"."+Individu_.libelleInd.getName(),DroitProfilInd_.droitProfil.getName()+"."+DroitProfil_.codProfil.getName()};

	/* Injections */
	@Resource
	private transient Environment environment;
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient UserController userController;

	/* Composants */	
	private Button btnNouveauAdmin = new Button(FontAwesome.PLUS);
	private Button btnSupprimerAdmin = new Button(FontAwesome.TRASH_O);
	private BeanItemContainer<DroitProfilInd> containerAdmin = new BeanItemContainer<DroitProfilInd>(DroitProfilInd.class);
	private TableFormating adminTable = new TableFormating(null,containerAdmin);
	
	@Resource
	private transient EntityPusher<DroitProfilInd> droitProfilIndEntityPusher;
	

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);	
		
		Label title = new Label(applicationContext.getMessage("droitprofilind.title", null, UI.getCurrent().getLocale()));
		title.addStyleName(ValoTheme.LABEL_H2);
		addComponent(title);

		/* Boutons */
		if (userController.isAdmin()){
			HorizontalLayout buttonsLayoutAdmin = new HorizontalLayout();
			buttonsLayoutAdmin.setWidth(100, Unit.PERCENTAGE);
			buttonsLayoutAdmin.setSpacing(true);
			addComponent(buttonsLayoutAdmin);
			
			btnNouveauAdmin.setCaption(applicationContext.getMessage("droitprofilind.btnNouveau", null, UI.getCurrent().getLocale()));
			btnNouveauAdmin.addClickListener(e -> droitProfilController.addProfilToAdmin());
			buttonsLayoutAdmin.addComponent(btnNouveauAdmin);
			buttonsLayoutAdmin.setComponentAlignment(btnNouveauAdmin, Alignment.MIDDLE_LEFT);

			btnSupprimerAdmin.setCaption(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()));
			btnSupprimerAdmin.setEnabled(false);
			btnSupprimerAdmin.addClickListener(e -> {
				if (adminTable.getValue() instanceof DroitProfilInd) {
					droitProfilController.deleteProfilToUser((DroitProfilInd) adminTable.getValue());
				}
			});
			buttonsLayoutAdmin.addComponent(btnSupprimerAdmin);
			buttonsLayoutAdmin.setComponentAlignment(btnSupprimerAdmin, Alignment.MIDDLE_RIGHT);
		}

		/* Table des batchs */		
		containerAdmin.addNestedContainerProperty(DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName());
		containerAdmin.addNestedContainerProperty(DroitProfilInd_.individu.getName()+"."+Individu_.libelleInd.getName());
		containerAdmin.addNestedContainerProperty(DroitProfilInd_.droitProfil.getName()+"."+DroitProfil_.codProfil.getName());
		adminTable.setSizeFull();
		adminTable.setVisibleColumns((Object[]) USER_PROFIL_FIELDS_ORDER);
		for (String fieldName : USER_PROFIL_FIELDS_ORDER) {
			adminTable.setColumnHeader(fieldName, applicationContext.getMessage("droitprofilind.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		adminTable.setSortContainerPropertyId(DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName());
		adminTable.setColumnCollapsingAllowed(true);
		adminTable.setColumnReorderingAllowed(true);
		adminTable.setSelectable(true);
		adminTable.setImmediate(true);
		adminTable.addItemSetChangeListener(e -> adminTable.sanitizeSelection());
		
		Boolean isScolCentral = userController.isScolCentrale();
		Boolean isAdmin = userController.isAdmin();
		
		adminTable.addValueChangeListener(e -> {
			/* Les boutons d'édition, de programme et de lancement de batch sont actifs seulement si un droit est sélectionné. */
			boolean droitIsSelected = false;
			if (!(adminTable.getValue() instanceof DroitProfilInd)){
				droitIsSelected = false;
			}else if (((DroitProfilInd)adminTable.getValue()).getDroitProfil().getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH)){
				droitIsSelected = false;
			}else if (isAdmin){
				droitIsSelected = true;
			}else if (isScolCentral && ((DroitProfilInd)adminTable.getValue()).getDroitProfil().getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE)){
				droitIsSelected = true;
			}
			btnSupprimerAdmin.setEnabled(droitIsSelected);
		});
		addComponent(adminTable);
		setExpandRatio(adminTable, 1);
		
		/* Inscrit la vue aux mises à jour de droitProfil */
		droitProfilIndEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		containerAdmin.removeAllItems();
		containerAdmin.addAll(droitProfilController.getDroitProfilInds());
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
		/* Desinscrit la vue aux mises à jour de droitProfil */
		droitProfilIndEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(DroitProfilInd entity) {
		adminTable.removeItem(entity);
		adminTable.addItem(entity);
		adminTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(DroitProfilInd entity) {
		adminTable.removeItem(entity);
		adminTable.addItem(entity);
		adminTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(DroitProfilInd entity) {
		adminTable.removeItem(entity);
	}
}
