package fr.univlorraine.ecandidat.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.repositories.CompteMinimaRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.repositories.OpiRepository;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.bean.mail.CandidatureMailBean;

/**
 * Traitement des candidatures (opi, etc..)
 * @author Kevin Hergalant
 */
@Component
public class CandidatureGestionController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CandidatureCtrCandController ctrCandCandidatureController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient DemoController demoController;
	
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient OpiRepository opiRepository;
	@Resource
	private transient CompteMinimaRepository compteMinimaRepository;

	@Resource
	private transient DateTimeFormatter formatterDate;
	
	/*Le service SI Scol*/
	@Resource(name="${siscol.implementation}")
	private SiScolGenericService siScolService;
	
	
	/** Genere un opi si besoin
	 * @param candidature
	 */
	public void generateOpi(Candidature candidature) {
		if (candidature==null){
			return;
		}
		TypeDecisionCandidature lastTypeDecision = candidatureController.getLastTypeDecisionCandidature(candidature);
		if (parametreController.getIsUtiliseOpi() && lastTypeDecision.getTypeDecision().getTemDeverseOpiTypDec() && !demoController.getDemoMode()){
			Opi opi = opiRepository.findOne(candidature.getIdCand());
			if (opi==null){
				opi = opiRepository.save(new Opi(candidature));
				candidature.setOpi(opi);
				if (parametreController.getOpiImmediat()){
					siScolService.creerOpiViaWS(candidature.getCandidat(),userController.getCurrentUserLogin());
				}				
			}			
		}		
	}
	
	/**  Si un candidat rejette une candidature, le premier de la liste comp est pris
	 * @param formation
	 */
	public void candidatFirstCandidatureListComp(Formation formation){
		formation = formationRepository.findOne(formation.getIdForm());
		if (!formation.getTemListCompForm() || formation.getTypeDecisionFavListComp()==null){
			return;
		}
		
		formation.getCandidatures().stream().forEach(e->e.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(e)));
		Optional<Candidature> optCand = formation.getCandidatures().stream().filter(e->
											e.getLastTypeDecision()!=null 
											&& e.getLastTypeDecision().getTemValidTypeDecCand()
											&& e.getLastTypeDecision().getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisListComp())
											&& e.getLastTypeDecision().getListCompRangTypDecCand()!=null)
										.sorted((e1,e2)->(e1.getLastTypeDecision().getListCompRangTypDecCand().compareTo(e2.getLastTypeDecision().getListCompRangTypDecCand())))
										.findFirst();
		if (optCand.isPresent()){
			Candidature candidature = optCand.get();
			ctrCandCandidatureController.saveTypeDecisionCandidature(optCand.get(), formation.getTypeDecisionFavListComp(), true, "autoListComp");
			CandidatureMailBean candMailBean = mailController.getCandidatureMailBean(candidature);
			mailController.sendMail(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),formation.getTypeDecisionFavListComp().getMail(),null,candMailBean);
		}
	}
	
	/**
	 * Lance le batch de destruction des dossiers
	 */
	public void launchBatchDestructDossier() throws FileException{
		List<Campagne> listeCamp =  campagneController.getCampagnes().stream().filter(e->(e.getDatDestructEffecCamp()==null && e.getDatArchivCamp()!=null)).collect(Collectors.toList());
		for (Campagne campagne : listeCamp){
			if (campagneController.getDateDestructionDossier(campagne).isBefore(LocalDateTime.now())){
				for (CompteMinima cptMin : campagne.getCompteMinimas()){
					if (cptMin.getCandidat()!=null){
						for (Candidature candidature : cptMin.getCandidat().getCandidatures()){
							for (PjCand pjCand : candidature.getPjCands()){
								candidaturePieceController.removeFileToPj(pjCand);
							}
						}
						
					}
					compteMinimaRepository.delete(cptMin);					
				}
				campagneController.saveDateDestructionCampagne(campagne);
			}
		}
	}
	
	/**
	 * Lance le batch de creation d'OPI asynchrone
	 */
	public void launchBatchAsyncOPI(){
		List<Opi> listeOpi = opiRepository.findByDatPassageOpiIsNull();
		List<Candidat> listeCandidat = listeOpi.stream().map(e->e.getCandidature().getCandidat()).distinct().collect(Collectors.toList());
		listeCandidat.forEach(e->{
			siScolService.creerOpiViaWS(e,"batch-eCandidat");
		});
	}
}
