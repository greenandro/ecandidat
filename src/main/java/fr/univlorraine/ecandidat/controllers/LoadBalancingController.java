package fr.univlorraine.ecandidat.controllers;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.LoadBalancingReload;
import fr.univlorraine.ecandidat.entities.ecandidat.LoadBalancingReloadRun;
import fr.univlorraine.ecandidat.entities.ecandidat.LoadBalancingReloadRunPK;
import fr.univlorraine.ecandidat.repositories.LoadBalancingReloadRepository;
import fr.univlorraine.ecandidat.repositories.LoadBalancingReloadRunRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 *
 */
@Component
public class LoadBalancingController {
	
	/* Injections */
	private Logger logger = LoggerFactory.getLogger(LoadBalancingController.class);
	
	@Resource
	private transient TableRefController tableRefController;
	
	@Resource
	private transient OffreFormationController offreFormationController;
	
	@Resource
	private transient FaqController faqController;
	
	@Resource
	private transient LoadBalancingReloadRepository loadBalancingReloadRepository;
	
	@Resource
	private transient LoadBalancingReloadRunRepository loadBalancingReloadRunRepository;
	
	@Value("${load.balancing.gestionnaire.mode:}")
	private transient Boolean loadBalancingGestionnaireMode;
	
	@Value("${load.balancing.candidat.url:}")
	private transient String loadBalancingCandidatUrl;
	
	@Value("${load.balancing.candidat.id.instance:}")
	private transient String loadBalancingIdInstance;
	
	@Value("${app.url}")
	private transient String appUrl;
	
	/*LoadBalancing*/
	
	public Boolean isLoadBalancingGestionnaireMode(){
		if (loadBalancingGestionnaireMode!=null && loadBalancingGestionnaireMode){
			logger.trace("GestionnaireMode : true");
			return true;
		}
		logger.trace("GestionnaireMode : false");
		return false;
	}
	
	public Boolean isLoadBalancingCandidatMode(){
		if (loadBalancingGestionnaireMode!=null && !loadBalancingGestionnaireMode){
			logger.trace("CandidateMode : true");
			return true;
		}
		logger.trace("CandidateMode : false");
		return false;
	}
	
	/**
	 * @return l'id d'instance de l'application
	 */
	String getIdInstance(){
		if (loadBalancingIdInstance!=null && !loadBalancingIdInstance.equals("")){
			return loadBalancingIdInstance;
		}
		return "1";
	}
	
	/**
	 * @return l'url de l'application (ajoute un / a la fin)
	 */
	public String getApplicationPath(Boolean addSlash){
		if (addSlash){
			return MethodUtils.formatUrlApplication(appUrl);
		}
		return appUrl;
	}
	
	/**
	 * @return l'url de l'application candidat pour le loadbalancing (ajoute un / a la fin)
	 */
	public String getApplicationPathForCandidat(){
		if (isLoadBalancingGestionnaireMode() && loadBalancingCandidatUrl!=null){
			return MethodUtils.formatUrlApplication(loadBalancingCandidatUrl);
		}else{
			return getApplicationPath(true);
		}
	}
	
	/**
	 * Vérifie si un batch doit etre lancé depuis la dernière date de verification
	 * 1min --> 60000
	 * 2min --> 120000
	 * 5min --> 300000
	 */
	//@Scheduled(fixedRateString="300000")
	@Scheduled(fixedRateString="${load.balancing.refresh.fixedRate}")
    public void checkBatchLBRun() {
		if (isLoadBalancingCandidatMode()){
			String instance = getIdInstance();
			LocalDateTime now = LocalDateTime.now();
			List<LoadBalancingReloadRun> liste = loadBalancingReloadRunRepository.findByIdInstanceIdLbReloadRun(instance);
			if (liste!=null && liste.size()!=0){				
				LoadBalancingReloadRun loadBalancingReload = liste.get(0);
				LocalDateTime lastCheck = loadBalancingReload.getId().getDatLastCheckLbReloadRun();
				logger.trace("Vérification des données pour l'instance "+instance+" avant la "+lastCheck);
				List<LoadBalancingReload> listeToReload = loadBalancingReloadRepository.findByDatCreLbReloadAfterOrDatCreLbReload(lastCheck,lastCheck);
				listeToReload.forEach(e->{
					String code = e.getCodDataLbReload();
					logger.trace("Rechargement des données pour l'instance "+instance+" : code="+code);
					reloadData(code);
					logger.trace("Fin rechargement des données pour l'instance "+instance+" : code="+code);
				});
				loadBalancingReloadRunRepository.delete(loadBalancingReload);
			}
			loadBalancingReloadRunRepository.saveAndFlush(new LoadBalancingReloadRun(new LoadBalancingReloadRunPK(now, instance)));
		}		
    }
	
	/**
	 * Recharge toutes les données en cache au départ de l'appli
	 */
	public void reloadAllData(){
		String instance = getIdInstance();
		LocalDateTime now = LocalDateTime.now();
		tableRefController.getListPaysEnService();
		tableRefController.getPaysFrance();
		tableRefController.getListDepartementEnService();
		tableRefController.getListeTypDiplome();
		tableRefController.getListeCentreGestion();
		tableRefController.getListeBacOuxEqu();
		tableRefController.getListeDipAutCur();
		tableRefController.getListeMention();
		tableRefController.getListeMentionNivBac();
		tableRefController.getListeTypeResultat();
		tableRefController.getMapParametre();
		tableRefController.getLangueEnService();
		tableRefController.getLangueDefault();
		tableRefController.getListeTypeTraitement();
		tableRefController.getListeTypeAvis();
		tableRefController.getListeTypeStatut();
		tableRefController.getListeTypeStatutPiece();
		tableRefController.getListeCivilte();
		tableRefController.getListeDroitFonctionnaliteCandidature();
		faqController.getFaq();
		offreFormationController.getOdf();
		if (isLoadBalancingCandidatMode()){
			loadBalancingReloadRunRepository.deleteInBatch(loadBalancingReloadRunRepository.findByIdInstanceIdLbReloadRun(instance));
			loadBalancingReloadRunRepository.saveAndFlush(new LoadBalancingReloadRun(new LoadBalancingReloadRunPK(now, instance)));
		}
	}
	
	/** Recharge les données
	 * @param code
	 */
	private void reloadData(String code) {
		switch (code) {
		case ConstanteUtils.LB_RELOAD_TABLE_REF_PAYS:
			tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_PAYS);
			break;
		case ConstanteUtils.LB_RELOAD_TABLE_REF_DPT:
			tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_DEPARTEMENT);
			break;
		case ConstanteUtils.LB_RELOAD_TABLE_REF_TYPDIP:
			tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_TYPDIPLOME);
			break;
		case ConstanteUtils.LB_RELOAD_TABLE_REF_CGE:
			tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_CENTREGESTION);
			break;
		case ConstanteUtils.LB_RELOAD_TABLE_REF_BAC:
			tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_BAC_OU_EQU);
			break;
		case ConstanteUtils.LB_RELOAD_TABLE_REF_DIP:
			tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_DIP_AUT_CUR);
			break;
		case ConstanteUtils.LB_RELOAD_TABLE_REF_MENTION:
			tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_MENTION);
			break;
		case ConstanteUtils.LB_RELOAD_TABLE_REF_MENTBAC:
			tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_MENTION_BAC);
			break;
		case ConstanteUtils.LB_RELOAD_TABLE_REF_TYPRES:
			tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_TYPRESULTAT);
			break;
		case ConstanteUtils.LB_RELOAD_PARAM:
			tableRefController.reloadMapParametre();
			break;
		case ConstanteUtils.LB_RELOAD_LANGUE:
			tableRefController.reloadLangues();
			break;
		case ConstanteUtils.LB_RELOAD_FAQ:
			faqController.reloadFaq();
			break;
		case ConstanteUtils.LB_RELOAD_ODF:
			offreFormationController.reloadOdf();
			break;
		default:
			break;
		}
	}
	
	/** Demande aux autres instances de recharger la data
	 * @param code
	 */
	public void askToReloadData(String code){
		if (isLoadBalancingGestionnaireMode()){
			LocalDateTime now = LocalDateTime.now();
			LoadBalancingReload loadBalancingReload = loadBalancingReloadRepository.findOne(code);
			if (loadBalancingReload!=null){
				loadBalancingReload.setDatCreLbReload(now);
			}else{
				loadBalancingReload = new LoadBalancingReload(code,now);
			}
			loadBalancingReloadRepository.save(loadBalancingReload);
		}
	}
}
