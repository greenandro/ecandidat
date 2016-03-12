package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fr.univlorraine.ecandidat.controllers.CandidatureCtrCandController;
import fr.univlorraine.ecandidat.controllers.MotivationAvisController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.LocalTimeField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredCheckBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredDateField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxMotivationAvis;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxTypeDecision;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxTypeTraitement;

/**
 * Fenêtre d'action sur une ou plusieurs candidatures
 * @author Kevin Hergalant
 *
 */

@Configurable(preConstruction=true)
public class CtrCandActionCandidatureWindow extends Window{
	

	/** serialVersionUID **/
	private static final long serialVersionUID = -7776558654950981770L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient MotivationAvisController motivationAvisController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CandidatureCtrCandController ctrCandCandidatureController;
	
	public static final String[] FIELDS_ORDER_DECISION = {TypeDecisionCandidature_.typeDecision.getName(),
		TypeDecisionCandidature_.motivationAvis.getName(),
		TypeDecisionCandidature_.listCompRangTypDecCand.getName(),
		TypeDecisionCandidature_.preselectLieuTypeDecCand.getName(),
		TypeDecisionCandidature_.preselectDateTypeDecCand.getName(),
		TypeDecisionCandidature_.preselectHeureTypeDecCand.getName(),
		TypeDecisionCandidature_.temAppelTypeDecCand.getName(),
		TypeDecisionCandidature_.commentTypeDecCand.getName()};
	
	public static final String[] FIELDS_ORDER_TYPE_STATUT = {Candidature_.typeStatut.getName(),Candidature_.datReceptDossierCand.getName()};
	
	public static final String[] FIELDS_ORDER_TYPE_TRAIT = {Candidature_.typeTraitement.getName()};
	
	/* Composants */
	private OptionGroup optionGroupAction;
	private CustomBeanFieldGroup<TypeDecisionCandidature> fieldGroupDecision;
	private FormLayout formLayoutDecision;
	private CustomBeanFieldGroup<Candidature> fieldGroupTypeStatut;
	private FormLayout formLayoutTypeStatut;
	private CustomBeanFieldGroup<Candidature> fieldGroupTypeTrait;
	private FormLayout formLayoutTypeTrait;
	
	/*cas de modif d'une seule candidature*/
	private Candidature candidature;

	private Button btnValid;
	private Button btnClose;
	
	/*Le listener*/
	private ChangeCandidatureWindowListener changeCandidatureWindowListener;

	/**
	 * Crée une fenêtre d'action sur une ou plusieurs candidatures
	 * @param listeCandidature la liste de candidature a manipuler
	 */
	public CtrCandActionCandidatureWindow(List<Candidature> listeCandidature, Boolean enMasse) {
		/* Style */
		setModal(true);
		setWidth(550,Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);
		
		/*On vérifie si on traite un seul candidat*/
		if (listeCandidature.size()>0 && listeCandidature.size()==1){
			candidature = listeCandidature.get(0);
		}

		/* Titre */
		setCaption(applicationContext.getMessage("candidature.action.window", null, UI.getCurrent().getLocale()));
		
		/*Le container d'options*/
		BeanItemContainer<DroitFonctionnalite> container = new BeanItemContainer<DroitFonctionnalite>(DroitFonctionnalite.class);
		
		userController.getCandidatureFonctionnalite().forEach(e->{
			if (e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE)){
				container.addItem(new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE_STATUT_DOSSIER,applicationContext.getMessage("candidature.action.statut.dossier", null, UI.getCurrent().getLocale())));				
			}else{
				container.addItem(e);
			}
		});
		
		/*Ajout de la possiblité d'ouvrir le candidat ou l'histo de la candidature*/
		if (candidature != null){
			container.addItem(new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_OPEN_CANDIDAT,applicationContext.getMessage("candidature.action.open", null, UI.getCurrent().getLocale())));
			container.addItem(new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE_HISTO,applicationContext.getMessage("candidature.action.histo", null, UI.getCurrent().getLocale())));
		}
		
		
		
		container.sort(new Object[]{DroitFonctionnalite_.libFonc.getName()}, new boolean[]{true});
		/*Les options*/
		optionGroupAction = new OptionGroup(applicationContext.getMessage("candidature.action.label", new Object[]{listeCandidature.size()}, UI.getCurrent().getLocale()),container);
		optionGroupAction.setItemCaptionPropertyId(DroitFonctionnalite_.libFonc.getName());
		optionGroupAction.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        layout.addComponent(optionGroupAction);
        
        optionGroupAction.addValueChangeListener(e->majComponents());
        
        /*Le field group pour la decision*/
        fieldGroupDecision = new CustomBeanFieldGroup<>(TypeDecisionCandidature.class);
		fieldGroupDecision.setItemDataSource(new TypeDecisionCandidature());
		formLayoutDecision = new FormLayout();
		formLayoutDecision.setCaption(applicationContext.getMessage("candidature.action.select.opt", null, UI.getCurrent().getLocale()));
		formLayoutDecision.setWidth(100, Unit.PERCENTAGE);
		formLayoutDecision.setSpacing(true);
		for (String fieldName : FIELDS_ORDER_DECISION) {
			Field<?> field;
			if (fieldName.equals(TypeDecisionCandidature_.typeDecision.getName())){				
				field = fieldGroupDecision.buildAndBind(applicationContext.getMessage("action.decision." + fieldName, null, UI.getCurrent().getLocale()), fieldName,ComboBoxTypeDecision.class);
			}else{
				field = fieldGroupDecision.buildAndBind(applicationContext.getMessage("action.decision." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
			}
			
			if (!fieldName.equals(TypeDecisionCandidature_.preselectHeureTypeDecCand.getName())){
				field.setWidth(100, Unit.PERCENTAGE);
			}else{
				field.setSizeUndefined();
			}
						
			formLayoutDecision.addComponent(field);
		}
		layout.addComponent(formLayoutDecision);		
		
		fieldGroupTypeStatut = new CustomBeanFieldGroup<>(Candidature.class);		
		fieldGroupTypeStatut.setItemDataSource(new Candidature());
		
		
		formLayoutTypeStatut = new FormLayout();		
		formLayoutTypeStatut.setWidth(100, Unit.PERCENTAGE);
		formLayoutTypeStatut.setSpacing(true);
		for (String fieldName : FIELDS_ORDER_TYPE_STATUT) {
			Field<?> field = fieldGroupTypeStatut.buildAndBind(applicationContext.getMessage("candidature.action." + fieldName, null, UI.getCurrent().getLocale()), fieldName);		
			field.setWidth(100, Unit.PERCENTAGE);
			formLayoutTypeStatut.addComponent(field);
		}
		layout.addComponent(formLayoutTypeStatut);
		
		@SuppressWarnings("unchecked")
		RequiredComboBox<TypeStatut> cbTypeStatut = (RequiredComboBox<TypeStatut>)fieldGroupTypeStatut.getField(Candidature_.typeStatut.getName());
		cbTypeStatut.addValueChangeListener(e->majStatutDossierComponent());
		
		ComboBoxTypeDecision cbTypeDecision = (ComboBoxTypeDecision)fieldGroupDecision.getField(TypeDecisionCandidature_.typeDecision.getName());
		cbTypeDecision.addValueChangeListener(e->majAvisComponent());
		
		fieldGroupTypeTrait = new CustomBeanFieldGroup<>(Candidature.class);		
		fieldGroupTypeTrait.setItemDataSource(new Candidature());
		
		
		formLayoutTypeTrait = new FormLayout();		
		formLayoutTypeTrait.setWidth(100, Unit.PERCENTAGE);
		formLayoutTypeTrait.setSpacing(true);
		for (String fieldName : FIELDS_ORDER_TYPE_TRAIT) {
			Field<?> field = fieldGroupTypeTrait.buildAndBind(applicationContext.getMessage("candidature.action." + fieldName, null, UI.getCurrent().getLocale()), fieldName);						
			formLayoutTypeTrait.addComponent(field);
		}
		layout.addComponent(formLayoutTypeTrait);
        
        
		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);
		
		btnClose = new Button(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnClose.addClickListener(e -> close());
		buttonsLayout.addComponent(btnClose);
		buttonsLayout.setComponentAlignment(btnClose, Alignment.MIDDLE_LEFT);

		btnValid = new Button(applicationContext.getMessage("btnValid", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnValid.setDisableOnClick(true);
		btnValid.addClickListener(e -> {
			DroitFonctionnalite fonc = (DroitFonctionnalite)optionGroupAction.getValue();
			if (fonc == null){
				close();
			}else{
				String codFonc = fonc.getCodFonc();
				if (codFonc==null){
					close();
				}
				if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE_STATUT_DOSSIER)){
					try {
						/* Valide la saisie */
						fieldGroupTypeStatut.commit();
						/* Enregistre la typeStatutPiece saisie */
						if (ctrCandCandidatureController.editListCandidatureTypStatut(listeCandidature,fieldGroupTypeStatut.getItemDataSource().getBean().getTypeStatut(),fieldGroupTypeStatut.getItemDataSource().getBean().getDatReceptDossierCand(),enMasse)){
							if (changeCandidatureWindowListener!=null){
								changeCandidatureWindowListener.action(listeCandidature);
							}
							/* Ferme la fenêtre */
							close();
						};
						
					}catch (CommitException ce) {						
					}finally{
						btnValid.setEnabled(true);
					}
				}else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_EDIT_TYPTRAIT)){
					try {
						/* Valide la saisie */
						fieldGroupTypeTrait.commit();
						/* Enregistre la typeStatutPiece saisie */
						if (ctrCandCandidatureController.editListCandidatureTypTrait(listeCandidature,fieldGroupTypeTrait.getItemDataSource().getBean().getTypeTraitement(),enMasse)){
							if (changeCandidatureWindowListener!=null){
								changeCandidatureWindowListener.action(listeCandidature);
							}
							/* Ferme la fenêtre */
							close();
						};
						
					}catch (CommitException ce) {						
					}finally{
						btnValid.setEnabled(true);
					}
				}else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_EDIT_AVIS)){
					try {
						/* Valide la saisie */
						fieldGroupDecision.commit();
						/* Enregistre la typeStatutPiece saisie */
						if (ctrCandCandidatureController.editAvis(listeCandidature,fieldGroupDecision.getItemDataSource().getBean(),enMasse)){
							if (changeCandidatureWindowListener!=null){
								changeCandidatureWindowListener.action(listeCandidature);
							}
							/* Ferme la fenêtre */
							close();
						};
						
					}catch (CommitException ce) {						
					}finally{
						btnValid.setEnabled(true);
					}
				}else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_VALID_AVIS)){
					if (ctrCandCandidatureController.validAvis(listeCandidature,enMasse)){		
						if (changeCandidatureWindowListener!=null){
							changeCandidatureWindowListener.action(listeCandidature);
						}
					}
					close();
				}else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_VALID_TYPTRAIT)){
					if (ctrCandCandidatureController.validTypTrait(listeCandidature,enMasse)){
						if (changeCandidatureWindowListener!=null){
							changeCandidatureWindowListener.action(listeCandidature);
						}						
					}		
					close();
				}else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_OPEN_CANDIDAT)){
					ctrCandCandidatureController.openCandidat(candidature);
					if (changeCandidatureWindowListener!=null){
						changeCandidatureWindowListener.openCandidature(candidature);
					}
					close();
				}else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE_HISTO)){
					ctrCandCandidatureController.showHistoAvis(candidature);
				}
			}			
		});
		buttonsLayout.addComponent(btnValid);
		buttonsLayout.setComponentAlignment(btnValid, Alignment.MIDDLE_RIGHT);

		/*Met a jour lers composants*/
		majComponents();
		
		/* Centre la fenêtre */
		center();
	}
	
	/**
	 * Met a jour les composants
	 * @param listeCandidature 
	 */
	private void majComponents(){
		DroitFonctionnalite fonc = (DroitFonctionnalite)optionGroupAction.getValue();		
		if (fonc == null){
			formLayoutTypeTrait.setVisible(false);
			formLayoutTypeStatut.setVisible(false);
			formLayoutDecision.setVisible(false);
		}else{
			String codFonc = fonc.getCodFonc();
			if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_EDIT_TYPTRAIT)){				
				if (candidature != null){
					ComboBoxTypeTraitement cbTypeTraitement = (ComboBoxTypeTraitement)fieldGroupTypeTrait.getField(Candidature_.typeTraitement.getName());
					cbTypeTraitement.setValue(candidature.getTypeTraitement());
				}
				formLayoutTypeTrait.setVisible(true);
			}else{
				formLayoutTypeTrait.setVisible(false);
				
			}
			if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE_STATUT_DOSSIER)){	
				formLayoutTypeStatut.setVisible(true);
				@SuppressWarnings("unchecked")
				RequiredComboBox<TypeStatut> cbTypeStatut = (RequiredComboBox<TypeStatut>)fieldGroupTypeStatut.getField(Candidature_.typeStatut.getName());
				cbTypeStatut.setValue(tableRefController.getTypeStatutReceptionne());
				majStatutDossierComponent();
			}else{				
				formLayoutTypeStatut.setVisible(false);
			}
			if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_EDIT_AVIS)){
				formLayoutDecision.setVisible(true);
				majAvisComponent();				
			}else{
				formLayoutDecision.setVisible(false);
			}
		}
		center();
	}
	
	/**
	 * Mise à jour des composants pour les StatutDossier
	 */
	@SuppressWarnings("unchecked")
	private void majStatutDossierComponent(){
		RequiredComboBox<TypeStatut> cbTypeStatut = (RequiredComboBox<TypeStatut>)fieldGroupTypeStatut.getField(Candidature_.typeStatut.getName());
		RequiredDateField fieldDateRecept = (RequiredDateField)fieldGroupTypeStatut.getField(Candidature_.datReceptDossierCand.getName());

		if (cbTypeStatut.getValue()!=null){
			TypeStatut typeStatut = (TypeStatut) cbTypeStatut.getValue();
			if (typeStatut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_ATT)){				
				fieldDateRecept.setVisible(false);
				fieldDateRecept.setRequired(false);
				fieldDateRecept.setRequiredError(null);
				fieldDateRecept.setValue(null);
			}else{
				fieldDateRecept.setVisible(true);
				fieldDateRecept.setRequired(true);
				fieldDateRecept.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
				if (typeStatut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_REC)){
					fieldDateRecept.setCaption(applicationContext.getMessage("candidature.action." + Candidature_.datReceptDossierCand.getName(), null, UI.getCurrent().getLocale()));
				}else if (typeStatut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_COM)){
					fieldDateRecept.setCaption(applicationContext.getMessage("candidature.action." + Candidature_.datCompletDossierCand.getName(), null, UI.getCurrent().getLocale()));
				}else if (typeStatut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_INC)){
					fieldDateRecept.setCaption(applicationContext.getMessage("candidature.action." + Candidature_.datIncompletDossierCand.getName(), null, UI.getCurrent().getLocale()));
				}
			}			
		}
		fieldDateRecept.setLocalValue(LocalDate.now());
	}
	
	/**
	 * Mise à jour des composants pour les avis
	 */
	private void majAvisComponent(){
		ComboBoxTypeDecision cbTypeDecision = (ComboBoxTypeDecision)fieldGroupDecision.getField(TypeDecisionCandidature_.typeDecision.getName());
		ComboBoxMotivationAvis cbMotivation = (ComboBoxMotivationAvis)fieldGroupDecision.getField(TypeDecisionCandidature_.motivationAvis.getName());
		RequiredIntegerField fieldRang = (RequiredIntegerField)fieldGroupDecision.getField(TypeDecisionCandidature_.listCompRangTypDecCand.getName());
		RequiredTextField fieldLieuPreselect = (RequiredTextField)fieldGroupDecision.getField(TypeDecisionCandidature_.preselectLieuTypeDecCand.getName());
		RequiredDateField fieldDatePreselect = (RequiredDateField)fieldGroupDecision.getField(TypeDecisionCandidature_.preselectDateTypeDecCand.getName());
		RequiredCheckBox fieldAppel = (RequiredCheckBox)fieldGroupDecision.getField(TypeDecisionCandidature_.temAppelTypeDecCand.getName());
		LocalTimeField fieldHeurePreselect = (LocalTimeField)fieldGroupDecision.getField(TypeDecisionCandidature_.preselectHeureTypeDecCand.getName());
		RequiredTextField fieldComment = (RequiredTextField)fieldGroupDecision.getField(TypeDecisionCandidature_.commentTypeDecCand.getName());

		if (cbTypeDecision.getValue()!=null){
			if (candidature!=null && candidature.getLastTypeDecision()!=null &&
					candidature.getLastTypeDecision().getTemValidTypeDecCand() && 
					candidature.getLastTypeDecision().getTypeDecision().getTemDefinitifTypDec() &&
					parametreController.getIsAppel()){
				fieldAppel.setVisible(true);
			}else{
				fieldAppel.setVisible(false);
				fieldAppel.setValue(false);
			}
			
			fieldComment.setVisible(true);
			
			TypeDecision typeDecision = (TypeDecision) cbTypeDecision.getValue();
			if (typeDecision.getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_DEF)){
				cbMotivation.setBoxNeeded(true, null);
			}else{
				cbMotivation.setBoxNeeded(false, null);
			}
			if (typeDecision.getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_LISTE_COMP)){
				fieldRang.setVisible(true);
				fieldRang.setRequired(true);
				fieldRang.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
			}else{
				fieldRang.setVisible(false);
				fieldRang.setRequired(false);
				fieldRang.setRequiredError(null);
				fieldRang.setValue(null);
			}
			if (typeDecision.getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_PRESELECTION)){				
				fieldLieuPreselect.setVisible(true);
				fieldDatePreselect.setVisible(true);
				fieldHeurePreselect.setVisible(true);
				if (candidature!=null){					
					fieldLieuPreselect.setValue(candidature.getFormation().getPreselectLieuForm());
					fieldDatePreselect.setValue((candidature.getFormation().getPreselectDateForm()!=null)?Date.valueOf(candidature.getFormation().getPreselectDateForm()):null);
					fieldHeurePreselect.setValue(candidature.getFormation().getPreselectHeureForm());
				}else{
					fieldLieuPreselect.setValue(null);
					fieldDatePreselect.setValue(null);
					fieldHeurePreselect.setValue(null);
				}				
			}else{
				fieldLieuPreselect.setVisible(false);
				fieldDatePreselect.setVisible(false);
				fieldHeurePreselect.setVisible(false);
			}
		}else{
			cbMotivation.setBoxNeeded(false, null);
			fieldRang.setVisible(false);
			fieldRang.setRequired(false);
			fieldRang.setRequiredError(null);
			fieldRang.setValue(null);
			fieldLieuPreselect.setVisible(false);
			fieldDatePreselect.setVisible(false);
			fieldHeurePreselect.setVisible(false);
			fieldLieuPreselect.setValue(null);
			fieldDatePreselect.setValue(null);
			fieldHeurePreselect.setValue(null);
			fieldAppel.setVisible(false);
			fieldAppel.setValue(false);
			fieldComment.setValue(null);
			fieldComment.setVisible(false);
		}
		center();
	}
	
	/**
	 * Défini le 'ChangeCandidatureWindowListener' utilisé
	 * @param changeCandidatureWindowListener
	 */
	public void addChangeCandidatureWindowListener(ChangeCandidatureWindowListener changeCandidatureWindowListener) {
		this.changeCandidatureWindowListener = changeCandidatureWindowListener;
	}

	/**
	 * Interface pour récupérer une action
	 */
	public interface ChangeCandidatureWindowListener extends Serializable {
		
		/** Appelé si open est selectionné
		 * @param cand
		 */
		public void openCandidature(Candidature cand);

		/**
		 * Appelé lorsque tout autre action est envoyé --> mise a jour de la liste presentation de la window
		 * @param listeCandidature 
		 */
		public void action(List<Candidature> listeCandidature);
	}
}
