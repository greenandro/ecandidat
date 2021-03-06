package fr.univlorraine.ecandidat.views.windows;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.AdresseController;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatureController;
import fr.univlorraine.ecandidat.controllers.CandidatureCtrCandController;
import fr.univlorraine.ecandidat.controllers.CandidaturePieceController;
import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureCandidatViewListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.FormulairePresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader.OnDemandStreamResource;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Fenêtre d'édition de candidature
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CandidatureWindow extends Window implements CandidatureListener{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -1967836926575353048L;
	
	/*Champs de PJ*/
	public String[] FIELDS_ORDER_PJ = {PjPresentation.CHAMPS_CHECK,PjPresentation.CHAMPS_LIB_PJ,PjPresentation.CHAMPS_FILE_PJ,PjPresentation.CHAMPS_LIB_STATUT,PjPresentation.CHAMPS_CONDITIONNEL,PjPresentation.CHAMPS_COMMENTAIRE};
	public String[] FIELDS_ORDER_FORMULAIRE = {FormulairePresentation.CHAMPS_LIB,FormulairePresentation.CHAMPS_URL,FormulairePresentation.CHAMPS_LIB_STATUT,FormulairePresentation.CHAMPS_CONDITIONNEL,FormulairePresentation.CHAMPS_REPONSES};

	@Resource
	private transient ApplicationContext applicationContext;
	
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CandidatureCtrCandController ctrCandCandidatureController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient AdresseController adresseController;
	
	private Label labelPj = new Label("",ContentMode.HTML);
	private BeanItemContainer<PjPresentation> pjContainer = new BeanItemContainer<PjPresentation>(PjPresentation.class);
	private TableFormating pjTable = new TableFormating(pjContainer);
	private BeanItemContainer<FormulairePresentation> formulaireContainer = new BeanItemContainer<FormulairePresentation>(FormulairePresentation.class);
	private TableFormating formulaireTable = new TableFormating(formulaireContainer);
	private GridLayout gridInfoLayout = new GridLayout(2, 5);
	private GridLayout gridDateLayout = new GridLayout(2, 4);
	
	/*La candidature*/
	private Candidature candidature;
	private List<SimpleTablePresentation> listePresentation;
	private List<SimpleTablePresentation> listeDatePresentation;
	
	/* Composants */
	private Button btnDownload;
	private Button btnAction;
	private Button btnClose;
	private Button btnConfirm;
	private Button btnDesist;
	private Button btnCancel;
	private Button btnTransmettre;
	
	/**
	 * Boolean d'autorisation de modif
	 */
	private Boolean isDematerialise;
	
	/**
	 * Boolean permettant de savoir si l'utilisateur a accès aux boutons d'action
	 */
	private Boolean isAutorizedToUpdate;
	
	/**
	 * Boolean permettant de savoir si l'utilisateur est un gestionnaire de la candidature
	 */
	private Boolean isGestionnaireOfCandidature;
	
	/**
	 * Boolean permettant de savoir si l'utilisateur est le candidat
	 */
	private Boolean isCandidatOfCandidature;
	
	/**
	 * Date limite de rerour de la candidature
	 */
	private String dateLimiteRetour;
	
	/**
	 * Le listener d'ecoute de la vue candidature
	 */
	private CandidatureCandidatViewListener candidatureCandidatListener;

	/** Crée une fenêtre d'édition de candidature
	 * @param candidatureWindow
	 * @param isLocked
	 * @param isCanceled
	 * @param archived
	 */
	public CandidatureWindow(Candidature candidatureWindow, Boolean isLocked, Boolean isCanceled, Boolean archived) {
		this.candidature = candidatureWindow;
		
		Boolean isCandidat = candidatureController.isCandidatOfCandidature(candidatureWindow);
		isCandidatOfCandidature = isCandidat && !isLocked && !isCanceled && !archived; 
		isGestionnaireOfCandidature = candidatureController.isGestionnaireOfCandidature(candidatureWindow) && !isLocked && !isCanceled && !archived; 
		isAutorizedToUpdate = (isCandidatOfCandidature || isGestionnaireOfCandidature) && !isLocked && !isCanceled && !archived; 
		
		isDematerialise = candidatureController.isCandidatureDematerialise(candidatureWindow);
		
		/* Style */
		setModal(true);
		setSizeFull();
		setResizable(false);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("candidature.window.title", new Object[]{candidatController.getLibelleTitle(candidatureWindow.getCandidat().getCompteMinima())}, UI.getCurrent().getLocale()));
		
		/*Definition des valeurs de liste*/
		listePresentation = candidatureController.getInformationsCandidature(candidature, isCandidat);
		listeDatePresentation = candidatureController.getInformationsDateCandidature(candidature, isCandidat);
		dateLimiteRetour = MethodUtils.getLibByPresentationCode(listeDatePresentation, "candidature."+Candidature_.formation.getName()+"."+Formation_.datRetourForm.getName());
		
		/*Phrase Non-Demat*/
		if (!isDematerialise){
			Label labelNonDemat = new Label(applicationContext.getMessage("pieceJustificative.nodemat.title", new Object[]{dateLimiteRetour}, UI.getCurrent().getLocale()));
			labelNonDemat.addStyleName(StyleConstants.LABEL_SAUT_LIGNE);
			labelNonDemat.setSizeUndefined();
			labelNonDemat.addStyleName(ValoTheme.LABEL_H4);
			labelNonDemat.addStyleName(StyleConstants.LABEL_MORE_BOLD);
			layout.addComponent(labelNonDemat);
			layout.setComponentAlignment(labelNonDemat, Alignment.MIDDLE_CENTER);
		}
		
		/*Info + adresse*/
		HorizontalLayout hlPresentationAdr = new HorizontalLayout();
		hlPresentationAdr.setSizeFull();
		hlPresentationAdr.setSpacing(true);
		layout.addComponent(hlPresentationAdr);
		layout.setExpandRatio(hlPresentationAdr, 1);
		
		/*Grid d'info*/
		gridInfoLayout.setSizeUndefined();
		gridInfoLayout.setWidth(100, Unit.PERCENTAGE);
		gridInfoLayout.setMargin(true);
		gridInfoLayout.setSpacing(true);
		gridInfoLayout.setColumnExpandRatio(0, 0);
		gridInfoLayout.setColumnExpandRatio(1, 1);
		Panel panelInfo = new Panel(applicationContext.getMessage("candidature.info.title", null, UI.getCurrent().getLocale()),gridInfoLayout);
		panelInfo.addStyleName(StyleConstants.PANEL_COLORED);
		panelInfo.setSizeFull();
		hlPresentationAdr.addComponent(panelInfo);
		hlPresentationAdr.setExpandRatio(panelInfo, 1);		
		updateCandidaturePresentation(listePresentation);
		
		/*Grid des dates*/
		gridDateLayout.setSizeUndefined();
		gridDateLayout.setWidth(100, Unit.PERCENTAGE);
		gridDateLayout.setMargin(true);
		gridDateLayout.setSpacing(true);
		gridDateLayout.setColumnExpandRatio(0, 0);
		gridDateLayout.setColumnExpandRatio(1, 1);
		Panel panelDateInfo = new Panel(applicationContext.getMessage("candidature.info.date.title", null, UI.getCurrent().getLocale()),gridDateLayout);
		panelDateInfo.addStyleName(StyleConstants.PANEL_COLORED);
		panelDateInfo.setSizeFull();
		hlPresentationAdr.addComponent(panelDateInfo);
		hlPresentationAdr.setExpandRatio(panelDateInfo, 0.7f);
		updateCandidatureDatePresentation(listeDatePresentation);
		
		
		/*Adresse de contact*/		
		VerticalLayout vlAdr = new VerticalLayout();
		vlAdr.setSizeUndefined();
		vlAdr.setWidth(100, Unit.PERCENTAGE);
		vlAdr.setMargin(true);
		Panel panelAdr = new Panel(applicationContext.getMessage("candidature.adresse.title", null, UI.getCurrent().getLocale()),vlAdr);
		panelAdr.addStyleName(StyleConstants.PANEL_COLORED);
		panelAdr.setSizeFull();
		hlPresentationAdr.addComponent(panelAdr);
		hlPresentationAdr.setExpandRatio(panelAdr, 0.7f);
		
		Label labelAdr = new Label(adresseController.getLibelleAdresseCommission(candidature.getFormation().getCommission(), "<br>"));
		labelAdr.setContentMode(ContentMode.HTML);
		labelAdr.addStyleName(StyleConstants.LABEL_SAUT_LIGNE);
		vlAdr.addComponent(labelAdr);
		
		
		/*Ce listener pour mettre a jour la window*/
		CandidatureListener listener = this;

		/*Onglets*/
		TabSheet sheet = new TabSheet();
		sheet.addStyleName(StyleConstants.TABSHEET_LARGE_CAPTION);
		sheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		sheet.setImmediate(true);				
		sheet.setSizeFull();
		layout.addComponent(sheet);
		layout.setExpandRatio(sheet, 1.4f);
		
		
		/*Les pieces*/
		VerticalLayout vlPJ = new VerticalLayout();		
		vlPJ.setSizeFull();
		vlPJ.setSpacing(true);
		sheet.addTab(vlPJ, applicationContext.getMessage("candidature.pj", null, UI.getCurrent().getLocale()));
		
		HorizontalLayout hlTitlePj = new HorizontalLayout();
		hlTitlePj.setWidth(100, Unit.PERCENTAGE);
		hlTitlePj.setMargin(true);
		hlTitlePj.setSpacing(true);
		
		labelPj.addStyleName(ValoTheme.LABEL_BOLD);
		hlTitlePj.addComponent(labelPj);
		hlTitlePj.setComponentAlignment(labelPj, Alignment.MIDDLE_LEFT);
		hlTitlePj.setExpandRatio(labelPj, 1);
		
		List<PjPresentation> listePj = candidaturePieceController.getPjCandidature(candidature);
		pjContainer.addAll(listePj);
		
		if (isGestionnaireOfCandidature){
			Button editPj = new Button(applicationContext.getMessage("pieceJustificative.noselected.libbtn", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
			editPj.addStyleName(ValoTheme.BUTTON_SMALL);
			editPj.addClickListener(e->{
				List<PjPresentation> listeCheck = pjContainer.getItemIds().stream().filter(pj->pj.getCheck()!=null && pj.getCheck() == true).collect(Collectors.toList());
				if (listeCheck.size()==0){
					Notification.show(applicationContext.getMessage("pieceJustificative.noselected", null, UI.getCurrent().getLocale()),Type.WARNING_MESSAGE);
				}else{
					Boolean trouveNonConcerne = false;
					String codeNonConcerne = NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE;
					
					for (PjPresentation pj : listeCheck){
						if (pj.getCodStatut().equals(codeNonConcerne)){
							trouveNonConcerne = true;
						}
					}
					if (!trouveNonConcerne){
						candidaturePieceController.changeStatutPj(listeCheck,candidature,listener);
					}else{
						Notification.show(applicationContext.getMessage("pieceJustificative.nonconcerned", null, UI.getCurrent().getLocale()),Type.WARNING_MESSAGE);
					}
											
				}
			});
			hlTitlePj.addComponent(editPj);
			hlTitlePj.setComponentAlignment(editPj, Alignment.MIDDLE_RIGHT);
		}
		
		vlPJ.addComponent(hlTitlePj);
		
		/* Table des pj */
		if (!fileController.getModeDematBackoffice().equals(ConstanteUtils.TYPE_FICHIER_STOCK_NONE)){
			pjTable.addGeneratedColumn(PjPresentation.CHAMPS_LIB_PJ, new ColumnGenerator() {

				/**serialVersionUID **/
				private static final long serialVersionUID = -1985038014803378244L;

				@Override
				public Object generateCell(Table source, Object itemId,
						Object columnId) {
					final PjPresentation pieceJustif = (PjPresentation) itemId;
					Fichier file = pieceJustif.getPieceJustif().getFichier();
					String libPj = pieceJustif.getLibPj();
					if (pieceJustif.getPieceJustif().getFichier()==null){
						return new Label(libPj);
					}else{								
						HorizontalLayout hl = new HorizontalLayout();
						hl.setSpacing(true);
						hl.setWidth(100, Unit.PERCENTAGE);
						
						Button btnDownload = new Button(FontAwesome.DOWNLOAD);
						btnDownload.setDescription(applicationContext.getMessage("file.btnDownload", null, UI.getCurrent().getLocale()));
						hl.addComponent(btnDownload);
						hl.setComponentAlignment(btnDownload, Alignment.MIDDLE_CENTER);
						new OnDemandFileDownloader(new OnDemandStreamResource() {
							/*** serialVersionUID*/
							private static final long serialVersionUID = 5030762008310392600L;

							@Override
							public InputStream getStream() {
								InputStream is = fileController.getInputStreamFromFichier(file,true);
								if (is != null){
									btnDownload.setEnabled(true);
									return is;
								}
								btnDownload.setEnabled(true);
								return null;
							}
							
							@Override
							public String getFilename() {
								return file.getNomFichier();
							}
						},btnDownload);
						Label label = new Label(libPj);
						hl.addComponent(label);
						hl.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
						hl.setExpandRatio(label, 1.0f);
						
						return hl;
					}
				}
				
			});
		}
		
		
		if (isDematerialise){
			labelPj.setValue(applicationContext.getMessage("pieceJustificative.demat", new Object[]{dateLimiteRetour}, UI.getCurrent().getLocale()));			
			pjTable.addGeneratedColumn(PjPresentation.CHAMPS_FILE_PJ, new ColumnGenerator() {
				/*** serialVersionUID*/
				private static final long serialVersionUID = -1985038014803378244L;

				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					final PjPresentation pieceJustif = (PjPresentation) itemId;
					if (pieceJustif.getFilePj()==null && !pieceJustif.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)){
						if (isAutorizedToUpdatePJ(pieceJustif.getCodStatut())){
							Button btnAdd = new Button(FontAwesome.PLUS);
							btnAdd.setDescription(applicationContext.getMessage("file.btnAdd", null, UI.getCurrent().getLocale()));							
							btnAdd.addClickListener(e->candidaturePieceController.addFileToPieceJustificative(pieceJustif,candidature,listener));
							return btnAdd;
						}else{
							return null;
						}
						
					}else if (!pieceJustif.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)){
						HorizontalLayout hl = new HorizontalLayout();
						hl.setSpacing(true);
						hl.setWidth(100, Unit.PERCENTAGE);
						if (isAutorizedToUpdatePJ(pieceJustif.getCodStatut())){
							Button btnDel = new Button(FontAwesome.MINUS);
							btnDel.setDescription(applicationContext.getMessage("file.btnDel", null, UI.getCurrent().getLocale()));
							btnDel.addClickListener(e->candidaturePieceController.deleteFileToPieceJustificative(pieceJustif,candidature,listener));
							hl.addComponent(btnDel);
							hl.setComponentAlignment(btnDel, Alignment.MIDDLE_CENTER);
						}
						Button btnDownload = new Button(FontAwesome.DOWNLOAD);
						btnDownload.setDescription(applicationContext.getMessage("file.btnDownload", null, UI.getCurrent().getLocale()));
						hl.addComponent(btnDownload);
						hl.setComponentAlignment(btnDownload, Alignment.MIDDLE_CENTER);
						new OnDemandFileDownloader(new OnDemandStreamResource() {
							/*** serialVersionUID*/
							private static final long serialVersionUID = 5030762008310392600L;

							@Override
							public InputStream getStream() {
								InputStream is = fileController.getInputStreamFromFichier(pieceJustif.getFilePj(),false);
								if (is != null){
									btnDownload.setEnabled(true);
									return is;
								}
								btnDownload.setEnabled(true);
								return null;
							}
							
							@Override
							public String getFilename() {
								return pieceJustif.getFilePj().getNomFichier();
							}
						},btnDownload);
						Label label = new Label(pieceJustif.getFilePj().getNomFichier());
						hl.addComponent(label);
						hl.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
						hl.setExpandRatio(label, 1.0f);
						
						return hl;
					}
					return null;				
				}
			});
		}else{
			labelPj.setValue(applicationContext.getMessage("pieceJustificative.nodemat", null, UI.getCurrent().getLocale()));
			FIELDS_ORDER_PJ = (String[]) ArrayUtils.removeElement(FIELDS_ORDER_PJ, PjPresentation.CHAMPS_FILE_PJ);
		}
		
		if (isGestionnaireOfCandidature){
			pjTable.addGeneratedColumn(PjPresentation.CHAMPS_CHECK, new ColumnGenerator(){

				/**serialVersionUID **/
				private static final long serialVersionUID = 1522690670761921058L;

				@Override
				public Object generateCell(Table source, Object itemId,
						Object columnId) {
					CheckBox cb = new CheckBox();
					PjPresentation pj = (PjPresentation) itemId;
					cb.setValue(pj.getCheck());
					cb.addValueChangeListener(e->{
						pj.setCheck(cb.getValue());
					});
			        return cb;
				}				
			});
		}else{
			FIELDS_ORDER_PJ = (String[]) ArrayUtils.removeElement(FIELDS_ORDER_PJ, PjPresentation.CHAMPS_CHECK);
		}
		
		if (listePj.stream().filter(e->e.getPJConditionnel()).count()>0){
			pjTable.addGeneratedColumn(PjPresentation.CHAMPS_CONDITIONNEL, new ColumnGenerator() {
				
				/**serialVersionUID **/
				private static final long serialVersionUID = -4680554333128589763L;

				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					final PjPresentation pieceJustif = (PjPresentation) itemId;
					
					if (pieceJustif.getFilePj()==null && pieceJustif.getPJConditionnel()){
						if (isAutorizedToUpdatePJ(pieceJustif.getCodStatut())){
							if (!pieceJustif.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)){
								Button btn = new Button(applicationContext.getMessage("pj.btn.nonConcerne", null, UI.getCurrent().getLocale()), FontAwesome.THUMBS_O_DOWN);
								btn.addClickListener(e->{
									candidaturePieceController.setIsConcernedPieceJustificative(pieceJustif, false,candidature,listener);
								});								
								return getLayoutBtnConditionnel(btn);
							}else{
								Button btn = new Button(applicationContext.getMessage("pj.btn.concerne", null, UI.getCurrent().getLocale()), FontAwesome.THUMBS_O_UP);
								btn.addClickListener(e->{
									candidaturePieceController.setIsConcernedPieceJustificative(pieceJustif, true, candidature,listener);
								});	
								return getLayoutBtnConditionnel(btn);
							}
						}else{
							if (pieceJustif.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)){
								return applicationContext.getMessage("pj.btn.nonConcerne", null, UI.getCurrent().getLocale());
							}
						}
					}
					return null;
				}
			});
		}else{
			FIELDS_ORDER_PJ = (String[]) ArrayUtils.removeElement(FIELDS_ORDER_PJ, PjPresentation.CHAMPS_CONDITIONNEL);
		}
		
		
		pjTable.setVisibleColumns((Object[]) FIELDS_ORDER_PJ);
		for (String fieldName : FIELDS_ORDER_PJ) {
			pjTable.setColumnHeader(fieldName, applicationContext.getMessage("pieceJustificative." + fieldName, null, UI.getCurrent().getLocale()));
		}
		pjTable.setColumnCollapsingAllowed(true);
		pjTable.setColumnReorderingAllowed(true);
		pjTable.setSortContainerPropertyId(PjPresentation.CHAMPS_LIB_PJ);
		pjTable.setSelectable(false);
		pjTable.setImmediate(true);
		pjTable.setSizeFull();
		vlPJ.addComponent(pjTable);
		vlPJ.setExpandRatio(pjTable, 1);
		
		
		/*Formulaires*/
		List<FormulairePresentation> listeFormulaire = candidaturePieceController.getFormulaireCandidature(candidature);
		
		if (listeFormulaire.size()>0){
			VerticalLayout vlForm = new VerticalLayout();		
			vlForm.setSizeFull();
			vlForm.setSpacing(true);
			sheet.addTab(vlForm, applicationContext.getMessage("candidature.formulaire", null, UI.getCurrent().getLocale()));
			
			HorizontalLayout hlFormTitle = new HorizontalLayout();
			hlFormTitle.setSpacing(true);
			
			Label labelFormulaire = new Label(applicationContext.getMessage("formulaireComp.title", null, UI.getCurrent().getLocale()),ContentMode.HTML);
			labelFormulaire.addStyleName(ValoTheme.LABEL_H3);	
			hlFormTitle.addComponent(labelFormulaire);
			
			Button showResponse = new Button(applicationContext.getMessage("formulaireComp.btn.show.reponse", null, UI.getCurrent().getLocale()), FontAwesome.SEARCH_PLUS);
			showResponse.setEnabled(false);
			showResponse.addStyleName(ValoTheme.BUTTON_SMALL);
			showResponse.addClickListener(e->{
				if (formulaireTable.getValue() instanceof FormulairePresentation){
					FormulairePresentation pres = (FormulairePresentation) formulaireTable.getValue();
					if (pres.getReponses()!=null){
						String ret = "";
						StringTokenizer st = new StringTokenizer(pres.getReponses(), "\\{;\\}");

						while (st.hasMoreElements()) {
							ret = ret + st.nextElement()+"<br>";
						}
						
						UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("formulaireComp.reponses", null, UI.getCurrent().getLocale()), ret, 500, 70));
					}
					
				}
			});
			hlFormTitle.addComponent(showResponse);
			hlFormTitle.setComponentAlignment(showResponse, Alignment.BOTTOM_LEFT);
			
			vlForm.addComponent(hlFormTitle);
			
			formulaireContainer.addAll(candidaturePieceController.getFormulaireCandidature(candidature));
					
			formulaireTable.addGeneratedColumn(FormulairePresentation.CHAMPS_URL, new ColumnGenerator() {
				/**serialVersionUID **/
				private static final long serialVersionUID = 2404357691366134124L;


				@Override
				public Object generateCell(Table source, Object itemId,
						Object columnId) {
					final FormulairePresentation formulaire = (FormulairePresentation) itemId;
					String url = formulaire.getUrlFormulaire();
					if (url!=null && !formulaire.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)){
						Button urlBtn = new Button(url, FontAwesome.FILE_ZIP_O);
						urlBtn.addStyleName(ValoTheme.BUTTON_LINK);
						BrowserWindowOpener urlBwo = new BrowserWindowOpener(url);
						urlBwo.extend(urlBtn);
						return urlBtn;
					}else if (url!=null){
						return url;
					}
					return null;
				}
				
			});
			formulaireTable.addGeneratedColumn(FormulairePresentation.CHAMPS_REPONSES, new ColumnGenerator() {
				/**serialVersionUID **/
				private static final long serialVersionUID = 8293778536942060100L;


				@Override
				public Object generateCell(Table source, Object itemId,
						Object columnId) {
					final FormulairePresentation formulaire = (FormulairePresentation) itemId;
					if(formulaire.getReponses()!=null){
						String ret = formulaire.getReponses().replaceAll("\\{;\\}", " / ");
						return MethodUtils.replaceLast(ret, " / ", "");
					}
					return null;
				}
				
			});
			if (listeFormulaire.stream().filter(e->e.getConditionnel()).count()>0){
				formulaireTable.addGeneratedColumn(FormulairePresentation.CHAMPS_CONDITIONNEL, new ColumnGenerator() {
					
					/**serialVersionUID **/
					private static final long serialVersionUID = 8293778536942060100L;

					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						final FormulairePresentation formulaire = (FormulairePresentation) itemId;
						if (formulaire.getConditionnel()){
							if (isAutorizedToUpdatePJ(formulaire.getCodStatut())){
								if (!formulaire.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)){
									Button btn = new Button(applicationContext.getMessage("formulaire.btn.nonConcerne", null, UI.getCurrent().getLocale()), FontAwesome.THUMBS_O_DOWN);
									btn.addClickListener(e->{
										candidaturePieceController.setIsConcernedFormulaire(formulaire, false,candidature,listener);
									});								
									return getLayoutBtnConditionnel(btn);
								}else{
									Button btn = new Button(applicationContext.getMessage("formulaire.btn.concerne", null, UI.getCurrent().getLocale()), FontAwesome.THUMBS_O_UP);
									btn.addClickListener(e->{
										candidaturePieceController.setIsConcernedFormulaire(formulaire, true, candidature,listener);
									});	
									return getLayoutBtnConditionnel(btn);
								}
							}else{
								if (formulaire.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)){
									return applicationContext.getMessage("formulaire.btn.nonConcerne", null, UI.getCurrent().getLocale());
								}
							}
						}
						return null;
					}
				});
			}else{
				FIELDS_ORDER_FORMULAIRE = (String[]) ArrayUtils.removeElement(FIELDS_ORDER_FORMULAIRE, FormulairePresentation.CHAMPS_CONDITIONNEL);
			}

			/* Table des formulaires */						
			formulaireTable.setVisibleColumns((Object[]) FIELDS_ORDER_FORMULAIRE);
			for (String fieldName : FIELDS_ORDER_FORMULAIRE) {
				formulaireTable.setColumnHeader(fieldName, applicationContext.getMessage("formulaireComp." + fieldName, null, UI.getCurrent().getLocale()));
			}
			formulaireTable.setColumnCollapsingAllowed(true);
			formulaireTable.setColumnReorderingAllowed(true);
			formulaireTable.setSelectable(true);
			formulaireTable.setImmediate(true);
			formulaireTable.setSortContainerPropertyId(FormulairePresentation.CHAMPS_LIB);
			formulaireTable.addValueChangeListener(e -> {
				/* Les boutons d'ouverture de reponse. */
				if (formulaireTable.getValue() instanceof FormulairePresentation){
					FormulairePresentation pres = (FormulairePresentation) formulaireTable.getValue();
					if (pres.getReponses()!=null){
						showResponse.setEnabled(true);
						return;
					}
				}
				showResponse.setEnabled(false);
			});
			
			
			vlForm.addComponent(formulaireTable);
			vlForm.setExpandRatio(formulaireTable, 1);
			formulaireTable.setSizeFull();
		}
		
		/*Sheet info comp*/
		String infoComp = candidature.getFormation().getInfoCompForm();
		if (infoComp!=null && !infoComp.equals("")){
			VerticalLayout vlInfoComp = new VerticalLayout();		
			vlInfoComp.setSizeFull();

			VerticalLayout vlInfoCompContent = new VerticalLayout();		
			vlInfoCompContent.setSizeUndefined();
			vlInfoCompContent.setWidth(100, Unit.PERCENTAGE);
			vlInfoCompContent.setMargin(true);
			
			Label labelInfoComp = new Label(infoComp);
			labelInfoComp.setContentMode(ContentMode.HTML);
			labelInfoComp.addStyleName(StyleConstants.LABEL_SAUT_LIGNE);
			//labelInfoComp.remoStyleName("v-label-undef-w");
			vlInfoCompContent.addComponent(labelInfoComp);
			
			Panel panelInfoComp = new Panel(vlInfoCompContent);
			panelInfoComp.setSizeFull();
			panelInfoComp.addStyleName(StyleConstants.PANEL_WITHOUT_BORDER);
			vlInfoComp.addComponent(panelInfoComp);
			vlInfoComp.setExpandRatio(panelInfoComp, 1);
			
			
			sheet.addTab(vlInfoComp, applicationContext.getMessage("candidature.infoscomp", null, UI.getCurrent().getLocale()));
		}
		
		
		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);
		
		btnClose = new Button(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnClose.addClickListener(e -> close());
		buttonsLayout.addComponent(btnClose);
		buttonsLayout.setComponentAlignment(btnClose, Alignment.MIDDLE_LEFT);
		
		if (isGestionnaireOfCandidature){
			btnAction = new Button(applicationContext.getMessage("btnAction", null, UI.getCurrent().getLocale()), FontAwesome.EDIT);
			btnAction.addClickListener(e->{
				//ctrCandCandidatureController.changeStatutDossier(candidature,listener);
				ctrCandCandidatureController.editActionCandidature(candidature,listener);
			});
			buttonsLayout.addComponent(btnAction);
			buttonsLayout.setComponentAlignment(btnAction, Alignment.MIDDLE_CENTER);
		}
		
		btnConfirm = new Button(applicationContext.getMessage("candidature.confirmation", null, UI.getCurrent().getLocale()), FontAwesome.THUMBS_UP);
		btnConfirm.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		btnConfirm.addClickListener(e->{
			candidatureController.setConfirmationCandidature(candidature,true,listener);
		});
		buttonsLayout.addComponent(btnConfirm);
		buttonsLayout.setComponentAlignment(btnConfirm, Alignment.MIDDLE_CENTER);
		
		btnDesist = new Button(applicationContext.getMessage("candidature.desistement", null, UI.getCurrent().getLocale()), FontAwesome.THUMBS_DOWN);
		btnDesist.addStyleName(ValoTheme.BUTTON_DANGER);
		btnDesist.addClickListener(e->{
			candidatureController.setConfirmationCandidature(candidature,false,listener);
		});
		buttonsLayout.addComponent(btnDesist);
		buttonsLayout.setComponentAlignment(btnDesist, Alignment.MIDDLE_CENTER);
		
		btnCancel = new Button(applicationContext.getMessage("candidature.cancel", null, UI.getCurrent().getLocale()), FontAwesome.ERASER);
		btnCancel.addClickListener(e->{
			candidatureController.cancelCandidature(candidature,listener,candidatureCandidatListener);
		});
		buttonsLayout.addComponent(btnCancel);
		buttonsLayout.setComponentAlignment(btnCancel, Alignment.MIDDLE_CENTER);
		
		btnTransmettre = new Button(applicationContext.getMessage("candidature.transmettre", null, UI.getCurrent().getLocale()), FontAwesome.SEND);		
		btnTransmettre.addClickListener(e->{
			candidaturePieceController.transmettreCandidatureAfterClick(this.candidature, pjContainer.getItemIds(), this);
		});
		buttonsLayout.addComponent(btnTransmettre);
		buttonsLayout.setComponentAlignment(btnTransmettre, Alignment.MIDDLE_CENTER);
		
		updateBtnAction();
		updateBtnTransmettre();
		
		/*Bouton d'annulation d'éannulation de candidature*/
		
		/*if (candidatureController.isAutorizedToUpdateCandidature(candidatureWindow) && !isLocked && isCanceled && !archived){
			Button btnAnnulCancel = new Button(applicationContext.getMessage("candidature.annul.cancel", null, UI.getCurrent().getLocale()), FontAwesome.ERASER);
			btnAnnulCancel.addClickListener(e->{
				candidatureController.annulCancelCandidature(candidature,listener);
			});
			buttonsLayout.addComponent(btnAnnulCancel);
			buttonsLayout.setComponentAlignment(btnAnnulCancel, Alignment.MIDDLE_CENTER);
		}*/

		btnDownload = new Button(applicationContext.getMessage("candidature.download", null, UI.getCurrent().getLocale()), FontAwesome.CLOUD_DOWNLOAD);
		buttonsLayout.addComponent(btnDownload);
		buttonsLayout.setComponentAlignment(btnDownload, Alignment.MIDDLE_RIGHT);
		
		new OnDemandFileDownloader(new OnDemandStreamResource() {
			/*** serialVersionUID*/
			private static final long serialVersionUID = 776254791843178061L;

			@Override
			public InputStream getStream() {
				InputStream is = candidatureController.downloadDossier(candidature, listePresentation, listeDatePresentation ,labelAdr.getValue(), pjContainer.getItemIds(),formulaireContainer.getItemIds());
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

		/* Centre la fenêtre */
		center();
	}
	
	/**
	 * @param btn
	 * @return le layout de bouton conditionnel
	 */
	private HorizontalLayout getLayoutBtnConditionnel(Button btn){
		btn.addStyleName(ValoTheme.BUTTON_TINY);
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(btn);
		layout.setComponentAlignment(btn, Alignment.MIDDLE_CENTER);
		return layout;
	}
	
	
	/**
	 * Modifie l'etat des boutons de transmission
	 */
	private void updateBtnTransmettre() {
		if (!isAutorizedToUpdate || !isDematerialise || !isAutorizedToUpdateCandidature() || !candidaturePieceController.isOkToTransmettreCandidatureStatutDossier(candidature.getTypeStatut().getCodTypStatut(), false)){
			btnTransmettre.setVisible(false);
		}else{
			btnTransmettre.setVisible(true);
			if (candidaturePieceController.isOkToTransmettreCandidatureStatutPiece(pjContainer.getItemIds(), false)){
				btnTransmettre.addStyleName(ValoTheme.BUTTON_FRIENDLY);
			}else{
				btnTransmettre.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
			}
		}
	}
	
	/**
	 * Modifie l'etat des boutons d'annulation, de confirmation et desistement
	 */
	private void updateBtnAction(){
		if (isAutorizedToUpdate && candidature!=null){
			TypeDecisionCandidature td = candidature.getLastTypeDecision();
			
			/*Mise a jour des boutons de confirmation et desistement*/
			if (td!=null && td.getTypeDecision().getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_FAV) && td.getTemValidTypeDecCand() 
					&& candidature.getTemAcceptCand()==null && candidature.getDatAnnulCand()==null){
				if (isGestionnaireOfCandidature){
					btnConfirm.setVisible(true);
					btnDesist.setVisible(true);
				}else if((candidature.getFormation().getDatConfirmForm()==null || 
						(candidature.getFormation().getDatConfirmForm()!=null && 
						(candidature.getFormation().getDatConfirmForm().isAfter(LocalDate.now()) || candidature.getFormation().getDatConfirmForm().isEqual(LocalDate.now()))))){
					btnConfirm.setVisible(true);
					btnDesist.setVisible(true);
				}else{
					btnConfirm.setVisible(false);
					btnDesist.setVisible(false);
				}
			}else{
				btnConfirm.setVisible(false);
				btnDesist.setVisible(false);
			}
			
			if (td==null && candidature.getTypeStatut().getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_ATT)){
				btnCancel.setVisible(true);
			}else{
				btnCancel.setVisible(false);
			}
			
		}else{
			btnConfirm.setVisible(false);
			btnDesist.setVisible(false);
			btnCancel.setVisible(false);
		}
	}
	
	/** Met a jour le panel d'info
	 * @param listePresentation
	 */
	private void updateCandidaturePresentation(List<SimpleTablePresentation> listePresentation){
		int i = 0;
		gridInfoLayout.removeAllComponents();
		gridInfoLayout.setRows(listePresentation.size());
		for (SimpleTablePresentation e : listePresentation){
			Label title = new Label(e.getTitle());
			title.addStyleName(ValoTheme.LABEL_BOLD);
			title.setSizeUndefined();
			gridInfoLayout.addComponent(title,0,i);
			Label value = new Label((String) e.getValue(),ContentMode.HTML);
			if ((e.getCode().equals("candidature."+ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION) && e.getShortValue()!=null && !e.getShortValue().equals(NomenclatureUtils.TYP_AVIS_ATTENTE))
				||
				(e.getCode().equals("candidature."+ConstanteUtils.CANDIDATURE_LIB_STATUT) && e.getShortValue()!=null && !e.getShortValue().equals(NomenclatureUtils.TYPE_STATUT_ATT))
				){
				title.addStyleName(StyleConstants.LABEL_COLORED);
				value.addStyleName(StyleConstants.LABEL_COLORED);
				value.addStyleName(ValoTheme.LABEL_BOLD);
			}
			value.setWidth(100, Unit.PERCENTAGE);
			gridInfoLayout.addComponent(value,1,i);
			i++;
		}
	}
	
	/** Met à jour le panel de dates
	 * @param listePresentation
	 */
	private void updateCandidatureDatePresentation(List<SimpleTablePresentation> listePresentation){
		int i = 0;
		gridDateLayout.removeAllComponents();
		gridDateLayout.setRows(listePresentation.size());
		for (SimpleTablePresentation e : listePresentation){
			Label title = new Label(e.getTitle());
			title.addStyleName(ValoTheme.LABEL_BOLD);
			title.setSizeUndefined();
			gridDateLayout.addComponent(title,0,i);
			Label value = new Label((String) e.getValue());
			if (e.getCode().equals("candidature."+Candidature_.formation.getName()+"."+Formation_.datRetourForm.getName())){
				title.addStyleName(StyleConstants.LABEL_COLORED);
				value.addStyleName(StyleConstants.LABEL_COLORED);
				value.addStyleName(ValoTheme.LABEL_BOLD);
			}
			value.setWidth(100, Unit.PERCENTAGE);
			gridDateLayout.addComponent(value,1,i);
			i++;
		}
	}
	
	/**
	 * @return true si l'utilisateur a le droit de modifier les pj
	 */
	private Boolean isAutorizedToUpdateCandidature(){
		if (isAutorizedToUpdate){
			if (!candidature.getFormation().getDatRetourForm().isBefore(LocalDate.now()) || isGestionnaireOfCandidature){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param pieceJustif
	 * @return true si l'utilisateur a le droit de modifier la pj
	 */
	private Boolean isAutorizedToUpdatePJ(String codStatutPiece){
		if (isGestionnaireOfCandidature){
			return true;
		}
		if (!isAutorizedToUpdateCandidature()){
			return false;
		}else{
			String statutCandidature = candidature.getTypeStatut().getCodTypStatut();
			if (statutCandidature.equals(NomenclatureUtils.TYPE_STATUT_REC) || (statutCandidature.equals(NomenclatureUtils.TYPE_STATUT_COM))){
				return false;
			}else{
				if (codStatutPiece.equals(NomenclatureUtils.TYP_STATUT_PIECE_VALIDE)){
					return false;
				}
			}
		}
		return true;
	}
	
	/** Ajoute un listener de candidature
	 * @param candidatureCandidatListener
	 */
	public void addCandidatureCandidatListener(CandidatureCandidatViewListener candidatureCandidatListener){
		this.candidatureCandidatListener = candidatureCandidatListener;
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#pjModified(fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation, fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void pjModified(PjPresentation pieceJustif, Candidature candidature) {
		pjContainer.removeItem(pieceJustif);
		pjContainer.addBean(pieceJustif);
		pjTable.sort();
		this.candidature = candidature;
		updateBtnTransmettre();
		candidaturePieceController.transmettreCandidatureAfterDepot(this.candidature, pjContainer.getItemIds(), this, dateLimiteRetour);
	}
	
	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#formulaireModified(fr.univlorraine.ecandidat.utils.bean.presentation.FormulairePresentation, fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void formulaireModified(FormulairePresentation formulaire,
			Candidature candidature) {
		formulaireContainer.removeItem(formulaire);
		formulaireContainer.addBean(formulaire);
		formulaireTable.sort();
		this.candidature = candidature;
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#candidatureCanceled(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void candidatureCanceled(Candidature candidature) {
		close();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#pjsModified(java.util.List, fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void pjsModified(List<PjPresentation> listePj,
			Candidature candidature) {
		listePj.forEach(e->{
			e.setCheck(false);
			pjContainer.removeItem(e);
			pjContainer.addBean(e);
		});
		pjTable.sort();
		this.candidature = candidature;
		updateBtnTransmettre();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#infosCandidatureModified(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void infosCandidatureModified(Candidature candidature) {
		listePresentation = candidatureController.getInformationsCandidature(candidature, isCandidatOfCandidature);
		updateCandidaturePresentation(listePresentation);
		this.candidature = candidature;
		this.candidature.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(candidature));
		updateBtnAction();
		updateBtnTransmettre();
	}
	
	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#transmissionDossier(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void transmissionDossier(Candidature candidatureSave) {
		infosCandidatureModified(candidatureSave);
		/*on trie la table pour mettre a jour les boutons de delete de fichier et ne plus les afficher si c'est transmis*/
		pjTable.sort();
		if (candidatureCandidatListener!=null){
			candidatureCandidatListener.statutDossierModified(this.candidature);
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#openCandidat()
	 */
	@Override
	public void openCandidat() {
		close();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#candidatureDeleted(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void candidatureDeleted(Candidature candidature) {
		close();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#candidatureAnnulCanceled(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void candidatureAnnulCanceled(Candidature candidatureSave) {
		close();
	}
}
