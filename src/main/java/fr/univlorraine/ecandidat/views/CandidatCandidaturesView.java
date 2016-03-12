package fr.univlorraine.ecandidat.views;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatureController;
import fr.univlorraine.ecandidat.controllers.CandidaturePieceController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission_;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureCandidatViewListener;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;

/**
 * Page de gestion des candidatures du candidat
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CandidatCandidaturesView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatCandidaturesView extends CandidatViewTemplate implements View, CandidatureCandidatViewListener{

	/** serialVersionUID **/
	private static final long serialVersionUID = 2421706908276140168L;

	public static final String NAME = "candidatCandidaturesView";
	
	public static final String[] FIELDS_ORDER = {
		Candidature_.formation.getName()+"."+Formation_.libForm.getName(),
		Candidature_.formation.getName()+"."+Formation_.datRetourForm.getName(),
		ConstanteUtils.CANDIDATURE_LIB_STATUT,
		ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION,
		Candidature_.formation.getName()+"."+Formation_.commission.getName()+"."+Commission_.centreCandidature.getName()+"."+CentreCandidature_.libCtrCand.getName()
		};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient I18nController i18nController;
	
	/* Composants */
	private BeanItemContainer<Candidature> candidatureContainer = new BeanItemContainer<Candidature>(Candidature.class);
	private TableFormating candidatureTable = new TableFormating(null, candidatureContainer);

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {	
		super.init();
		setNavigationButton(CandidatFormationProView.NAME, null);
		String[] fieldsOrderToUse = FIELDS_ORDER;
		
		Button btnNewCandidature = new Button(applicationContext.getMessage("candidature.btn.new", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);		
		btnNewCandidature.setEnabled(true);
		btnNewCandidature.addClickListener(e -> {
			candidatureController.editNewCandidature();
		});
		addGenericButton(btnNewCandidature, Alignment.MIDDLE_LEFT);


		Button btnOpenCandidature = new Button(applicationContext.getMessage("btnOpen", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnOpenCandidature.setEnabled(false);
		btnOpenCandidature.addClickListener(e -> {
			if (candidatureTable.getValue() instanceof Candidature) {
				Candidature candidature = (Candidature) candidatureTable.getValue();
				candidatureController.openCandidatureCandidat(candidature, this);
			}
		});
		addGenericButton(btnOpenCandidature, Alignment.MIDDLE_RIGHT);
		
		/*Gestionnaire?*/
		Boolean isGestionnaire = userController.isGestionnaire();
		
		/*Table candidatures*/
		if (isGestionnaire){
			btnNewCandidature.setCaption(applicationContext.getMessage("candidature.btn.proposition", null, UI.getCurrent().getLocale()));
			candidatureContainer.addNestedContainerProperty(Candidature_.formation.getName()+"."+Formation_.commission.getName()+"."+Commission_.centreCandidature.getName()+"."+CentreCandidature_.libCtrCand.getName());
		}else{
			fieldsOrderToUse = (String[])ArrayUtils.removeElement(fieldsOrderToUse, Candidature_.formation.getName()+"."+Formation_.commission.getName()+"."+Commission_.centreCandidature.getName()+"."+CentreCandidature_.libCtrCand.getName());
		}
		candidatureContainer.addNestedContainerProperty(Candidature_.formation.getName()+"."+Formation_.libForm.getName());
		candidatureContainer.addNestedContainerProperty(Candidature_.formation.getName()+"."+Formation_.datRetourForm.getName());
				
		candidatureTable.addGeneratedColumn(ConstanteUtils.CANDIDATURE_LIB_STATUT, new ColumnGenerator() {			
			/**serialVersionUID*/
			private static final long serialVersionUID = -1985038014803378244L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final Candidature candidature = (Candidature) itemId;
				return new Label(i18nController.getI18nTraduction(candidature.getTypeStatut().getI18nLibTypStatut()));
			}
		});
		candidatureTable.addGeneratedColumn(ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION, new ColumnGenerator() {			
			/**serialVersionUID*/
			private static final long serialVersionUID = 1334549956279013012L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final Candidature candidature = (Candidature) itemId;
				return new Label(candidatureController.getLibLastTypeDecisionCandidature(candidature.getLastTypeDecision(),!isGestionnaire));
			}
		});
		
		candidatureTable.setSizeFull();
		candidatureTable.setVisibleColumns((Object[]) fieldsOrderToUse);
		for (String fieldName : fieldsOrderToUse) {
			candidatureTable.setColumnHeader(fieldName, applicationContext.getMessage("candidature." + fieldName, null, UI.getCurrent().getLocale()));
		}
		candidatureTable.setSortContainerPropertyId(Candidature_.idCand.getName());
		candidatureTable.setColumnCollapsingAllowed(true);
		candidatureTable.setColumnReorderingAllowed(true);
		candidatureTable.setSelectable(true);
		candidatureTable.setImmediate(true);
		candidatureTable.addItemSetChangeListener(e -> candidatureTable.sanitizeSelection());
		candidatureTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de Candidature sont actifs seulement si une Candidature est sélectionnée. */
			btnOpenCandidature.setEnabled(candidatureTable.getValue() instanceof Candidature);
		});
		candidatureTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				candidatureTable.select(e.getItemId());
				btnOpenCandidature.click();
			}
		});
		addGenericComponent(candidatureTable);
		setGenericExpandRatio(candidatureTable);	
	}
	
	
	
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		if (majView(applicationContext.getMessage("candidatures.title", null, UI.getCurrent().getLocale()), true, null)){
			candidatureContainer.removeAllItems();
			candidatureContainer.addAll(candidatureController.getCandidatures(candidat));
		}

		String param = event.getParameters();
		if (param!=null && !param.equals("")){
			try {
				Integer id = Integer.parseInt(param);
				Candidature candidature = candidatureController.loadCandidature(id);
				if (candidature != null){
					candidatureController.openCandidatureCandidat(candidature, this);
				}
			} catch (NumberFormatException nfe) {
			}
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
		
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureCandidatViewListener#candidatureCanceled(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void candidatureCanceled(Candidature candidature) {
		candidatureTable.removeItem(candidature);
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureCandidatViewListener#statutDossierModified(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void statutDossierModified(Candidature candidature) {
		candidatureTable.removeItem(candidature);
		candidatureTable.addItem(candidature);
		candidatureTable.sort();
		
	}
}