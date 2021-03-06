package fr.univlorraine.ecandidat.controllers.rest;

import java.util.Base64;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.LoadBalancingController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.CandidatCompteMinimaView;

/**
 * Contrôleur REST pour la gestion de l'entité candidat
 */
@Controller
@RequestMapping("/candidat")
public class CandidatRest {
	
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient UserController userController;

	/**
	 * valide le compte
	 */
	@RequestMapping(value="/dossier/{numDossierOpiEncode}", method=RequestMethod.GET)
	public String valideDossier(@PathVariable String numDossierOpiEncode) {
		String numDossierOpi = null;
		String mode = "";
		try{
			byte[] numDossierOpiByte = Base64.getUrlDecoder().decode(numDossierOpiEncode);
			numDossierOpi = new String(numDossierOpiByte);
			
			CompteMinima cptMin = candidatController.searchCptMinByNumDossier(numDossierOpi);
			if (cptMin!=null){
				if (cptMin.getTemValidCptMin()){
					mode = ConstanteUtils.REST_VALID_ALREADY_VALID;
				}else{
					cptMin.setTemValidCptMin(true);
					cptMin.setTemValidMailCptMin(true);
					candidatController.simpleSaveCptMin(cptMin);
					mode = ConstanteUtils.REST_VALID_SUCCESS;
					//userController.validSecurityUserCptMin();
				}
			}else{
				mode = ConstanteUtils.REST_VALID_CPT_NULL;
			}
		}catch (Exception e){
			mode = ConstanteUtils.REST_VALID_ERROR;
		}
		
		
		String path = loadBalancingController.getApplicationPath(true)+"#!"+CandidatCompteMinimaView.NAME+"/"+mode;
		return "redirect:"+path;
	}
	
	/**
	 * valide le mail
	 */
	@RequestMapping(value="/mail/{numDossierOpiEncode}", method=RequestMethod.GET)
	public String valideMail(@PathVariable String numDossierOpiEncode) {
		String numDossierOpi = null;
		String mode = "";
		try{
			byte[] numDossierOpiByte = Base64.getUrlDecoder().decode(numDossierOpiEncode);
			numDossierOpi = new String(numDossierOpiByte);
			
			if (numDossierOpi!=null){
				CompteMinima cptMin = candidatController.searchCptMinByNumDossier(numDossierOpi);
				mode = "";
				if (cptMin!=null){
					if (cptMin.getTemValidMailCptMin()){
						mode = ConstanteUtils.REST_VALID_ALREADY_VALID;
					}else{
						cptMin.setTemValidMailCptMin(true);
						candidatController.simpleSaveCptMin(cptMin);
						mode = ConstanteUtils.REST_VALID_SUCCESS;
						//userController.validSecurityUserMail(cptMin, true);
					}
				}else{
					mode = ConstanteUtils.REST_VALID_CPT_NULL;
				}
			}
		}catch (Exception e){
			mode = ConstanteUtils.REST_VALID_ERROR;
		}
		
		String path = loadBalancingController.getApplicationPath(true)+"#!"+CandidatCompteMinimaView.NAME+"/"+mode;
		return "redirect:"+path;
	}
}
