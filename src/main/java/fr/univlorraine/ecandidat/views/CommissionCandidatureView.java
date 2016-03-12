package fr.univlorraine.ecandidat.views;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.controllers.CommissionController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.services.security.SecurityCommission;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.template.CandidatureViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des candidatures pour la commission
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CommissionCandidatureView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_COMMISSION)
public class CommissionCandidatureView extends CandidatureViewTemplate implements View, EntityPushListener<Candidature>{


	/** serialVersionUID **/
	private static final long serialVersionUID = -1006929083802162687L;
	
	public static final String NAME = "commissionCandidatureView";
	
	/* Injections */
	@Resource
	private transient EntityPusher<Candidature> candidatureEntityPusher;
	
	@Resource
	private transient CommissionController commissionController;
	@Resource
	private transient UserController userController;
	
	private Commission commission;
	
	private Label labelError = new Label();


	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {		
		labelError.setValue(applicationContext.getMessage("erreurView.title", null, UI.getCurrent().getLocale()));
		labelError.setVisible(false);
		super.init(false, false, false, false);
		
		/* Inscrit la vue aux mises à jour de candidature */
		candidatureEntityPusher.registerEntityPushListener(this);
	}
	
	@Override
	protected List<Candidature> getListeCandidature(Commission commission) {
		return ctrCandCandidatureController.getCandidatureByCommission(commission);
	}

	@Override
	protected Commission getCommission() {
		return commission;		
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		/*Récupération de la commission en cours*/
		SecurityCommission securityCommission = userController.getCommission();
		if (securityCommission==null || securityCommission.getIdComm()==null){			
			commission = null;
		}else{
			commission = commissionController.getCommissionById(securityCommission.getIdComm());			
			((MainUI)UI.getCurrent()).checkConcordanceCommission(securityCommission.getIdComm());
		}
		if (commission==null){			
			addComponent(new Label(applicationContext.getMessage("erreurView.title", null, UI.getCurrent().getLocale())));
			return;
		}
		if (commission!=null){
			setTitle(applicationContext.getMessage("candidature.commission.title", new Object[]{commission.getLibComm()}, UI.getCurrent().getLocale()));
			majContainer();
			labelError.setVisible(false);
			switchToErrorMode(false);			
		}else{
			switchToErrorMode(true);
			labelError.setVisible(true);
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de candidature */
		candidatureEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Candidature entity) {
		removeEntity(entity);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Candidature entity) {
		if (entity.getDatAnnulCand()!=null){
			return;
		}
		removeEntity(entity);
		addEntity(entity);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Candidature entity) {
		if (!isEntityApartientCommission(entity)){
			return;
		}
		removeEntity(entity);
		if (entity.getDatAnnulCand()!=null){
			return;
		}
		entity.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(entity));
		addEntity(entity);
	}

}
