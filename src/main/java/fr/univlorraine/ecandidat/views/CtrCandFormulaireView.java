package fr.univlorraine.ecandidat.views;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.controllers.FormulaireController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.template.FormulaireViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des formulaires du centre de candidature
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CtrCandFormulaireView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandFormulaireView extends FormulaireViewTemplate implements View, EntityPushListener<Formulaire>{		

	/** serialVersionUID **/
	private static final long serialVersionUID = -1104739790401795184L;
	
	public static final String NAME = "ctrCandFormulaireView";
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormulaireController formulaireController;
	@Resource
	protected transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient EntityPusher<Formulaire> formulaireEntityPusher;
	
	
	/* Composants */
	private CentreCandidature ctrCand;

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/*Récupération du centre de canidature en cours*/
		SecurityCtrCandFonc securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FORMULAIRE);
		if (securityCtrCandFonc==null || securityCtrCandFonc.getIdCtrCand()==null || securityCtrCandFonc.getReadOnly()==null){	
			setSizeFull();
			setMargin(true);
			setSpacing(true);
			addComponent(new Label(applicationContext.getMessage("erreurView.title", null, UI.getCurrent().getLocale())));
			return;
		}
		super.init();
		
		((MainUI)UI.getCurrent()).checkConcordanceCentreCandidature(securityCtrCandFonc.getIdCtrCand());
		ctrCand = centreCandidatureController.getCentreCandidature(securityCtrCandFonc.getIdCtrCand());
		
		titleParam.setValue(applicationContext.getMessage("formulaire.ctrCand.title", new Object[]{ctrCand.getLibCtrCand()}, UI.getCurrent().getLocale()));
		
		btnNew.addClickListener(e -> {
			formulaireController.editNewFormulaire(ctrCand);
		});
		
		container.addAll(formulaireController.getFormulairesByCtrCand(ctrCand.getIdCtrCand()));
		
		/*Gestion du readOnly*/
		if (!securityCtrCandFonc.getReadOnly()){
			formulaireTable.addItemClickListener(e -> {
				if (e.isDoubleClick()) {
					formulaireTable.select(e.getItemId());
					btnEdit.click();
				}
			});
			buttonsLayout.setVisible(true);
		}else{
			buttonsLayout.setVisible(false);
		}
		
		/* Inscrit la vue aux mises à jour de formulaire */
		formulaireEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de formulaire */
		formulaireEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Formulaire entity) {
		if (ctrCand!=null && entity.getCentreCandidature()!=null && entity.getCentreCandidature().getIdCtrCand()==ctrCand.getIdCtrCand()){
			formulaireTable.removeItem(entity);
			formulaireTable.addItem(entity);
			formulaireTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Formulaire entity) {
		if (ctrCand!=null && entity.getCentreCandidature()!=null && entity.getCentreCandidature().getIdCtrCand()==ctrCand.getIdCtrCand()){
			formulaireTable.removeItem(entity);
			formulaireTable.addItem(entity);
			formulaireTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Formulaire entity) {
		if (ctrCand!=null && entity.getCentreCandidature()!=null && entity.getCentreCandidature().getIdCtrCand()==ctrCand.getIdCtrCand()){
			formulaireTable.removeItem(entity);
		}
	}
}
