package fr.univlorraine.ecandidat.views;

import java.util.List;

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

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatAdminListener;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Page d'administration d'un candidat
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CandidatAdminView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CandidatAdminView extends VerticalLayout implements View, CandidatAdminListener{	

	/** serialVersionUID **/
	private static final long serialVersionUID = 5842232696061936906L;

	public static final String NAME = "candidatAdminView";

	public static final String[] FIELDS_ORDER = {SimpleTablePresentation.champsTitle,SimpleTablePresentation.champsValue};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient ParametreController parametreController;
	
	/* Composants d'affichage des données*/
	private BeanItemContainer<SimpleTablePresentation> container = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	private TableFormating table = new TableFormating(null, container);
	
	/* Composants d'erreur*/
	private VerticalLayout globalLayout = new VerticalLayout();
	private Label errorLabel = new Label();
	private Label lockLabel = new Label();
	
	/*Titre et actions*/
	private HorizontalLayout buttonsLayout = new HorizontalLayout();	
	private Label title = new Label();
	
	
	private CompteMinima cptMin;	

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		setMargin(true);
		setSpacing(true);
		
		globalLayout.setSizeFull();
		globalLayout.setSpacing(true);
		addComponent(globalLayout);
		addComponent(errorLabel);
						
		/* Titre */		
		title.addStyleName(ValoTheme.LABEL_H2);
		globalLayout.addComponent(title);
		
		/* Lock */
		lockLabel.addStyleName(ValoTheme.LABEL_FAILURE);
		lockLabel.setVisible(false);
		globalLayout.addComponent(lockLabel);
		
		/* Boutons candidatures*/
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		globalLayout.addComponent(buttonsLayout);		
		
		Button btnEdit = new Button(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.addClickListener(e -> {
			candidatController.editAdminCptMin(cptMin, this);
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);


		if (parametreController.getSIScolMode().equals(ConstanteUtils.SI_SCOL_APOGEE)){
			Button btnSyncApogee = new Button(applicationContext.getMessage("btnSyncApo", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
			btnSyncApogee.setDisableOnClick(true);
			btnSyncApogee.addClickListener(e -> {
				candidatController.synchronizeCandidat(cptMin,this);
				btnSyncApogee.setEnabled(true);
			});
			buttonsLayout.addComponent(btnSyncApogee);
			buttonsLayout.setComponentAlignment(btnSyncApogee, Alignment.MIDDLE_CENTER);
		}		
		
		Button btnDelete = new Button(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.addClickListener(e -> {
			candidatController.deleteCandidat(cptMin, this);	
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);
		
		/*La table*/
		table.addBooleanColumn(SimpleTablePresentation.champsValue,false);
		table.setVisibleColumns((Object[]) FIELDS_ORDER);
		table.setColumnCollapsingAllowed(false);
		table.setColumnReorderingAllowed(false);
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table.setSelectable(false);
		table.setImmediate(true);	
		table.setColumnWidth(SimpleTablePresentation.champsTitle, 250);
		table.setCellStyleGenerator((components, itemId, columnId)->{
			if (columnId!=null && columnId.equals(SimpleTablePresentation.champsTitle)){
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		globalLayout.addComponent(table);
		
				
	}
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		cptMin = candidatController.getCompteMinima();
		String error = candidatController.getErrorViewForAdmin(cptMin);
		
		if (error!=null){
			errorLabel.setValue(error);
			errorLabel.setVisible(true);
			globalLayout.setVisible(false);
		}else{
			errorLabel.setVisible(false);
			globalLayout.setVisible(true);
			
			title.setValue(applicationContext.getMessage("candidat.admin.title", new Object[]{candidatController.getLibelleTitle(cptMin)}, UI.getCurrent().getLocale()));
			
			List<SimpleTablePresentation> liste = candidatController.getInfoForAdmin(cptMin);
			container.removeAllItems();
			container.addAll(liste);
			table.setPageLength(liste.size());
			
			String lockError = candidatController.getLockErrorFull(cptMin);
			if (lockError!=null){
				lockLabel.setValue(lockError);
				lockLabel.setVisible(true);
				buttonsLayout.setVisible(false);
			}
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		candidatController.unlockCandidatFull(cptMin);
		super.detach();
		
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatAdminListener#cptMinModified(fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima)
	 */
	@Override
	public void cptMinModified(CompteMinima cptMin) {
		List<SimpleTablePresentation> liste = candidatController.getInfoForAdmin(cptMin);
		container.removeAllItems();
		container.addAll(liste);
		table.setPageLength(liste.size());
	}

}
