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
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatParcoursController;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatStage;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatStage_;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatStageListener;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;

/**
 * Page de gestion des parcours pro du candidat
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CandidatStageView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatStageView extends CandidatViewTemplate implements View, CandidatStageListener{

	/**serialVersionUID**/
	private static final long serialVersionUID = -3359952121644902749L;

	public static final String NAME = "candidatStageView";

	public static final String[] FIELDS_ORDER_STAGE = {
		CandidatStage_.anneeStage.getName(),
		CandidatStage_.dureeStage.getName(),
		CandidatStage_.nbHSemStage.getName(),
		CandidatStage_.organismeStage.getName(),
		CandidatStage_.descriptifStage.getName()
	};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;
	
	/*Stages*/
	private BeanItemContainer<CandidatStage> stageContainer = new BeanItemContainer<CandidatStage>(CandidatStage.class);
	private TableFormating stageTable = new TableFormating(null, stageContainer);
		
	
	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		super.init();
		setNavigationButton(CandidatCursusExterneView.NAME,CandidatFormationProView.NAME);
		Button btnNewStage = new Button(applicationContext.getMessage("stage.btn.new", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNewStage.setEnabled(true);
		btnNewStage.addClickListener(e -> {
			candidatParcoursController.editStage(candidat, null, this);
		});
		addGenericButton(btnNewStage, Alignment.MIDDLE_LEFT);


		Button btnEditStage = new Button(applicationContext.getMessage("btnModifier", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEditStage.setEnabled(false);
		btnEditStage.addClickListener(e -> {
			if (stageTable.getValue() instanceof CandidatStage) {
				candidatParcoursController.editStage(candidat, (CandidatStage) stageTable.getValue(), this);
			}
		});
		addGenericButton(btnEditStage, Alignment.MIDDLE_CENTER);
		
		Button btnDeleteStage = new Button(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDeleteStage.setEnabled(false);
		btnDeleteStage.addClickListener(e -> {
			if (stageTable.getValue() instanceof CandidatStage) {
				candidatParcoursController.deleteStage(candidat, (CandidatStage) stageTable.getValue(), this);
			}			
		});
		addGenericButton(btnDeleteStage, Alignment.MIDDLE_RIGHT);
		
		/*Table stage*/		
		stageTable.setSizeFull();
		stageTable.setVisibleColumns((Object[]) FIELDS_ORDER_STAGE);
		for (String fieldName : FIELDS_ORDER_STAGE) {
			stageTable.setColumnHeader(fieldName, applicationContext.getMessage("stage." + fieldName, null, UI.getCurrent().getLocale()));
		}
		stageTable.setColumnCollapsingAllowed(true);
		stageTable.setColumnReorderingAllowed(true);
		stageTable.setSelectable(true);
		stageTable.setImmediate(true);
		stageTable.setSortContainerPropertyId(CandidatStage_.anneeStage.getName());
		stageTable.addItemSetChangeListener(e -> stageTable.sanitizeSelection());
		stageTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de CandidatCursusPro sont actifs seulement si une CandidatCursusPro est sélectionnée. */
			boolean stageIsSelected = stageTable.getValue() instanceof CandidatStage;
			btnEditStage.setEnabled(stageIsSelected);
			btnDeleteStage.setEnabled(stageIsSelected);
		});
		stageTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				stageTable.select(e.getItemId());
				btnEditStage.click();
			}
		});
		addGenericComponent(stageTable);
		setGenericExpandRatio(stageTable);
	}
	
	/**
	 * Met a jour les composants
	 */
	private void majComponents(){
		stageContainer.removeAllItems();
		stageContainer.addAll(candidat.getCandidatStage());
		stageTable.sort();
	}
	
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		
		if (majView(applicationContext.getMessage("stage.title", null, UI.getCurrent().getLocale()), true,  ConstanteUtils.LOCK_STAGE)){
			majComponents();
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		candidatController.unlockCandidatRessource(cptMin, ConstanteUtils.LOCK_STAGE);
		super.detach();
		
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatStageListener#stageModified(java.util.List)
	 */
	@Override
	public void stageModified(List<CandidatStage> candidatStage) {
		candidat.setCandidatStage(candidatStage);
		majComponents();
	}
	
}