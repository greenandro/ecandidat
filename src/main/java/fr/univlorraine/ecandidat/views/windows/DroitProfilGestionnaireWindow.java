package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * Fenêtre de recherche d'individu Ldap
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class DroitProfilGestionnaireWindow extends DroitProfilIndividuWindow {

	
	/** serialVersionUID **/
	private static final long serialVersionUID = -1102784822092848358L;
		
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TableRefController tableRefController;
	

	/* Composants */
	private TextField tfLoginApogee;
	private RequiredComboBox<SiScolCentreGestion> comboBoxCGE;
	private CheckBox cbIsAllCommission;
	private ListSelect selectCommission;
	

	/*Listener*/
	private DroitProfilGestionnaireListener droitProfilGestionnaireListener;


	/** Constructeur de la fenêtre de profil pour gestionnaire
	 * @param gestionnaire
	 */
	public DroitProfilGestionnaireWindow(Gestionnaire gestionnaire) {
		this(gestionnaire.getCentreCandidature());
		switchToModifMode(gestionnaire);
	}
	
	/**Constructeur de la fenêtre de profil pour gestionnaire
	 * @param ctrCand
	 */
	public DroitProfilGestionnaireWindow(CentreCandidature ctrCand) {
		super(NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE);
		setHeight(650, Unit.PIXELS);
		
		
		/*Login Apogee pour les gestionnaires*/
		tfLoginApogee = new TextField(applicationContext.getMessage("droitprofilind.table.individu.loginApoInd", null, Locale.getDefault()),"");
		tfLoginApogee.setNullRepresentation("");
		tfLoginApogee.setMaxLength(20);
		tfLoginApogee.setWidth(280,Unit.PIXELS);
		addOption(tfLoginApogee);
		
		/*Lise de CGE  pour les gestionnaires*/
		comboBoxCGE = new RequiredComboBox<SiScolCentreGestion>(tableRefController.getListeCentreGestion(),SiScolCentreGestion.class);
		comboBoxCGE.setNullSelectionAllowed(true);
		comboBoxCGE.setCaption(applicationContext.getMessage("window.search.people.cge", null, Locale.getDefault()));
		comboBoxCGE.setWidth(100, Unit.PERCENTAGE);
		addOption(comboBoxCGE);
		
		/*CheckBox isAllCommission pour les commissions*/
		cbIsAllCommission = new CheckBox(applicationContext.getMessage("droitprofilind.table.individu.isAllComm", null, Locale.getDefault()));	
		cbIsAllCommission.setImmediate(true);
		cbIsAllCommission.addValueChangeListener(e->{
			selectCommission.setValue(null);
			selectCommission.setVisible(!cbIsAllCommission.getValue());
		});
		addOption(cbIsAllCommission, Alignment.MIDDLE_LEFT);
		
		/*NativeSelect isAllCommission pour les commissions*/
		selectCommission = new ListSelect("Commissions");
		selectCommission.setImmediate(true);
		selectCommission.setWidth(100, Unit.PERCENTAGE);
		selectCommission.setMultiSelect(true);
		selectCommission.setRows(10);
		selectCommission.setItemCaptionPropertyId(ConstanteUtils.GENERIC_LIBELLE);
		selectCommission.setContainerDataSource(new BeanItemContainer<Commission>(Commission.class,ctrCand.getCommissions()));		
		addOption(selectCommission);
		
		cbIsAllCommission.setValue(false);
	}

	/**
	 * Vérifie les données et si c'est ok, fait l'action du listener
	 */
	@Override
	protected void performAction(){
		if (droitProfilGestionnaireListener != null && checkData()){
			Individu individu = getIndividu();
			DroitProfil droit = getDroitProfil();
			if ((isModificationMode &&  droit!=null) || (!isModificationMode && individu!=null && droit!=null)){
				String loginApogee = tfLoginApogee.getValue();
				if (loginApogee!=null && loginApogee.equals("")){
					loginApogee = null;
				}
				SiScolCentreGestion cge = (SiScolCentreGestion)comboBoxCGE.getValue();
				if (loginApogee!=null && !loginApogee.equals("") && cge!=null){
					Notification.show(applicationContext.getMessage("window.search.people.login.cge", null, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
					return;
				}
				
				Boolean isAllCommission = cbIsAllCommission.getValue();
				@SuppressWarnings("unchecked")
				Set<Commission> setCommission = (Set<Commission>) selectCommission.getValue(); 
				if (!isAllCommission && setCommission.size()==0){
					Notification.show(applicationContext.getMessage("window.search.people.comm.allorone", null, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
					return;
				}
				
				droitProfilGestionnaireListener.btnOkClick(individu,droit,loginApogee,cge, isAllCommission, setCommission.stream().collect(Collectors.toList()));
				close();
			}				
		}
	}
	
	/** Passe en mode modif
	 * @param gestionnaire
	 */
	protected void switchToModifMode(Gestionnaire gestionnaire) {
		super.switchToModifMode(gestionnaire.getDroitProfilInd());
		if (gestionnaire!=null){
			tfLoginApogee.setValue(gestionnaire.getLoginApoGest());
			comboBoxCGE.setValue(gestionnaire.getSiScolCentreGestion());
			cbIsAllCommission.setValue(gestionnaire.getTemAllCommGest());
			selectCommission.setValue(gestionnaire.getCommissions().stream().collect(Collectors.toSet()));
		}
	}
	

	/**
	 * Défini le 'DroitProfilGestionnaireListener' utilisé
	 * @param droitProfilGestionnaireListener
	 */
	public void addDroitProfilGestionnaireListener(DroitProfilGestionnaireListener droitProfilGestionnaireListener) {
		this.droitProfilGestionnaireListener = droitProfilGestionnaireListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui
	 */
	public interface DroitProfilGestionnaireListener extends Serializable {
		
		/** Appelé lorsque Oui est cliqué.
		 * @param individu
		 * @param droit
		 * @param loginApo
		 * @param centreGestion
		 * @param isAllCommission
		 * @param listCommission
		 */
		public void btnOkClick(Individu individu, DroitProfil droit, String loginApo, SiScolCentreGestion centreGestion, Boolean isAllCommission, List<Commission> listCommission);

	}

}
