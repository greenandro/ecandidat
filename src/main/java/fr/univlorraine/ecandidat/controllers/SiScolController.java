package fr.univlorraine.ecandidat.controllers;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolComBdi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolUtilisateur;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.repositories.SiScolAnneeUniRepository;
import fr.univlorraine.ecandidat.repositories.SiScolBacOuxEquRepository;
import fr.univlorraine.ecandidat.repositories.SiScolCentreGestionRepository;
import fr.univlorraine.ecandidat.repositories.SiScolComBdiRepository;
import fr.univlorraine.ecandidat.repositories.SiScolCommuneRepository;
import fr.univlorraine.ecandidat.repositories.SiScolDepartementRepository;
import fr.univlorraine.ecandidat.repositories.SiScolDipAutCurRepository;
import fr.univlorraine.ecandidat.repositories.SiScolEtablissementRepository;
import fr.univlorraine.ecandidat.repositories.SiScolMentionNivBacRepository;
import fr.univlorraine.ecandidat.repositories.SiScolMentionRepository;
import fr.univlorraine.ecandidat.repositories.SiScolPaysRepository;
import fr.univlorraine.ecandidat.repositories.SiScolTypDiplomeRepository;
import fr.univlorraine.ecandidat.repositories.SiScolTypResultatRepository;
import fr.univlorraine.ecandidat.repositories.SiScolUtilisateurRepository;
import fr.univlorraine.ecandidat.repositories.VersionRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.InfoWindow;
import fr.univlorraine.ecandidat.views.windows.InputWindow;

/** Batch de synchro siScol
 * @author Kevin Hergalant
 */
@Component
public class SiScolController {
	
	private Logger logger = LoggerFactory.getLogger(SiScolController.class);
	
	/*Le service SI Scol*/
	@Resource(name="${siscol.implementation}")
	private SiScolGenericService siScolService;
	
	@Resource
	private transient ApplicationContext applicationContext;
	
	@Resource
	private transient TableRefController tableRefController;
	
	/*Injection repository ecandidat*/
	@Resource
	private transient SiScolUtilisateurRepository siScolUtilisateurRepository;
	@Resource
	private transient SiScolTypDiplomeRepository siScolTypDiplomeRepository;
	@Resource
	private transient SiScolPaysRepository siScolPaysRepository;
	@Resource
	private transient SiScolMentionRepository siScolMentionRepository;
	@Resource
	private transient SiScolTypResultatRepository siScolTypResultatRepository;
	@Resource
	private transient SiScolMentionNivBacRepository siScolMentionNivBacRepository;
	@Resource
	private transient SiScolEtablissementRepository siScolEtablissementRepository;
	@Resource
	private transient SiScolDipAutCurRepository siScolDipAutCurRepository;
	@Resource
	private transient SiScolDepartementRepository siScolDepartementRepository;
	@Resource
	private transient SiScolCommuneRepository siScolCommuneRepository;
	@Resource
	private transient SiScolCentreGestionRepository siScolCentreGestionRepository;
	@Resource
	private transient SiScolBacOuxEquRepository siScolBacOuxEquRepository;
	@Resource
	private transient SiScolComBdiRepository siScolComBdiRepository;
	@Resource
	private transient VersionRepository versionRepository;
	@Resource
	private transient SiScolAnneeUniRepository siScolAnneeUniRepository;
	
	private static Boolean launchBatchWithListOption = true;
	
	/**
	 * Batch complet de synchro siScol
	 * @throws SiScolException 
	 */
	public void syncSiScol() throws SiScolException{
		logger.debug("Lancement du batch siScol");
		if (siScolService == null){
			return;
		}
		syncBacOuEqu();
		syncMention();
		syncCGE();
		syncUtilisateurs();
		syncDepartement();
		syncCommune();
		syncDipAutCur();
		syncPays();
		syncEtablissement();
		syncTypDiplome();
		syncTypResultat();
		syncMentionNivBac();
		syncComBdi();
		syncAnneeUni();
		syncVersion();
		logger.debug("Fin du batch siScol");
	}
	
	/** Synchronise les BacOuEqu
	 * @throws SiScolException 
	 */
	private void syncBacOuEqu() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncBacOuEqu");		
		List<SiScolBacOuxEqu> listeSiScol = siScolService.getListSiScolBacOuxEqu();
		if (listeSiScol == null){
			return;
		}
		if (launchBatchWithListOption){
			siScolBacOuxEquRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(bac ->siScolBacOuxEquRepository.saveAndFlush(bac));
		}
		
		tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_BAC_OU_EQU);
	}
	
	/** Synchronise les centres de gestion
	 * @throws SiScolException 
	 */
	private void syncCGE() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncCGE");
		List<SiScolCentreGestion> listeSiScol = siScolService.getListSiScolCentreGestion();
		if (listeSiScol == null){
			return;
		}
		if (launchBatchWithListOption){
			siScolCentreGestionRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(cge ->siScolCentreGestionRepository.saveAndFlush(cge));
		}
		
		tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_CENTREGESTION);
	}
	
	/** Synchronise les communes
	 * @throws SiScolException 
	 */
	private void syncCommune() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncCommune");
		List<SiScolCommune> listeSiScol = siScolService.getListSiScolCommune();
		if (listeSiScol == null){
			return;
		}
		if (launchBatchWithListOption){
			siScolCommuneRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(commune ->siScolCommuneRepository.saveAndFlush(commune));
		}		
	}
	
	/** Synchronise les departements
	 * @throws SiScolException 
	 */
	private void syncDepartement() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncDepartement");
		List<SiScolDepartement> listeSiScol = siScolService.getListSiScolDepartement();
		if (listeSiScol == null){
			return;
		}
		if (launchBatchWithListOption){
			siScolDepartementRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(departement ->siScolDepartementRepository.saveAndFlush(departement));
		}		
		tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_DEPARTEMENT);
	}
	
	/** Synchronise les DipAutCur
	 * @throws SiScolException 
	 */
	private void syncDipAutCur() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncDipAutCur");
		List<SiScolDipAutCur> listeSiScol = siScolService.getListSiScolDipAutCur();
		if (listeSiScol == null){
			return;
		}
		if (launchBatchWithListOption){
			siScolDipAutCurRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(dipAutCur ->siScolDipAutCurRepository.saveAndFlush(dipAutCur));
		}		
		tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_DIP_AUT_CUR);
	}
	
	/** Synchronise les etablissements
	 * @throws SiScolException 
	 */
	private void syncEtablissement() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncEtablissement");
		List<SiScolEtablissement> listeSiScol = siScolService.getListSiScolEtablissement();
		if (listeSiScol == null){
			return;
		}
		if (launchBatchWithListOption){
			siScolEtablissementRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(etablissement ->siScolEtablissementRepository.saveAndFlush(etablissement));
		}		
	}
	
	/** Synchronise les mentions
	 * @throws SiScolException 
	 */
	private void syncMention() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncMention");
		List<SiScolMention> listeSiScol = siScolService.getListSiScolMention();
		if (listeSiScol == null){
			return;
		}
		if (launchBatchWithListOption){
			siScolMentionRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(mention ->siScolMentionRepository.saveAndFlush(mention));
		}		
		tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_MENTION);
	}
	
	/** Synchronise les typResultats
	 * @throws SiScolException 
	 */
	private void syncTypResultat() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncTypResultat");
		List<SiScolTypResultat> listeSiScol = siScolService.getListSiScolTypResultat();
		if (listeSiScol == null){
			return;
		}
		if (launchBatchWithListOption){
			siScolTypResultatRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(typResultats ->siScolTypResultatRepository.saveAndFlush(typResultats));
		}		
		tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_TYPRESULTAT);
	}
	
	
	
	/** Synchronise les mentions niv bac
	 * @throws SiScolException 
	 */
	private void syncMentionNivBac() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncMentionNivBac");
		List<SiScolMentionNivBac> listeSiScol = siScolService.getListSiScolMentionNivBac();
		if (listeSiScol == null){
			return;
		}		
		if (launchBatchWithListOption){
			siScolMentionNivBacRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(mentionNivBac ->siScolMentionNivBacRepository.saveAndFlush(mentionNivBac));
		}
		tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_MENTION_BAC);
	}
	
	/** Synchronise les pays
	 * @throws SiScolException 
	 */
	private void syncPays() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncPays");
		List<SiScolPays> listeSiScol = siScolService.getListSiScolPays();
		if (listeSiScol == null){
			return;
		}
		if (launchBatchWithListOption){
			siScolPaysRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(pays ->siScolPaysRepository.saveAndFlush(pays));
		}
		
		tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_PAYS);
	}
	
	/** Synchronise les types de diplome
	 * @throws SiScolException 
	 */
	private void syncTypDiplome() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncTypDiplome");
		List<SiScolTypDiplome> listeSiScol = siScolService.getListSiScolTypDiplome();
		if (listeSiScol == null){
			return;
		}		
		if (launchBatchWithListOption){
			siScolTypDiplomeRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(typDiplome ->siScolTypDiplomeRepository.saveAndFlush(typDiplome));
		}
		tableRefController.clearSiScolTableRef(ConstanteUtils.TYP_REF_TYPDIPLOME);
	}
	
	/** Synchronise les utilisateurs
	 * @throws SiScolException 
	 */
	private void syncUtilisateurs() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncUtilisateurs");
		List<SiScolUtilisateur> listeSiScol = siScolService.getListSiScolUtilisateur();
		if (listeSiScol == null){
			return;
		}
		siScolUtilisateurRepository.deleteAllInBatch();
		
		/*Erreur de duplicate entry a toulouse et rennes*/		
		Exception ex = null;
		Integer i = 1;
		for (SiScolUtilisateur utilisateur : listeSiScol){
			utilisateur.setIdUti(i);			
			try{
				siScolUtilisateurRepository.saveAndFlush(utilisateur);
				i++;
			}catch(Exception e){
				ex = e;
			}
		}
		if (ex != null){
			logger.error("Erreur a l'insertion des utilisateurs", ex);
		}
	}
	
	/** Synchronise les combdi
	 * @throws SiScolException 
	 */
	private void syncComBdi() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncComBdi");
		List<SiScolComBdi> listeSiScol = siScolService.getListSiScolComBdi();
		if (listeSiScol == null){
			return;
		}		
		if (launchBatchWithListOption){
			siScolComBdiRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(comBdi ->siScolComBdiRepository.saveAndFlush(comBdi));
		}
	}
	
	/** Synchronise les annees universitaires
	 * @throws SiScolException 
	 */
	private void syncAnneeUni() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncAnneeUni");
		List<SiScolAnneeUni> listeSiScol = siScolService.getListSiScolAnneeUni();
		if (listeSiScol == null){
			return;
		}
		if (launchBatchWithListOption){
			siScolAnneeUniRepository.save(listeSiScol);
		}else{
			listeSiScol.forEach(anneUni ->siScolAnneeUniRepository.saveAndFlush(anneUni));
		}		
	}
	
	/** Synchronise la version apogée
	 * @throws SiScolException 
	 */
	private void syncVersion() throws SiScolException{
		logger.debug("Lancement du batch siScol-->syncVersion");
		Version version = siScolService.getVersion();
		if (version != null){
			version.setCodVersion(NomenclatureUtils.VERSION_SI_SCOL_COD);
			version.setDatVersion(LocalDateTime.now());
			versionRepository.save(version);			
		}
	}

	/**
	 * Test de la connexion
	 */
	public void testSiScolConnnexion() {		
		try {
			Version v = siScolService.getVersion();
			if (v!=null){
				Notification.show(applicationContext.getMessage("parametre.siscol.check.ok", new Object[]{v.getValVersion()}, UI.getCurrent().getLocale()));
			}else{
				Notification.show(applicationContext.getMessage("parametre.siscol.check.disable", null, UI.getCurrent().getLocale()));
			}		
		} catch (Exception e) {
			Notification.show(applicationContext.getMessage("parametre.siscol.check.ko", null, UI.getCurrent().getLocale()));
		}
	}
	
	/**
	 * Teste la connexion au WS Apogée
	 */
	public void testWSSiScolConnnexion() {
		InputWindow inputWindow = new InputWindow(applicationContext.getMessage("version.ws.message", null, UI.getCurrent().getLocale()), applicationContext.getMessage("version.ws.title", null, UI.getCurrent().getLocale()), false, 15);
		inputWindow.addBtnOkListener(text -> {
			if (text instanceof String && !text.isEmpty()) {
				if (text!=null){
					try {
						WSIndividu ind = siScolService.getIndividu(text, null, null);
						String ret = "Pas d'info";
						if(ind!=null){
							ret = "<u>Individu</u> : <br>"+ind+"<br><br><u>Adresse</u> : <br>"+ind.getAdresse()+
									"<br><br><u>Bac</u> : <br>"+ind.getBac()+"<br><br><u>Cursus interne</u> : <br>"+ind.getListCursusInterne();
						}
						
						UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("version.ws.result", null, UI.getCurrent().getLocale()), ret, 500, 70));
					} catch (Exception e) {
						Notification.show(applicationContext.getMessage("version.ws.error", null, UI.getCurrent().getLocale()),Type.WARNING_MESSAGE);
					}					
				}
			}
		});
		UI.getCurrent().addWindow(inputWindow);
	}
}
