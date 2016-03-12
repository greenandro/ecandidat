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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatParcoursController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays_;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatCursusExterneListener;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;

/**
 * Page de gestion des cursus externes du candidat
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CandidatCursusExterneView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatCursusExterneView extends CandidatViewTemplate implements View, CandidatCursusExterneListener{

	/** serialVersionUID **/
	private static final long serialVersionUID = -4231594087715212364L;

	public static final String NAME = "candidatCursusExterneView";
	
	public static final String[] FIELDS_ORDER_POST_BAC = {
		CandidatCursusPostBac_.anneeUnivCursus.getName(),
		CandidatCursusPostBac_.siScolPays.getName()+"."+SiScolPays_.libPay.getName(),
		CandidatCursusPostBac_.siScolDepartement.getName()+"."+SiScolDepartement_.libDep.getName(),
		CandidatCursusPostBac_.siScolCommune.getName()+"."+SiScolCommune_.libCom.getName(),
		CandidatCursusPostBac_.siScolEtablissement.getName()+"."+SiScolEtablissement_.libEtb.getName(),		
		CandidatCursusPostBac_.siScolDipAutCur.getName()+"."+SiScolDipAutCur_.libDac.getName(),
		CandidatCursusPostBac_.libCursus.getName(),
		CandidatCursusPostBac_.obtenuCursus.getName(),
		CandidatCursusPostBac_.siScolMention.getName()+"."+SiScolMention_.libMen.getName()
	};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;
	@Resource
	private transient TableRefController tableRefController;
	
	/* Composants */	
	private BeanItemContainer<CandidatCursusPostBac> postBacContainer = new BeanItemContainer<CandidatCursusPostBac>(CandidatCursusPostBac.class);
	private TableFormating postBacTable = new TableFormating(null, postBacContainer);

	/* Composants */

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		super.init();
		setNavigationButton(CandidatCursusInterneView.NAME, CandidatStageView.NAME);
		
		Button btnNewPostBac = new Button(applicationContext.getMessage("cursusexterne.btn.new", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNewPostBac.addClickListener(e -> {
			candidatParcoursController.editCursusPostBac(candidat, null, this);
		});
		addGenericButton(btnNewPostBac, Alignment.MIDDLE_LEFT);

		Button btnEditPostBac = new Button(applicationContext.getMessage("btnModifier", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEditPostBac.setEnabled(false);
		btnEditPostBac.addClickListener(e -> {
			if (postBacTable.getValue() instanceof CandidatCursusPostBac) {
				candidatParcoursController.editCursusPostBac(candidat, (CandidatCursusPostBac) postBacTable.getValue(), this);
			}
		});
		addGenericButton(btnEditPostBac, Alignment.MIDDLE_CENTER);
		
		Button btnDeletePostBac = new Button(FontAwesome.TRASH_O);
		btnDeletePostBac.setEnabled(false);
		btnDeletePostBac.setCaption(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()));
		btnDeletePostBac.addClickListener(e -> {
			if (postBacTable.getValue() instanceof CandidatCursusPostBac) {
				candidatParcoursController.deleteCursusPostBac(candidat, (CandidatCursusPostBac) postBacTable.getValue(), this);
			}			
		});
		addGenericButton(btnDeletePostBac, Alignment.MIDDLE_RIGHT);
		
		/*Table post Bac*/		
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolMention.getName()+"."+SiScolMention_.libMen.getName());
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolEtablissement.getName()+"."+SiScolEtablissement_.libEtb.getName());
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolPays.getName()+"."+SiScolPays_.libPay.getName());
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolDepartement.getName()+"."+SiScolDepartement_.libDep.getName());
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolCommune.getName()+"."+SiScolCommune_.libCom.getName());
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolDipAutCur.getName()+"."+SiScolDipAutCur_.libDac.getName());		
		
		postBacTable.setSizeFull();
		postBacTable.setVisibleColumns((Object[]) FIELDS_ORDER_POST_BAC);
		for (String fieldName : FIELDS_ORDER_POST_BAC) {
			postBacTable.setColumnHeader(fieldName, applicationContext.getMessage("cursusexterne." + fieldName, null, UI.getCurrent().getLocale()));
		}
		postBacTable.addGeneratedColumn(CandidatCursusPostBac_.obtenuCursus.getName(), new ColumnGenerator() {

			/**serialVersionUID **/
			private static final long serialVersionUID = -6382571666110400875L;

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				CandidatCursusPostBac post = (CandidatCursusPostBac) itemId;				
		        return tableRefController.getLibelleObtenuCursusByCode(post.getObtenuCursus());
			}
		});
		postBacTable.setSortContainerPropertyId(CandidatCursusPostBac_.anneeUnivCursus.getName());
		postBacTable.setColumnCollapsingAllowed(true);
		postBacTable.setColumnReorderingAllowed(true);
		postBacTable.setSelectable(true);
		postBacTable.setImmediate(true);
		postBacTable.addItemSetChangeListener(e -> postBacTable.sanitizeSelection());
		postBacTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de CandidatCursusPostBac sont actifs seulement si une CandidatCursusPostBac est sélectionnée. */
			boolean postBacIsSelected = postBacTable.getValue() instanceof CandidatCursusPostBac;
			btnEditPostBac.setEnabled(postBacIsSelected);
			btnDeletePostBac.setEnabled(postBacIsSelected);
		});
		postBacTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				postBacTable.select(e.getItemId());
				btnEditPostBac.click();
			}
		});
		addGenericComponent(postBacTable);
		setGenericExpandRatio(postBacTable);
	}	


	/**
	 * Met a jour les composants
	 */
	private void majComponentsPostBac(List<CandidatCursusPostBac> listCursus){
		postBacContainer.removeAllItems();
		postBacContainer.addAll(listCursus);
		postBacTable.sort();
	}
	
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		if (majView(applicationContext.getMessage("cursusexterne.title", null, UI.getCurrent().getLocale()), true,  ConstanteUtils.LOCK_CURSUS_EXTERNE)){
			majComponentsPostBac(candidat.getCandidatCursusPostBacs());
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		candidatController.unlockCandidatRessource(cptMin, ConstanteUtils.LOCK_CURSUS_EXTERNE);
		super.detach();		
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatCursusExterneListener#cursusModified(java.util.List)
	 */
	@Override
	public void cursusModified(List<CandidatCursusPostBac> list) {
		candidat.setCandidatCursusPostBacs(list);
		majComponentsPostBac(candidat.getCandidatCursusPostBacs());
	}
}
