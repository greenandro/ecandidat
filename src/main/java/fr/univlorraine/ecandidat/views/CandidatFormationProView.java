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
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPro;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPro_;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatFormationProListener;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;

/**
 * Page de gestion des parcours pro du candidat
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CandidatFormationProView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatFormationProView extends CandidatViewTemplate implements View, CandidatFormationProListener{

	/**serialVersionUID**/
	private static final long serialVersionUID = -7757655808606316737L;

	public static final String NAME = "candidatFormationProView";
		
	public static final String[] FIELDS_ORDER_FORMATIONS = {
		CandidatCursusPro_.anneeCursusPro.getName(),
		CandidatCursusPro_.intituleCursusPro.getName(),
		CandidatCursusPro_.dureeCursusPro.getName(),
		CandidatCursusPro_.organismeCursusPro.getName(),
		CandidatCursusPro_.objectifCursusPro.getName()
	};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;

	/*FormationPros*/	
	private BeanItemContainer<CandidatCursusPro> formationProContainer = new BeanItemContainer<CandidatCursusPro>(CandidatCursusPro.class);
	private TableFormating formationProTable = new TableFormating(null, formationProContainer);
	
	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		super.init();
		setNavigationButton(CandidatStageView.NAME, CandidatCandidaturesView.NAME);

		Button btnNewFormationPro = new Button(applicationContext.getMessage("formationpro.btn.new", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNewFormationPro.setEnabled(true);
		btnNewFormationPro.addClickListener(e -> {
			candidatParcoursController.editFormationPro(candidat, null, this);
		});
		addGenericButton(btnNewFormationPro, Alignment.MIDDLE_LEFT);
		
		Button btnEditFormationPro = new Button(applicationContext.getMessage("btnModifier", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEditFormationPro.setEnabled(false);
		btnEditFormationPro.addClickListener(e -> {
			if (formationProTable.getValue() instanceof CandidatCursusPro) {
				candidatParcoursController.editFormationPro(candidat, (CandidatCursusPro) formationProTable.getValue(), this);
			}
		});
		addGenericButton(btnEditFormationPro, Alignment.MIDDLE_CENTER);
		
		Button btnDeleteFormationPro = new Button(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDeleteFormationPro.setEnabled(false);
		btnDeleteFormationPro.addClickListener(e -> {
			if (formationProTable.getValue() instanceof CandidatCursusPro) {
				candidatParcoursController.deleteFormationPro(candidat, (CandidatCursusPro) formationProTable.getValue(), this);
			}			
		});
		addGenericButton(btnDeleteFormationPro, Alignment.MIDDLE_RIGHT);
		
		/*Table formationPro*/		
		formationProTable.setSizeFull();
		formationProTable.setVisibleColumns((Object[]) FIELDS_ORDER_FORMATIONS);
		for (String fieldName : FIELDS_ORDER_FORMATIONS) {
			formationProTable.setColumnHeader(fieldName, applicationContext.getMessage("formationpro." + fieldName, null, UI.getCurrent().getLocale()));
		}
		formationProTable.setColumnCollapsingAllowed(true);
		formationProTable.setColumnReorderingAllowed(true);
		formationProTable.setSortContainerPropertyId(CandidatCursusPro_.anneeCursusPro.getName());
		formationProTable.setSelectable(true);
		formationProTable.setImmediate(true);
		formationProTable.addItemSetChangeListener(e -> formationProTable.sanitizeSelection());
		formationProTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de CandidatCursusPro sont actifs seulement si une CandidatCursusPro est sélectionnée. */
			boolean formationProIsSelected = formationProTable.getValue() instanceof CandidatCursusPro;
			btnEditFormationPro.setEnabled(formationProIsSelected);
			btnDeleteFormationPro.setEnabled(formationProIsSelected);
		});
		formationProTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				formationProTable.select(e.getItemId());
				btnEditFormationPro.click();
			}
		});
		addGenericComponent(formationProTable);
		setGenericExpandRatio(formationProTable);
	}
	
	/**
	 * Met a jour les composants
	 */
	private void majComponents(){
		formationProContainer.removeAllItems();
		formationProContainer.addAll(candidat.getCandidatCursusPros());
		formationProTable.sort();
	}
	
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		if (majView(applicationContext.getMessage("formationpro.title", null, UI.getCurrent().getLocale()), true,  ConstanteUtils.LOCK_FORMATION_PRO)){
			majComponents();
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		candidatController.unlockCandidatRessource(cptMin, ConstanteUtils.LOCK_FORMATION_PRO);
		super.detach();
		
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatFormationProListener#formationProModified(java.util.List)
	 */
	@Override
	public void formationProModified(List<CandidatCursusPro> candidatCursusPros) {
		candidat.setCandidatCursusPros(candidatCursusPros);
		majComponents();
	}
	
}