package fr.univlorraine.ecandidat.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.HistoNumDossier;
import fr.univlorraine.ecandidat.repositories.CompteMinimaRepository;
import fr.univlorraine.ecandidat.repositories.FichierRepository;
import fr.univlorraine.ecandidat.repositories.HistoNumDossierRepository;
import fr.univlorraine.ecandidat.services.file.FileManager;
import fr.univlorraine.ecandidat.services.security.SecurityUserCandidat;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.PasswordHashUtils;
import fr.univlorraine.ecandidat.views.windows.CandidatureWindow;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 *
 */
@Component
public class TestController {
	private Logger logger = LoggerFactory.getLogger(TestController.class);
	
	/* Injections */
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient UiController uiController;
	@Resource
	private transient AdresseController adresseController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient CompteMinimaRepository compteMinimaRepository;
	@Resource
	private transient HistoNumDossierRepository histoNumDossierRepository;
	@Resource
	private transient UserController userController;
	@Resource
	private transient FileManager fileManager;
	@Resource
	private transient FichierRepository fichierRepository;
	
	@Value("${enableTestMode:}")
	private transient Boolean enableTestMode;

	public Boolean isTestMode(){
		if (enableTestMode==null){
			return false;
		}
		return enableTestMode;
	}
	
	public void openCandidature(){
		SecurityUserCandidat cand = userController.getSecurityUserCandidat();
		if (cand!=null){			
			CompteMinima cpt = compteMinimaRepository.findOne(cand.getIdCptMin());
			if (cpt!=null && cpt.getCandidat()!=null && cpt.getCandidat().getCandidatures().size()>0){
				logger.debug("openCandidature : "+cpt.getNumDossierOpiCptMin());
				Candidature candidature = cpt.getCandidat().getCandidatures().get(0);
				CandidatureWindow cw = new CandidatureWindow(candidature,true, false, false);
				UI.getCurrent().addWindow(cw);
				cw.close();
			}
		}
	}
	
	public void allInOne(){
		candidatToFormation();
		openCandidature();
		downloadDossier();
		deleteCandidat();
		finish();
	}
	
	public void afficheFichierPerdu(){
		fichierRepository.findAll().forEach(e->{
			try{
				if(e.getTypFichier().equals("C")){
					fileManager.getInputStreamFromFile(e);
				}				
			}catch(Exception ex){
				//System.out.println(e.getIdFichier());
			}
			
		});
		
	}
	
	public void deleteCandidat() {		
		SecurityUserCandidat cand = userController.getSecurityUserCandidat();
		if (cand!=null){
			CompteMinima cpt = compteMinimaRepository.findOne(cand.getIdCptMin());
			if (cpt!=null){
				logger.debug("Delete compte NoDossier = "+cpt.getNumDossierOpiCptMin());
				/*if (cpt.getCandidat().getCandidatBacOuEqu()!=null){
					candidatBacOuEquRepository.delete(cpt.getCandidat().getCandidatBacOuEqu());
				}*/
				/*cpt.getCandidat().getCandidatures().forEach(e->{
					candidatureRepository.delete(e);
				})*/;
				//candidatRepository.delete(cpt.getCandidat());
				//cpt.setCandidat(null);
				compteMinimaRepository.delete(cpt);
				uiController.unregisterUiCandidat((MainUI)UI.getCurrent());
				SecurityContext context = SecurityContextHolder.createEmptyContext();
				SecurityContextHolder.setContext(context);
				UI.getCurrent().getSession().getSession()
						.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
				MainUI current = (MainUI) UI.getCurrent();
				uiController.registerUiCandidat(current);
				current.navigateToAccueilView();
			}
		}
	}
	
	public void finish(){
		logger.debug("Finish");
	}
	
	public void candidatToFormation(){
		candidatureController.candidatToFormation(1, null, true);
	}
	
	public void downloadDossier(){		
		SecurityUserCandidat cand = userController.getSecurityUserCandidat();
		if (cand!=null){			
			CompteMinima cpt = compteMinimaRepository.findOne(cand.getIdCptMin());
			logger.debug("Download dossier : "+cpt.getNumDossierOpiCptMin());
			if (cpt!=null && cpt.getCandidat()!=null && cpt.getCandidat().getCandidatures().size()>0){
				logger.debug("Download dossier candidat : "+cpt.getNumDossierOpiCptMin());
				Candidature candidature = cpt.getCandidat().getCandidatures().get(0);
				candidatureController.downloadDossier(candidature, candidatureController.getInformationsCandidature(candidature, false), candidatureController.getInformationsDateCandidature(candidature, false), 
						adresseController.getLibelleAdresseCommission(candidature.getFormation().getCommission(), "<br>"), candidaturePieceController.getPjCandidature(candidature),candidaturePieceController.getFormulaireCandidature(candidature));
			}
		}
	}
	
	/*public void createMultipleCptMin(){
		for (int i = 0;i<200;i++){
			createCompteMinima();
		}
	}*/
	
	/*public CompteMinima getRandomCptMin(List<CompteMinima> liste){
		Random randomGenerator = new Random();
		List<CompteMinima> listeWithout = new ArrayList<CompteMinima>();
		liste.forEach(e->{
			if (e.getIdCptMin()>18){
				listeWithout.add(e);
			}
		});
		int index = randomGenerator.nextInt(listeWithout.size());
		return listeWithout.get(index); 
	}*/
	
	public CompteMinima createCompteMinima(){
		CompteMinima cptMin = compteMinimaRepository.findByNumDossierOpiCptMin("1QJ5A59F");
		Campagne campagne = campagneController.getCampagneActive();
		cptMin.setIdCptMin(null);
		cptMin.setSupannEtuIdCptMin(null);
		cptMin.setLoginCptMin(null);
		cptMin.setCampagne(campagne);
		String prefix = parametreController.getPrefixeNumDossCpt();
		Integer sizeNumDossier = ConstanteUtils.GEN_SIZE;
		if (prefix!=null){
			sizeNumDossier = sizeNumDossier-prefix.length();
		}
		
		String numDossierGenere = PasswordHashUtils.generateRandomPassword(sizeNumDossier,ConstanteUtils.GEN_NUM_DOSS);
		
		while(isNumDossierExist(numDossierGenere)){
			numDossierGenere = PasswordHashUtils.generateRandomPassword(sizeNumDossier,ConstanteUtils.GEN_NUM_DOSS);
		}
		
		if (prefix!=null){
			numDossierGenere = prefix+numDossierGenere;
		}		
		cptMin.setNumDossierOpiCptMin(numDossierGenere);
		try {
			cptMin.setPwdCptMin(PasswordHashUtils.createHash("123"));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		/*La date avant destruction*/
		LocalDateTime datValid = LocalDateTime.now();
		Integer nbJourToKeep = parametreController.getNbJourKeepCptMin();		
		datValid = datValid.plusDays(nbJourToKeep);
		datValid = LocalDateTime.of(datValid.getYear(), datValid.getMonth(), datValid.getDayOfMonth(), 23, 0,0);		
		cptMin.setDatFinValidCptMin(datValid);
		String numDossier  = cptMin.getNumDossierOpiCptMin();
		if (numDossier==null || numDossier.equals("")){
			return null;
		}
		cptMin = compteMinimaRepository.saveAndFlush(cptMin);
		histoNumDossierRepository.saveAndFlush(new HistoNumDossier(numDossier, campagne.getCodCamp()));
		logger.debug("Creation compte NoDossier = "+cptMin.getNumDossierOpiCptMin());
		return cptMin;
	}
	
	/** Vérifie qu'un dossier existe
	 * @param numDossier
	 * @return true si le numDossier existe deja
	 */
	private Boolean isNumDossierExist(String numDossier){		
		CompteMinima cptMin = compteMinimaRepository.findByNumDossierOpiCptMin(numDossier);
		if (cptMin != null || histoNumDossierRepository.exists(numDossier)){
			return true;
		}
		return false;
	}
	
	/*public void createCandidats(){
		CompteMinima cptMin = compteMinimaRepository.findByNumDossierOpiCptMin("1QJ5A59F");
		Candidat candidat = cptMin.getCandidat();
		candidat.setIneCandidat(null);
		candidat.setCleIneCandidat(null);
		candidat.setIdCandidat(null);
		candidat.setTemUpdatableCandidat(true);
		
		Adresse adresse = candidat.getAdresse();
		
		List<CompteMinima> liste = compteMinimaRepository.findAll();
		liste.forEach(e->{
			if (e.getIdCptMin()>17){
				candidat.setCompteMinima(e);
				adresse.setIdAdr(null);
				candidat.setAdresse(adresse);		
				candidatRepository.save(candidat);				
			}
		});
	}*/
}
