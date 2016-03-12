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

import fr.univlorraine.ecandidat.controllers.FormulaireController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.template.FormulaireViewTemplate;

/**
 * Page de visu des formulaires communs du centre de candidature
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CtrCandFormulaireCommunView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandFormulaireCommunView extends FormulaireViewTemplate implements View{		

	/** serialVersionUID **/
	private static final long serialVersionUID = -1104739790401795184L;
	
	public static final String NAME = "ctrCandFormulaireCommunView";
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormulaireController formulaireController;
	@Resource
	private transient UserController userController;

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

		titleParam.setValue(applicationContext.getMessage("formulaire.commun.title", null, UI.getCurrent().getLocale()));
		
		container.addAll(formulaireController.getFormulairesCommunScolEnService());
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
