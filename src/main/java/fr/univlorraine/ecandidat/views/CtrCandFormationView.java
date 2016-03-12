package fr.univlorraine.ecandidat.views;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.controllers.CampagneController;
import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.FormationController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission_;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des formations du centre de candidature
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CtrCandFormationView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandFormationView extends VerticalLayout implements View, EntityPushListener<Formation>{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -1394769692819084775L;
	
	public static final String NAME = "ctrCandFormationView";

	public static final String[] FIELDS_ORDER = {"flagEtat",Formation_.codForm.getName(),Formation_.libForm.getName(),Formation_.commission.getName()+"."+Commission_.libComm.getName(),Formation_.tesForm.getName(),"dateVoeux",Formation_.datRetourForm.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormationController formationController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient UserController userController;
	@Resource
	protected transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient EntityPusher<Formation> formationEntityPusher;
	
	@Resource
	private transient DateTimeFormatter formatterDate;

	/*Data du user*/
	private CentreCandidature ctrCand;
	
	/* Composants */	
	private TableFormating formationTable = new TableFormating();

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
		SecurityCtrCandFonc securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FORMATION);
		SecurityCentreCandidature securityCtrCand = userController.getCentreCandidature();
		if (securityCtrCand==null || securityCtrCandFonc==null || securityCtrCandFonc.getIdCtrCand()==null || securityCtrCandFonc.getReadOnly()==null){
			addComponent(new Label(applicationContext.getMessage("erreurView.title", null, UI.getCurrent().getLocale())));
			return;
		}
		((MainUI)UI.getCurrent()).checkConcordanceCentreCandidature(securityCtrCandFonc.getIdCtrCand());
		ctrCand = centreCandidatureController.getCentreCandidature(securityCtrCandFonc.getIdCtrCand());
		Campagne campagne = campagneController.getCampagneActive();
		
		/* Titre */
		HorizontalLayout hlTitle = new HorizontalLayout();
		hlTitle.setSpacing(true);
		//hlTitle.setWidth(100, Unit.PERCENTAGE);
		addComponent(hlTitle);
		
		Label titleParam = new Label(applicationContext.getMessage("formation.title", new Object[]{ctrCand.getLibCtrCand()}, UI.getCurrent().getLocale()));
		titleParam.addStyleName(ValoTheme.LABEL_H2);
		hlTitle.addComponent(titleParam);
		PopupView puv = new PopupView(applicationContext.getMessage("formation.table.flagEtat.tooltip", null, UI.getCurrent().getLocale()),getLegendLayout());
		hlTitle.addComponent(puv);
		hlTitle.setComponentAlignment(puv, Alignment.BOTTOM_LEFT);
		//hlTitle.setExpandRatio(puv, 1);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);


		Button btnNew = new Button(applicationContext.getMessage("formation.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			formationController.editNewFormation(ctrCand);
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);


		Button btnEdit = new Button(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (formationTable.getValue() instanceof Formation) {
				formationController.editFormation((Formation) formationTable.getValue(),ctrCand);
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);
		
		Button btnEditPieceComp = new Button(applicationContext.getMessage("formation.btnEditPiece", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEditPieceComp.setEnabled(false);
		btnEditPieceComp.addClickListener(e -> {
			if (formationTable.getValue() instanceof Formation) {
				formationController.editPieceCompFormation((Formation) formationTable.getValue(),ctrCand);
			}
		});
		buttonsLayout.addComponent(btnEditPieceComp);
		buttonsLayout.setComponentAlignment(btnEditPieceComp, Alignment.MIDDLE_CENTER);
		
		Button btnDelete = new Button(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (formationTable.getValue() instanceof Formation) {
				formationController.deleteFormation((Formation) formationTable.getValue());
			}			
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);


		/* Table des formations */
		BeanItemContainer<Formation> container = new BeanItemContainer<Formation>(Formation.class, formationController.getFormationsByCtrCand(ctrCand));
		container.addNestedContainerProperty(Formation_.commission.getName()+"."+Commission_.libComm.getName());
		formationTable.setContainerDataSource(container);
		formationTable.addGeneratedColumn("flagEtat", new ColumnGenerator() {
			
			/*** serialVersionUID*/
			private static final long serialVersionUID = 7461290324017459118L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Formation f = (Formation) itemId;
				String code = null;
				
				if (!f.getTesForm()){
					code = "red";
				}else{
					if (campagne==null){
						code = "blue";
					}else{
						LocalDate dateDeb = f.getDatDebDepotForm();
						LocalDate dateFin = f.getDatDebDepotForm();
						LocalDate dateRetour = f.getDatRetourForm();
						LocalDate dateConfirm = f.getDatConfirmForm();
						LocalDate datePubli = f.getDatPubliForm();
						LocalDate dateJury = f.getDatJuryForm();
						LocalDate dateAnalyse = f.getDatAnalyseForm();
						LocalDate datePreselect = f.getPreselectDateForm();
						if (!MethodUtils.isDateIncludeInInterval(dateDeb, campagne.getDatDebCamp(), campagne.getDatFinCamp())
							||
							!MethodUtils.isDateIncludeInInterval(dateFin, campagne.getDatDebCamp(), campagne.getDatFinCamp())
							||
							!MethodUtils.isDateIncludeInInterval(dateRetour, campagne.getDatDebCamp(), campagne.getDatFinCamp())
							||
							!MethodUtils.isDateIncludeInInterval(dateConfirm, campagne.getDatDebCamp(), campagne.getDatFinCamp())
							||
							!MethodUtils.isDateIncludeInInterval(datePubli, campagne.getDatDebCamp(), campagne.getDatFinCamp())
							||
							!MethodUtils.isDateIncludeInInterval(dateJury, campagne.getDatDebCamp(), campagne.getDatFinCamp())
							||
							!MethodUtils.isDateIncludeInInterval(dateAnalyse, campagne.getDatDebCamp(), campagne.getDatFinCamp())
							||
							!MethodUtils.isDateIncludeInInterval(datePreselect, campagne.getDatDebCamp(), campagne.getDatFinCamp())){
							code = "yellow";
						}else{
							code = "green";
						}
					}					
				}
				Image flag = new Image(null, new ThemeResource("images/icon/Flag-"+code+"-icon.png"));
				flag.setDescription(applicationContext.getMessage("formation.table.flagEtat.tooltip."+code, null, UI.getCurrent().getLocale()));
				return flag;
			}
		});
		formationTable.addGeneratedColumn("dateVoeux", new ColumnGenerator() {
			
			/*** serialVersionUID*/
			private static final long serialVersionUID = 7461290324017459118L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Formation f = (Formation) itemId;
				return applicationContext.getMessage("formation.table.dateVoeux.label", new Object[]{formatterDate.format(f.getDatDebDepotForm()),formatterDate.format(f.getDatFinDepotForm())}, UI.getCurrent().getLocale());
			}
		});
		
		formationTable.addBooleanColumn(Formation_.tesForm.getName());
		formationTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			formationTable.setColumnHeader(fieldName, applicationContext.getMessage("formation.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		
		formationTable.setSortContainerPropertyId(Formation_.codForm.getName());
		formationTable.setColumnCollapsingAllowed(true);
		formationTable.setColumnReorderingAllowed(true);
		formationTable.setSelectable(true);
		formationTable.setImmediate(true);
		formationTable.addItemSetChangeListener(e -> formationTable.sanitizeSelection());
		formationTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de formation sont actifs seulement si une formation est sélectionnée. */
			boolean formationIsSelected = formationTable.getValue() instanceof Formation;
			btnEdit.setEnabled(formationIsSelected);
			btnEditPieceComp.setEnabled(formationIsSelected);
			btnDelete.setEnabled(formationIsSelected);
		});
		formationTable.setColumnWidth("flagEtat", 60);
		addComponent(formationTable);
		setExpandRatio(formationTable, 1);
		formationTable.setSizeFull();
		
		/*Gestion du readOnly*/
		if (!securityCtrCandFonc.getReadOnly()){
			formationTable.addItemClickListener(e -> {
				if (e.isDoubleClick()) {
					formationTable.select(e.getItemId());
					btnEdit.click();
				}
			});
			buttonsLayout.setVisible(true);
		}else{
			buttonsLayout.setVisible(false);
		}
		
		
		/* Inscrit la vue aux mises à jour de formation */
		formationEntityPusher.registerEntityPushListener(this);
	}
	
	private VerticalLayout getLegendLayout(){
		VerticalLayout vlLegend = new VerticalLayout();
		//vlLegend.setWidth(300, Unit.PIXELS);
		vlLegend.setMargin(true);
		vlLegend.setSpacing(true);
		
		Label labelTitle = new Label(applicationContext.getMessage("formation.table.flagEtat.tooltip", null, UI.getCurrent().getLocale()));
		labelTitle.addStyleName(ValoTheme.LABEL_H2);
		
		vlLegend.addComponent(labelTitle);
		
		vlLegend.addComponent(getLegendLineLayout("green"));
		vlLegend.addComponent(getLegendLineLayout("red"));
		vlLegend.addComponent(getLegendLineLayout("yellow"));
		vlLegend.addComponent(getLegendLineLayout("blue"));
		return vlLegend;
	}
	
	private HorizontalLayout getLegendLineLayout(String txtCode){
		HorizontalLayout hlLineLegend = new HorizontalLayout();
		hlLineLegend.setWidth(100, Unit.PERCENTAGE);
		hlLineLegend.setSpacing(true);
		
		Image flagImg = new Image(null, new ThemeResource("images/icon/Flag-"+txtCode+"-icon.png"));
		Label label = new Label(applicationContext.getMessage("formation.table.flagEtat.tooltip." + txtCode, null, UI.getCurrent().getLocale()));
		hlLineLegend.addComponent(flagImg);
		hlLineLegend.setComponentAlignment(flagImg, Alignment.MIDDLE_LEFT);
		hlLineLegend.addComponent(label);
		hlLineLegend.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
		hlLineLegend.setExpandRatio(label, 1);
		return hlLineLegend;
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
		/* Désinscrit la vue des mises à jour de formation */
		formationEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Formation entity) {
		if (ctrCand!=null && entity.getCommission().getCentreCandidature().getIdCtrCand()==ctrCand.getIdCtrCand()){
			formationTable.removeItem(entity);
			formationTable.addItem(entity);
			formationTable.sort();			
		}		
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Formation entity) {
		if (ctrCand!=null && entity.getCommission().getCentreCandidature().getIdCtrCand()==ctrCand.getIdCtrCand()){
			formationTable.removeItem(entity);
			formationTable.addItem(entity);
			formationTable.sort();		
		}		
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Formation entity) {
		if (ctrCand!=null && entity.getCommission().getCentreCandidature().getIdCtrCand()==ctrCand.getIdCtrCand()){
			formationTable.removeItem(entity);		
		}		
	}
}
