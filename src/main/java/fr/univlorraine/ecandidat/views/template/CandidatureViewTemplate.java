package fr.univlorraine.ecandidat.views.template;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.controllers.AdresseController;
import fr.univlorraine.ecandidat.controllers.CandidatureController;
import fr.univlorraine.ecandidat.controllers.CandidatureCtrCandController;
import fr.univlorraine.ecandidat.controllers.CandidaturePieceController;
import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.controllers.CommissionController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat_;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement_;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader.OnDemandStreamResource;
import fr.univlorraine.ecandidat.vaadin.components.TableFilterDecorator;
import fr.univlorraine.ecandidat.vaadin.components.TableFilterFormating;
import fr.univlorraine.ecandidat.vaadin.components.TableFilterGenerator;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxCommission;
import fr.univlorraine.ecandidat.views.windows.CtrCandActionCandidatureWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandExportWindow;

public class CandidatureViewTemplate extends VerticalLayout{

	/** serialVersionUID **/
	private static final long serialVersionUID = -6250439872677389730L;

	public static final String[] FIELDS_ORDER_ALL = {			
			"check",
			//Candidature_.idCand.getName(),
			Candidature_.candidat.getName()+"."+Candidat_.compteMinima.getName()+"."+CompteMinima_.numDossierOpiCptMin.getName(),
			Candidature_.candidat.getName()+"."+Candidat_.nomPatCandidat.getName(),
			Candidature_.candidat.getName()+"."+Candidat_.prenomCandidat.getName(),
			Candidature_.formation.getName()+"."+Formation_.codForm.getName(),
			Candidature_.formation.getName()+"."+Formation_.libForm.getName(),
			Candidature_.typeStatut.getName()+"."+TypeStatut_.libTypStatut.getName(),
			Candidature_.typeTraitement.getName()+"."+TypeTraitement_.libTypTrait.getName(),
			Candidature_.temValidTypTraitCand.getName(),
			"lastTypeDecision."+TypeDecisionCandidature_.typeDecision.getName()+"."+TypeDecision_.libTypDec.getName(),
			"lastTypeDecision."+TypeDecisionCandidature_.temValidTypeDecCand.getName(),
			"lastTypeDecision."+TypeDecisionCandidature_.motivationAvis.getName()+"."+MotivationAvis_.libMotiv.getName(),
			"lastTypeDecision."+TypeDecisionCandidature_.commentTypeDecCand.getName(),
			Candidature_.temAcceptCand.getName(),
			Candidature_.datTransDossierCand.getName(),
			Candidature_.datReceptDossierCand.getName(),
			Candidature_.datCompletDossierCand.getName(),
			Candidature_.datIncompletDossierCand.getName(),
			"download"
			};
	
	/* Injections */
	@Resource
	protected transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	protected transient CandidatureController candidatureController;	
	@Resource
	protected transient CandidaturePieceController candidaturePieceController;
	@Resource
	protected transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient CommissionController commissionController;
	@Resource
	private transient AdresseController adresseController;
	@Resource
	protected transient CandidatureCtrCandController ctrCandCandidatureController;

	/* Composants */
	private CentreCandidature ctrCand;
	
	private BeanItemContainer<Candidature> container = new BeanItemContainer<Candidature>(Candidature.class);
	private TableFilterFormating candidatureTable = new TableFilterFormating();
	private ComboBoxCommission cbCommission = new ComboBoxCommission();
	private VerticalLayout layout = new VerticalLayout();
	private Label titleView = new Label();
	
	/**
	 * Initialise la vue
	 */
	public void init(Boolean modeModif, Boolean isGestCtrCand, Boolean isCanceled, Boolean isArchived) {
		String[] FIELDS_ORDER = FIELDS_ORDER_ALL;
		if (!modeModif){
			FIELDS_ORDER = (String[])ArrayUtils.removeElement(FIELDS_ORDER, "check");
		}
				
		/* Style */
		setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		addComponent(layout);
		
		/* Titre */		
		titleView.addStyleName(ValoTheme.LABEL_H2);
		layout.addComponent(titleView);
		
		SecurityCtrCandFonc securityCtrCandFonc = null;
		Boolean readOnly = true;
		if (isGestCtrCand){
			/*Récupération du centre de candidature en cours*/
			securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE);
			if (securityCtrCandFonc==null || securityCtrCandFonc.getIdCtrCand()==null || securityCtrCandFonc.getReadOnly()==null){			
				addComponent(new Label(applicationContext.getMessage("erreurView.title", null, UI.getCurrent().getLocale())));
				titleView.setVisible(false);
				return;
			}
			((MainUI)UI.getCurrent()).checkConcordanceCentreCandidature(securityCtrCandFonc.getIdCtrCand());
			ctrCand = centreCandidatureController.getCentreCandidature(securityCtrCandFonc.getIdCtrCand());	
			readOnly = securityCtrCandFonc.getReadOnly();
			
			List<Commission> liste = commissionController.getCommissionsByCtrCand(ctrCand,securityCtrCandFonc.getIsGestAllCommission(),securityCtrCandFonc.getListeIdCommission());
			cbCommission.setWidth(350,Unit.PIXELS);
			cbCommission.filterListValue(liste);
			
			if (liste.size()>0){
				cbCommission.setValue(liste.get(0));
			}
			
			/*Filtrage*/		
			HorizontalLayout filtreLayout = new HorizontalLayout();
			layout.addComponent(filtreLayout);
			filtreLayout.setSpacing(true);
			Label labelFiltre = new Label(applicationContext.getMessage("candidature.change.commission", null, UI.getCurrent().getLocale()));
			filtreLayout.addComponent(labelFiltre);
			filtreLayout.setComponentAlignment(labelFiltre, Alignment.MIDDLE_LEFT);
			
			filtreLayout.addComponent(cbCommission);
			filtreLayout.setComponentAlignment(cbCommission, Alignment.BOTTOM_LEFT);
			
			Button btnChange = new Button(applicationContext.getMessage("btnChange", null, UI.getCurrent().getLocale()),FontAwesome.REFRESH);
			btnChange.addClickListener(e->{
				majContainer();
			});
			filtreLayout.addComponent(btnChange);
			filtreLayout.setComponentAlignment(btnChange, Alignment.BOTTOM_LEFT);
			
			btnChange.click();
		}
		
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);
		

		Button btnOpen = new Button(applicationContext.getMessage("btnOpen", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnOpen.setEnabled(false);
		btnOpen.addClickListener(e -> {
			if (candidatureTable.getValue() instanceof Candidature) {
				candidatureController.openCandidature((Candidature) candidatureTable.getValue(),isCanceled, isArchived, null);				
			}
		});
		buttonsLayout.addComponent(btnOpen);
		buttonsLayout.setComponentAlignment(btnOpen, Alignment.MIDDLE_LEFT);
		
		if (modeModif && !readOnly){
			Button btnSelectAll = new Button(applicationContext.getMessage("btnSelectAll", null, UI.getCurrent().getLocale()), FontAwesome.RECYCLE);
			btnSelectAll.addClickListener(e -> {
				candidatureTable.getItemIds().forEach(f->{
					if (f instanceof Candidature) {
						((Candidature)f).setCheck(true);
					}
				});
				candidatureTable.refreshRowCache();
			});
			buttonsLayout.addComponent(btnSelectAll);
			buttonsLayout.setComponentAlignment(btnSelectAll, Alignment.MIDDLE_CENTER);
			
			Button btnDeselectAll = new Button(applicationContext.getMessage("btnDeselectAll", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
			btnDeselectAll.addClickListener(e -> {
				candidatureTable.getItemIds().forEach(f->{
					if (f instanceof Candidature) {
						((Candidature)f).setCheck(false);
					}
				});
				candidatureTable.refreshRowCache();
			});
			buttonsLayout.addComponent(btnDeselectAll);
			buttonsLayout.setComponentAlignment(btnDeselectAll, Alignment.MIDDLE_CENTER);
			
			Button btnEditMasse = new Button(applicationContext.getMessage("btnAction", null, UI.getCurrent().getLocale()), FontAwesome.GAVEL);
			btnEditMasse.addClickListener(e -> {
				List<Candidature> listeCheck = container.getItemIds().stream().filter(cand->(cand.getCheck()!=null && cand.getCheck() == true)).collect(Collectors.toList());
				if (listeCheck.size()==0 && candidatureTable.getValue()==null){
					Notification.show(applicationContext.getMessage("candidature.noselected", null, UI.getCurrent().getLocale()),Type.WARNING_MESSAGE);
				}else if (listeCheck.size()>ConstanteUtils.SIZE_MAX_EDITION_MASSE){
					Notification.show(applicationContext.getMessage("candidature.toomuchselected", new Object[]{ConstanteUtils.SIZE_MAX_EDITION_MASSE}, UI.getCurrent().getLocale()),Type.WARNING_MESSAGE);
					return;
				}else{
					if (listeCheck.size()==0 && candidatureTable.getValue()!=null){
						listeCheck.add((Candidature) candidatureTable.getValue());
					}
					CtrCandActionCandidatureWindow window = new CtrCandActionCandidatureWindow(listeCheck, true);
					UI.getCurrent().addWindow(window);
				}
			});
			buttonsLayout.addComponent(btnEditMasse);
			buttonsLayout.setComponentAlignment(btnEditMasse, Alignment.MIDDLE_RIGHT);
		}
		
		Button btnExport = new Button(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()), FontAwesome.FILE_EXCEL_O);
		btnExport.addClickListener(e->{
			@SuppressWarnings("unchecked")
			List<Candidature> listeCand = (List<Candidature>) candidatureTable.getItemIds();
			if (listeCand.size()==0){
				return;
			}			
			CtrCandExportWindow window = new CtrCandExportWindow(listeCand);
			UI.getCurrent().addWindow(window);
		});
		
		buttonsLayout.addComponent(btnExport);
		buttonsLayout.setComponentAlignment(btnExport, Alignment.MIDDLE_RIGHT);


		/* Table des formations */		
		container.addNestedContainerProperty(Candidature_.candidat.getName()+"."+Candidat_.compteMinima.getName()+"."+CompteMinima_.numDossierOpiCptMin.getName());
		container.addNestedContainerProperty(Candidature_.candidat.getName()+"."+Candidat_.nomPatCandidat.getName());
		container.addNestedContainerProperty(Candidature_.candidat.getName()+"."+Candidat_.prenomCandidat.getName());
		container.addNestedContainerProperty(Candidature_.formation.getName()+"."+Formation_.codForm.getName());		
		container.addNestedContainerProperty(Candidature_.formation.getName()+"."+Formation_.libForm.getName());
		container.addNestedContainerProperty(Candidature_.typeTraitement.getName()+"."+TypeTraitement_.libTypTrait.getName());
		container.addNestedContainerProperty(Candidature_.typeStatut.getName()+"."+TypeStatut_.libTypStatut.getName());
		container.addNestedContainerProperty("lastTypeDecision."+TypeDecisionCandidature_.typeDecision.getName()+"."+TypeDecision_.libTypDec.getName());
		container.addNestedContainerProperty("lastTypeDecision."+TypeDecisionCandidature_.temValidTypeDecCand.getName());
		container.addNestedContainerProperty("lastTypeDecision."+TypeDecisionCandidature_.motivationAvis.getName()+"."+MotivationAvis_.libMotiv.getName());
		container.addNestedContainerProperty("lastTypeDecision."+TypeDecisionCandidature_.commentTypeDecCand.getName());

		TableFilterGenerator filterGenerator = new TableFilterGenerator();
		candidatureTable.setFilterGenerator(filterGenerator);
		candidatureTable.setFilterBarVisible(true);
		candidatureTable.setFilterDecorator(new TableFilterDecorator(){
			private static final long serialVersionUID = 1517839735803236830L;

			@Override
			public String getAllItemsVisibleString() {
				return applicationContext.getMessage("filter.all", null, UI.getCurrent().getLocale());
			}			
		});
		if (modeModif){
			candidatureTable.addGeneratedColumn("check", new ColumnGenerator() {
				

				private static final long serialVersionUID = 4469374706314686286L;

				@Override
				public Object generateCell(CustomTable source, Object itemId,
						Object columnId) {
					CheckBox cb = new CheckBox();
					Candidature cand = (Candidature) itemId;
					cb.addValueChangeListener(e->{
						cand.setCheck(cb.getValue());
					});
					
					cb.setValue(cand.getCheck());
			        return cb;
				}
			});
		}
		
		/*Commentaire trop long*/
		candidatureTable.addGeneratedColumn("lastTypeDecision."+TypeDecisionCandidature_.commentTypeDecCand.getName(), new ColumnGenerator() {
			private static final long serialVersionUID = 1424062351000228982L;

			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				Candidature candidature = (Candidature) itemId;
				if (candidature.getLastTypeDecision()!=null){
					String lib = candidature.getLastTypeDecision().getCommentTypeDecCand();
					if (lib!=null && lib.length()>20){
						lib = lib.substring(0, 20)+"....";
					}
					Label label = new Label(lib);
					label.setDescription(candidature.getLastTypeDecision().getCommentTypeDecCand());
					return label;
				}else{
					return null;
				}
				
			}
			
		});
		
		candidatureTable.addGeneratedColumn("download", new ColumnGenerator() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 6474016030962514854L;

			@Override
			public Object generateCell(CustomTable source, Object itemId,
					Object columnId) {
				Candidature candidature = (Candidature) itemId;
				Button btnDownload = new Button(FontAwesome.CLOUD_DOWNLOAD);
				
				new OnDemandFileDownloader(new OnDemandStreamResource() {
					/*** serialVersionUID*/
					private static final long serialVersionUID = 776254791843178061L;

					@Override
					public InputStream getStream() {
						
						InputStream is = candidatureController.downloadDossier(candidature, candidatureController.getInformationsCandidature(candidature, false), candidatureController.getInformationsDateCandidature(candidature, false), 
								adresseController.getLibelleAdresseCommission(candidature.getFormation().getCommission(), "<br>"), candidaturePieceController.getPjCandidature(candidature),candidaturePieceController.getFormulaireCandidature(candidature));
						if (is != null){
							btnDownload.setEnabled(true);
							return is;
						}
						btnDownload.setEnabled(true);
						return null;
					}
					
					@Override
					public String getFilename() {
						return candidatureController.getNomFichierDossier(candidature.getCandidat());
					}
				},btnDownload);
				return btnDownload;
			}
		});

		
		candidatureTable.setContainerDataSource(container);
		candidatureTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			if (!fieldName.equals("check")){
				candidatureTable.setColumnHeader(fieldName, applicationContext.getMessage("candidature.table." + fieldName, null, UI.getCurrent().getLocale()));
			}else{
				candidatureTable.setColumnHeader(fieldName,"");
			}
		}
		candidatureTable.setColumnCollapsingAllowed(true);
		candidatureTable.setColumnReorderingAllowed(true);
		candidatureTable.setSelectable(true);
		candidatureTable.setImmediate(true);
		candidatureTable.setFilterFieldVisible("check",false);
		candidatureTable.setColumnCollapsible("check", false);
		//candidatureTable.setColumnCollapsed(Candidature_.idCand.getName(), true);
		candidatureTable.setSortContainerPropertyId(Candidature_.idCand.getName());
		candidatureTable.addItemSetChangeListener(e -> candidatureTable.sanitizeSelection());
		candidatureTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de formation sont actifs seulement si une formation est sélectionnée. */
			btnOpen.setEnabled(candidatureTable.getValue() instanceof Candidature);
		});
		candidatureTable.addBooleanColumn(Candidature_.temValidTypTraitCand.getName(), true);
		candidatureTable.addBooleanColumn("lastTypeDecision."+TypeDecisionCandidature_.temValidTypeDecCand.getName(), true);
		//candidatureTable.addBooleanColumn(Candidature_.temAcceptCand.getName(), true);
		
		/*On cache les colonnes de date*/
		candidatureTable.setColumnCollapsed(Candidature_.datTransDossierCand.getName(), true);
		candidatureTable.setColumnCollapsed(Candidature_.datReceptDossierCand.getName(), true);
		candidatureTable.setColumnCollapsed(Candidature_.datCompletDossierCand.getName(), true);
		candidatureTable.setColumnCollapsed(Candidature_.datIncompletDossierCand.getName(), true);
		
		candidatureTable.setColumnWidth(Candidature_.formation.getName()+"."+Formation_.libForm.getName(), 100);
		
		layout.addComponent(candidatureTable);
		layout.setExpandRatio(candidatureTable, 1);
		candidatureTable.setSizeFull();
		
		candidatureTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				candidatureTable.select(e.getItemId());
				btnOpen.click();
			}
		});
	}
	
	/** Passe au mode d'erreur
	 * @param mode
	 */
	protected void switchToErrorMode(Boolean mode){
		layout.setVisible(!mode);
	}
	
	/** Modifie le titre pour le centre de candidature
	 * @param code
	 */
	protected void setTitleCtrCand(String code){
		if (ctrCand==null){
			return;
		}
		setTitle(applicationContext.getMessage(code, new Object[]{ctrCand.getLibCtrCand()}, UI.getCurrent().getLocale()));	
	}
	
	/** Modifie le titre
	 * @param libelle
	 */
	protected void setTitle(String libelle){
		titleView.setValue(libelle);
	}
	
	/**
	 * @param commission
	 * @return la liste des candidature
	 */
	protected List<Candidature> getListeCandidature(Commission commission){
		return new ArrayList<Candidature>();
	}
	
	/**
	 * @return la commission
	 */
	protected Commission getCommission(){
		return (Commission) cbCommission.getValue();
	}
	
	/** Verifie que la candidature appartient bien à la commission
	 * @param entity
	 * @return true si lq cqndidature qppqrtient à la commission
	 */
	protected Boolean isEntityApartientCommission(Candidature entity){
		Commission commission = getCommission();
		if (commission==null){
			return false;
		}
		
		if (entity.getFormation().getCommission().getIdComm().equals(commission.getIdComm())){
			return true;
		}
		return false;
	}
	
	/** Met à jour le container grace a la commission
	 */
	protected void majContainer(){	
		container.removeAllItems();
		Commission commission = getCommission();
		if (commission!=null){
			container.addAll(getListeCandidature(commission));
		}		
	}
	
	/** Supprime une entité de la table
	 * @param entity
	 */
	public void removeEntity(Candidature entity) {
		if (!isEntityApartientCommission(entity)){
			return;
		}
		candidatureTable.removeItem(entity);
	}
	
	/** Persisite une entité de la table
	 * @param entity
	 */
	public void addEntity(Candidature entity) {
		if (!isEntityApartientCommission(entity)){
			return;
		}
		candidatureTable.addItem(entity);
		candidatureTable.sort();
	}
}
