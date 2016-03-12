package fr.univlorraine.ecandidat.views;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.template.CandidatureViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des candidatures du centre de candidature
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CtrCandCandidatureArchivedView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandCandidatureArchivedView extends CandidatureViewTemplate implements View, EntityPushListener<Candidature>{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -6279336917618333849L;
	
	public static final String NAME = "ctrCandCandidatureArchivedView";
	
	/* Injections */
	@Resource
	private transient EntityPusher<Candidature> candidatureEntityPusher;


	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		super.init(false, true, false, true);
		setTitleCtrCand("candidature.archived.title");
		/* Inscrit la vue aux mises à jour de candidature */
		candidatureEntityPusher.registerEntityPushListener(this);
	}
	

	
	@Override
	protected List<Candidature> getListeCandidature(Commission commission) {
		return ctrCandCandidatureController.getCandidatureByCommissionArchived(commission);
	}


	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		majContainer();
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
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Candidature entity) {
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Candidature entity) {
	}

}
