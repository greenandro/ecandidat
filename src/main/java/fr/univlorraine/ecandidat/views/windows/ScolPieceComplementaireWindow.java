package fr.univlorraine.ecandidat.views.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.FormationController;
import fr.univlorraine.ecandidat.controllers.FormulaireController;
import fr.univlorraine.ecandidat.controllers.PieceJustifController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire_;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif_;


/**
 * Fenêtre d'ajout de pièces complémentaires a une formation
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class ScolPieceComplementaireWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -2056861405725050563L;

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient PieceJustifController pieceJustifController;
	@Resource
	private transient FormulaireController formulaireController;
	@Resource
	private transient FormationController formationController;

	/* Composants */
	
	private Button btnEnregistrer;
	private Button btnAnnuler;
	
	/*PJ*/
	private ListSelect leftSelectPj;
	private ListSelect rightSelectPj;
	private ListSelect communSelectPj;
	private BeanItemContainer<PieceJustif> containerLeftPj; 
	private BeanItemContainer<PieceJustif> containerRightPj;
	private List<PieceJustif> listPj = new ArrayList<PieceJustif>();
	
	/*PJ*/
	private ListSelect leftSelectFormulaire;
	private ListSelect rightSelectFormulaire;
	private ListSelect communSelectFormulaire;
	private BeanItemContainer<Formulaire> containerLeftFormulaire; 
	private BeanItemContainer<Formulaire> containerRightFormulaire;
	private List<Formulaire> listFormulaire = new ArrayList<Formulaire>();

	/**
	 * Crée une fenêtre d'ajout de pièces complémentaires a une formation
	 * @param formation
	 * @param ctrCand
	 */
	@SuppressWarnings("unchecked")
	public ScolPieceComplementaireWindow(Formation formation, CentreCandidature ctrCand) {
		/* Style */
		setModal(true);
		setWidth(700,Unit.PIXELS);
		setImmediate(true);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setImmediate(true);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("formation.piececomp.window", null, UI.getCurrent().getLocale()));	
		
		/*Listes des PJ*/
		List<PieceJustif> listeRightPj = new ArrayList<PieceJustif>();
		List<PieceJustif> listeLeftPj = new ArrayList<PieceJustif>();
		
		/*Construction des listes*/
		if (formation.getPieceJustifs()!=null && formation.getPieceJustifs().size()!=0){
			pieceJustifController.getPieceJustifsByCtrCandAndScolCentral(ctrCand.getIdCtrCand()).forEach(e ->{
				Optional<PieceJustif> dp = new ArrayList<PieceJustif>(formation.getPieceJustifs()).stream().filter(pj -> pj.getCodPj().equals(e.getCodPj())).findFirst();
				if (dp.isPresent()){
					listeRightPj.add(e);
					listPj.add(e);
				}else{
					listeLeftPj.add(e);
				}
			});
		}else{
			listeLeftPj.addAll(pieceJustifController.getPieceJustifsByCtrCandAndScolCentral(ctrCand.getIdCtrCand()));
		}
		
		/*Containers*/
		containerLeftPj = new BeanItemContainer<PieceJustif>(PieceJustif.class, listeLeftPj);
		containerRightPj = new BeanItemContainer<PieceJustif>(PieceJustif.class, listeRightPj);
		
		/*Listtes de gauche et droite de PJ*/
		leftSelectPj = new ListSelect(applicationContext.getMessage("formation.piececomp.pj.dispo", null, UI.getCurrent().getLocale()));
		rightSelectPj = new ListSelect(applicationContext.getMessage("formation.piececomp.pj.select", null, UI.getCurrent().getLocale()));
		communSelectPj = new ListSelect(applicationContext.getMessage("formation.piececomp.pj.commun", null, UI.getCurrent().getLocale()));
		initListSelectPj(leftSelectPj,containerLeftPj);
		initListSelectPj(rightSelectPj,containerRightPj);
		initListSelectPj(communSelectPj,new BeanItemContainer<PieceJustif>(PieceJustif.class, pieceJustifController.getPieceJustifsCommunCtrCandEnService(ctrCand.getIdCtrCand())));
		communSelectPj.setEnabled(false);
		
		/*Layout bouton milieu PJ*/
		VerticalLayout layoutBtnPj = new VerticalLayout();
		//layoutBtnPj.setImmediate(true);
		layoutBtnPj.setHeight(100,Unit.PERCENTAGE);
		layoutBtnPj.setSpacing(true);
		Button btnGoRightPj = new Button(FontAwesome.ARROW_CIRCLE_RIGHT);
		Button btnGoLeftPj = new Button(FontAwesome.ARROW_CIRCLE_LEFT);
		layoutBtnPj.addComponent(btnGoRightPj);
		layoutBtnPj.setComponentAlignment(btnGoRightPj, Alignment.BOTTOM_CENTER);
		layoutBtnPj.addComponent(btnGoLeftPj);
		layoutBtnPj.setComponentAlignment(btnGoLeftPj, Alignment.TOP_CENTER);

		
		/*action du bouton mise à droite PJ*/
		btnGoRightPj.addClickListener(e->{
			Set<PieceJustif> collectionLeft = (Set<PieceJustif>) leftSelectPj.getValue();
			collectionLeft.forEach(pj -> {
				containerLeftPj.removeItem(pj);
				containerRightPj.addBean(pj);
				leftSelectPj.setValue(null);
				rightSelectPj.setValue(null);
				listPj.add(pj);
			});
		});
		
		/*action du bouton mise à gauche PJ*/
		btnGoLeftPj.addClickListener(e->{
			Set<PieceJustif> collectionRight = (Set<PieceJustif>) rightSelectPj.getValue();
			collectionRight.forEach(pj -> {
				containerRightPj.removeItem(pj);
				containerLeftPj.addBean(pj);
				leftSelectPj.setValue(null);
				rightSelectPj.setValue(null);
				listPj.remove(pj);
			});
		});
		
		
		/*Layout contenant les pj*/
		HorizontalLayout hlTwinSelectPj = new HorizontalLayout();
		hlTwinSelectPj.setImmediate(true);
		hlTwinSelectPj.setMargin(true);
		hlTwinSelectPj.setSpacing(true);
		hlTwinSelectPj.setWidth(100, Unit.PERCENTAGE);
		hlTwinSelectPj.addComponent(leftSelectPj);
		hlTwinSelectPj.setExpandRatio(leftSelectPj,1);
		hlTwinSelectPj.addComponent(layoutBtnPj);
		hlTwinSelectPj.setExpandRatio(layoutBtnPj,0.2f);
		hlTwinSelectPj.addComponent(rightSelectPj);
		hlTwinSelectPj.setExpandRatio(rightSelectPj,1);		
		hlTwinSelectPj.addComponent(communSelectPj);
		hlTwinSelectPj.setExpandRatio(communSelectPj,1);			
		
		/*Listes des Formulaires*/
		List<Formulaire> listeRightFormulaire = new ArrayList<Formulaire>();
		List<Formulaire> listeLeftFormulaire = new ArrayList<Formulaire>();
		
		/*Construction des listes*/
		if (formation.getFormulaires()!=null && formation.getFormulaires().size()!=0){
			formulaireController.getFormulairesByCtrCandAndScolCentral(ctrCand.getIdCtrCand()).forEach(e ->{
				Optional<Formulaire> dp = new ArrayList<Formulaire>(formation.getFormulaires()).stream().filter(formulaire -> formulaire.getCodFormulaire().equals(e.getCodFormulaire())).findFirst();
				if (dp.isPresent()){
					listeRightFormulaire.add(e);
					listFormulaire.add(e);
				}else{
					listeLeftFormulaire.add(e);
				}
			});
		}else{
			listeLeftFormulaire.addAll(formulaireController.getFormulairesByCtrCandAndScolCentral(ctrCand.getIdCtrCand()));
		}
		
		/*Containers*/
		containerLeftFormulaire = new BeanItemContainer<Formulaire>(Formulaire.class, listeLeftFormulaire);
		containerRightFormulaire = new BeanItemContainer<Formulaire>(Formulaire.class, listeRightFormulaire);
		
		/*Listtes de gauche et droite de formulaire*/
		leftSelectFormulaire = new ListSelect(applicationContext.getMessage("formation.piececomp.formulaire.dispo", null, UI.getCurrent().getLocale())); 
		rightSelectFormulaire = new ListSelect(applicationContext.getMessage("formation.piececomp.formulaire.select", null, UI.getCurrent().getLocale()));
		communSelectFormulaire = new ListSelect(applicationContext.getMessage("formation.piececomp.formulaire.commun", null, UI.getCurrent().getLocale()));
		initListSelectFormulaire(leftSelectFormulaire,containerLeftFormulaire);
		initListSelectFormulaire(rightSelectFormulaire,containerRightFormulaire);
		initListSelectFormulaire(communSelectFormulaire,new BeanItemContainer<Formulaire>(Formulaire.class, formulaireController.getFormulairesCommunCtrCandEnService(ctrCand.getIdCtrCand())));
		communSelectFormulaire.setEnabled(false);
		
		/*Layout bouton milieu formulaire*/
		VerticalLayout layoutBtnFormulaire = new VerticalLayout();
		layoutBtnFormulaire.setHeight(100,Unit.PERCENTAGE);
		layoutBtnFormulaire.setSpacing(true);
		Button btnGoRightFormulaire = new Button(FontAwesome.ARROW_CIRCLE_RIGHT);
		Button btnGoLeftFormulaire = new Button(FontAwesome.ARROW_CIRCLE_LEFT);
		layoutBtnFormulaire.addComponent(btnGoRightFormulaire);
		layoutBtnFormulaire.setComponentAlignment(btnGoRightFormulaire, Alignment.BOTTOM_CENTER);
		layoutBtnFormulaire.addComponent(btnGoLeftFormulaire);
		layoutBtnFormulaire.setComponentAlignment(btnGoLeftFormulaire, Alignment.TOP_CENTER);

		
		/*action du bouton mise à droite formulaire*/
		btnGoRightFormulaire.addClickListener(e->{
			Set<Formulaire> collectionLeft = (Set<Formulaire>) leftSelectFormulaire.getValue();
			collectionLeft.forEach(formulaire -> {
				containerLeftFormulaire.removeItem(formulaire);
				containerRightFormulaire.addBean(formulaire);
				leftSelectFormulaire.setValue(null);
				rightSelectFormulaire.setValue(null);
				listFormulaire.add(formulaire);
			});
		});
		
		/*action du bouton mise à gauche formulaire*/
		btnGoLeftFormulaire.addClickListener(e->{
			Set<Formulaire> collectionRight = (Set<Formulaire>) rightSelectFormulaire.getValue();
			collectionRight.forEach(formulaire -> {
				containerRightFormulaire.removeItem(formulaire);
				containerLeftFormulaire.addBean(formulaire);
				leftSelectFormulaire.setValue(null);
				rightSelectFormulaire.setValue(null);
				listFormulaire.remove(formulaire);
			});
		});
		
		
		/*Layout contenant les formulaire*/
		HorizontalLayout hlTwinSelectFormulaire = new HorizontalLayout();
		hlTwinSelectFormulaire.setImmediate(true);
		hlTwinSelectFormulaire.setMargin(true);
		hlTwinSelectFormulaire.setSpacing(true);
		hlTwinSelectFormulaire.setWidth(100, Unit.PERCENTAGE);
		hlTwinSelectFormulaire.addComponent(leftSelectFormulaire);
		hlTwinSelectFormulaire.setExpandRatio(leftSelectFormulaire,1);
		hlTwinSelectFormulaire.addComponent(layoutBtnFormulaire);
		hlTwinSelectFormulaire.setExpandRatio(layoutBtnFormulaire,0.2f);
		hlTwinSelectFormulaire.addComponent(rightSelectFormulaire);
		hlTwinSelectFormulaire.setExpandRatio(rightSelectFormulaire,1);
		hlTwinSelectFormulaire.addComponent(communSelectFormulaire);
		hlTwinSelectFormulaire.setExpandRatio(communSelectFormulaire,1);
		
		/*Sheet*/
		TabSheet sheet = new TabSheet();
		sheet.setImmediate(true);
		sheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		//sheet.addStyleName(StyleConstants.RESIZE_MAX_WIDTH);
		sheet.setSizeFull();
		sheet.addSelectedTabChangeListener(e->center());
		layout.addComponent(sheet);
		layout.setExpandRatio(sheet, 1);

		
		sheet.addTab(hlTwinSelectPj, applicationContext.getMessage("formation.piececomp.sheet.pj", null, UI.getCurrent().getLocale()));
		sheet.addTab(hlTwinSelectFormulaire, applicationContext.getMessage("formation.piececomp.sheet.formulaire", null, UI.getCurrent().getLocale()));
		
		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new Button(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnEnregistrer = new Button(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			/* Enregistre la langue saisie */
			formationController.savePiecesComplementaires(formation,listFormulaire,listPj);
			/* Ferme la fenêtre */
			close();
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}
	
	/** Initialise les listes de PJ
	 * @param listSelect
	 * @param container
	 */
	private void initListSelectPj(ListSelect listSelect, BeanItemContainer<PieceJustif> container){
		listSelect.setWidth(100, Unit.PERCENTAGE);
		listSelect.setMultiSelect(true);
		listSelect.setNullSelectionAllowed(false);
		listSelect.setContainerDataSource(container);
		listSelect.setImmediate(true);
		listSelect.setItemCaptionPropertyId(PieceJustif_.libPj.getName());
	}

	/** Initialise les listes de PJ
	 * @param listSelect
	 * @param container
	 */
	private void initListSelectFormulaire(ListSelect listSelect, BeanItemContainer<Formulaire> container){
		listSelect.setWidth(100, Unit.PERCENTAGE);
		listSelect.setMultiSelect(true);
		listSelect.setNullSelectionAllowed(false);
		listSelect.setContainerDataSource(container);
		listSelect.setImmediate(true);
		listSelect.setItemCaptionPropertyId(Formulaire_.libFormulaire.getName());
	}
}
