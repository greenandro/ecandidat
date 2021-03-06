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

import fr.univlorraine.ecandidat.controllers.PieceJustifController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.template.PieceJustifViewTemplate;

/**
 * Page de visu des PJ communes du centre de candidature
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CtrCandPieceJustifCommunView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandPieceJustifCommunView extends PieceJustifViewTemplate implements View{	

	/** serialVersionUID **/
	private static final long serialVersionUID = -3987439078767920106L;

	public static final String NAME = "ctrCandPieceJustifCommunView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient PieceJustifController pieceJustifController;
	@Resource
	private transient UserController userController;

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/*Récupération du centre de canidature en cours*/
		SecurityCtrCandFonc securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_PJ);
		if (securityCtrCandFonc==null || securityCtrCandFonc.getIdCtrCand()==null || securityCtrCandFonc.getReadOnly()==null){	
			setSizeFull();
			setMargin(true);
			setSpacing(true);
			addComponent(new Label(applicationContext.getMessage("erreurView.title", null, UI.getCurrent().getLocale())));
			return;
		}
		isVisuPjCommunMode = false;
		super.init();		
		
		titleParam.setValue(applicationContext.getMessage("pieceJustif.commun.title", null, UI.getCurrent().getLocale()));
		
		
		container.addAll(pieceJustifController.getPieceJustifsCommunScolEnService());
		buttonsLayout.setVisible(false);
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
		super.detach();
	}
}
