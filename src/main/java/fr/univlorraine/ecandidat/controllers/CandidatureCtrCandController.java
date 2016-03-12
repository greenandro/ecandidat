package fr.univlorraine.ecandidat.controllers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusInterne;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCand;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionCandidatureRepository;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.export.ExportListCandidatureOption;
import fr.univlorraine.ecandidat.utils.bean.mail.AvisMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.CandidatureMailBean;
import fr.univlorraine.ecandidat.views.windows.CtrCandActionCandidatureWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandActionCandidatureWindow.ChangeCandidatureWindowListener;
import fr.univlorraine.ecandidat.views.windows.CtrCandShowHistoWindow;
import net.sf.jett.transform.ExcelTransformer;

/**
 * Gestion des candidatures pour un gestionnaire
 * @author Kevin Hergalant
 */
@Component
public class CandidatureCtrCandController {	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatureRepository candidatureRepository;
	@Resource
	private transient TypeDecisionCandidatureRepository typeDecisionCandidatureRepository;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient LockCandidatController lockCandidatController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	
	@Resource
	private transient DateTimeFormatter formatterDate;
	
	/**
	 * @param commission 
	 * @return les candidatures par commission
	 */
	public List<Candidature> getCandidatureByCommission(Commission commission){
		Campagne campagneEnCours =  campagneController.getCampagneActive();
		if (campagneEnCours==null){
			return new ArrayList<Candidature>();
		}
		List<Candidature> liste = candidatureRepository.findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNull(commission.getIdComm(),campagneEnCours.getCodCamp());
		traiteListe(liste);
		return liste;
	}
	
	/**
	 * @return les candidatures annulées par centre
	 */
	public List<Candidature> getCandidatureByCommissionCanceled(Commission commission){
		Campagne campagneEnCours =  campagneController.getCampagneActive();
		if (campagneEnCours==null){
			return new ArrayList<Candidature>();
		}
		List<Candidature> liste = candidatureRepository.findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNotNull(commission.getIdComm(),campagneEnCours.getCodCamp());
		traiteListe(liste);
		return liste;
	}
	
	/**
	 * @return les candidatures archivées par centre
	 */
	public List<Candidature> getCandidatureByCommissionArchived(Commission commission) {
		List<Candidature> liste = candidatureRepository.findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneDatArchivCampIsNotNull(commission.getIdComm());
		traiteListe(liste);
		return liste;
	}
	
	/** Ajoute le dernier type de decision a toutes les candidatures
	 * @param liste
	 */
	private void traiteListe(List<Candidature> liste){
		liste.forEach(e->{
			e.setCheck(false);
			e.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(e));
		});
	}
	
	/**
	 * @param listeCandidature
	 * @return true si la liste comporte un lock
	 */
	private Boolean checkLockListCandidature(List<Candidature> listeCandidature){
		for (Candidature candidature : listeCandidature){
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return true;
			}
		}		
		return false;
	}
	
	/** Unlock une liste de candidature
	 * @param listeCandidature
	 */
	private void unlockListCandidature(List<Candidature> listeCandidature){
		listeCandidature.forEach(e->lockCandidatController.releaseLockCandidature(e));
	}

	/** Edite les types de traitement de candidatures
	 * @param listeCandidature
	 * @param typeTraitement
	 * @param enMasse 
	 */
	public Boolean editListCandidatureTypTrait(List<Candidature> listeCandidature,
			TypeTraitement typeTraitement, Boolean enMasse) {
		if (checkLockListCandidature(listeCandidature)){
			return false;
		}
		Integer nb = listeCandidature.size();
		for (Candidature candidature : listeCandidature){
			if (nb>1 && candidature.getLastTypeDecision()!=null && candidature.getLastTypeDecision().getTemValidTypeDecCand() != null && candidature.getLastTypeDecision().getTemValidTypeDecCand()){
				Notification.show(applicationContext.getMessage("candidature.editTypTrait.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return false;
			}
		}
		String user = userController.getCurrentUserLogin();
		
		for (Candidature e : listeCandidature){
			Assert.notNull(e);
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(e)) {
				continue;
			}
			if (!e.getTypeTraitement().equals(typeTraitement)){
				e.setTypeTraitement(typeTraitement);
				e.setTemValidTypTraitCand(false);
				e.setUserModCand(user);
				e.setDatModCand(LocalDateTime.now());
				candidatureRepository.save(e);
			}
		}
		
		Notification.show(applicationContext.getMessage("candidature.editTypTrait.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		if (enMasse){
			unlockListCandidature(listeCandidature);
		}	
		return true;
	}
	
	/**Valide les types de traitement de candidatures
	 * @param listeCandidature
	 * @param enMasse 
	 */
	public Boolean validTypTrait(List<Candidature> listeCandidature, Boolean enMasse) {
		if (checkLockListCandidature(listeCandidature)){
			return false;
		}
		String user = userController.getCurrentUserLogin();
		
		for (Candidature candidature : listeCandidature){
			Assert.notNull(candidature);
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}
			if (!candidature.getTemValidTypTraitCand()){
				candidature.setTemValidTypTraitCand(true);
				candidature.setUserModCand(user);
				candidature.setDatModCand(LocalDateTime.now());
				TypeTraitement typeTraitement = candidature.getTypeTraitement();
				String typeMail = "";
				if (typeTraitement.equals(tableRefController.getTypeTraitementAccesDirect())){
					typeMail = NomenclatureUtils.MAIL_TYPE_TRAIT_AD;
					TypeDecisionCandidature tdc = saveTypeDecisionCandidature(candidature,candidature.getFormation().getTypeDecisionFav(), true, user);
					candidature.getTypeDecisionCandidatures().add(tdc);
					candidature.setLastTypeDecision(tdc);		
				}else if(typeTraitement.equals(tableRefController.getTypeTraitementAccesControle())){
					typeMail = NomenclatureUtils.MAIL_TYPE_TRAIT_AC;
				}else{
					typeMail = NomenclatureUtils.MAIL_TYPE_TRAIT_ATT;
				}
				candidatureRepository.save(candidature);
				CandidatureMailBean mailBean = mailController.getCandidatureMailBean(candidature);
				mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),typeMail,null,mailBean);	
			}
		}

		Notification.show(applicationContext.getMessage("candidature.validTypTrait.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		if (enMasse){
			unlockListCandidature(listeCandidature);
		}		
		return true;
	}
	
	/** Enregistre un type de decision pour une candidature
	 * @param candidature
	 * @param typeDecision
	 * @return le TypeDecision appliqué
	 */
	public TypeDecisionCandidature saveTypeDecisionCandidature(Candidature candidature, TypeDecision typeDecision, Boolean valid, String user){
		TypeDecisionCandidature typeDecisionCandidature = new TypeDecisionCandidature(candidature, typeDecision);
		typeDecisionCandidature.setTemValidTypeDecCand(valid);
		typeDecisionCandidature.setUserCreTypeDecCand(user);
		typeDecisionCandidature.setTemAppelTypeDecCand(false);
		MethodUtils.validateBean(typeDecisionCandidature, LoggerFactory.getLogger(this.getClass()));
		return typeDecisionCandidatureRepository.save(typeDecisionCandidature);
	}

	/**Edite les avis de candidatures
	 * @param listeCandidature
	 * @param typeDecisionCandidature
	 * @param enMasse 
	 * @return true si tout s'est bien passé
	 */
	public Boolean editAvis(List<Candidature> listeCandidature,
			TypeDecisionCandidature typeDecisionCandidature, Boolean enMasse) {
		if (checkLockListCandidature(listeCandidature)){
			return false;
		}
		Integer nb = listeCandidature.size();
		for (Candidature candidature : listeCandidature){
			if (nb>1 && candidature.getLastTypeDecision()!=null && candidature.getLastTypeDecision().getTemValidTypeDecCand() != null && candidature.getLastTypeDecision().getTemValidTypeDecCand()){
				Notification.show(applicationContext.getMessage("candidature.editAvis.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return false;
			}
		}
		String user = userController.getCurrentUserLogin();
		
		for (Candidature e : listeCandidature){
			Assert.notNull(e);
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(e)) {
				continue;
			}
			typeDecisionCandidature.setCandidature(e);
			typeDecisionCandidature.setDatCreTypeDecCand(LocalDateTime.now());
			typeDecisionCandidature.setTemValidTypeDecCand(false);
			typeDecisionCandidature.setUserCreTypeDecCand(user);
			typeDecisionCandidatureRepository.save(typeDecisionCandidature);
			e.setTemAcceptCand(null);
			e.setUserModCand(user);
			e.getTypeDecisionCandidatures().add(typeDecisionCandidature);
			e.setLastTypeDecision(typeDecisionCandidature);
			e.setDatModCand(LocalDateTime.now());
			candidatureRepository.save(e);
		}
			
		if (enMasse){
			unlockListCandidature(listeCandidature);
		}
		Notification.show(applicationContext.getMessage("candidature.editAvis.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		return true;
	}

	/** Valide les avis de candidatures
	 * @param listeCandidature
	 * @param enMasse 
	 * @return true si tout s'est bien passé
	 */
	public Boolean validAvis(List<Candidature> listeCandidature, Boolean enMasse) {
		if (checkLockListCandidature(listeCandidature)){
			return false;
		}
		for (Candidature candidature : listeCandidature){
			if (candidature.getLastTypeDecision() == null){
				Notification.show(applicationContext.getMessage("candidature.validAvis.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return false;
			}
		}
		String user = userController.getCurrentUserLogin();
		
		for (Candidature candidature : listeCandidature){
			Assert.notNull(candidature);
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}
			TypeDecisionCandidature typeDecision = candidature.getLastTypeDecision();
			typeDecision.setTemValidTypeDecCand(true);
			typeDecision.setDatValidTypeDecCand(LocalDateTime.now());
			typeDecision.setUserValidTypeDecCand(user);
			typeDecision = typeDecisionCandidatureRepository.save(typeDecision);
			candidature.setUserModCand(user);
			candidature.setDatModCand(LocalDateTime.now());
			candidature.setTypeDecision(typeDecision);
			candidature.setLastTypeDecision(typeDecision);
			candidatureRepository.save(candidature);
			String motif = "";
			if (typeDecision.getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisDefavorable()) && typeDecision.getMotivationAvis()!=null){
				motif = i18nController.getI18nTraduction(typeDecision.getMotivationAvis().getI18nLibMotiv());
			}
			
			String complementAppel = "";
			if (typeDecision.getTemAppelTypeDecCand()){
				complementAppel = applicationContext.getMessage("candidature.mail.complement.appel", null, UI.getCurrent().getLocale());
			}
			String rang = "";
			if (typeDecision.getListCompRangTypDecCand()!=null){
				rang = String.valueOf(typeDecision.getListCompRangTypDecCand());
			}
			AvisMailBean mailBean = new AvisMailBean(motif,typeDecision.getCommentTypeDecCand(),getComplementPreselect(typeDecision), complementAppel, rang);
			CandidatureMailBean mailBeanCand = mailController.getCandidatureMailBean(candidature);
			mailController.sendMail(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),typeDecision.getTypeDecision().getMail(), mailBean, mailBeanCand);
		}
		if (enMasse){
			unlockListCandidature(listeCandidature);
		}	
		
		
		Notification.show(applicationContext.getMessage("candidature.validAvis.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		return true;
	}
	
	/**
	 * @param typeDecision
	 * @return un eventuel complément de préselection
	 */
	public String getComplementPreselect(TypeDecisionCandidature typeDecision){
		String complementPreselect = "";
		if (typeDecision.getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisPreselect()) &&
				(
				(typeDecision.getPreselectDateTypeDecCand()!=null && !typeDecision.getPreselectDateTypeDecCand().equals(""))
				||
				(typeDecision.getPreselectHeureTypeDecCand()!=null && !typeDecision.getPreselectHeureTypeDecCand().equals(""))
				||
				(typeDecision.getPreselectLieuTypeDecCand()!=null && !typeDecision.getPreselectLieuTypeDecCand().equals(""))
				)){
			complementPreselect = applicationContext.getMessage("candidature.mail.complement.preselect", null, UI.getCurrent().getLocale())+" ";
			if (typeDecision.getPreselectDateTypeDecCand()!=null && !typeDecision.getPreselectDateTypeDecCand().equals("")){
				complementPreselect = complementPreselect + applicationContext.getMessage("candidature.mail.complement.preselect.date", new Object[]{typeDecision.getPreselectDateTypeDecCand()}, UI.getCurrent().getLocale())+" ";
			}
			if (typeDecision.getPreselectHeureTypeDecCand()!=null && !typeDecision.getPreselectHeureTypeDecCand().equals("")){
				complementPreselect = complementPreselect + applicationContext.getMessage("candidature.mail.complement.preselect.heure", new Object[]{typeDecision.getPreselectHeureTypeDecCand()}, UI.getCurrent().getLocale())+" ";
			}
			if (typeDecision.getPreselectLieuTypeDecCand()!=null && !typeDecision.getPreselectLieuTypeDecCand().equals("")){
				complementPreselect = complementPreselect + applicationContext.getMessage("candidature.mail.complement.preselect.lieu", new Object[]{typeDecision.getPreselectLieuTypeDecCand()}, UI.getCurrent().getLocale());
			}
			/*Suppression du dernier espace*/
			if (complementPreselect!=null && complementPreselect.length()!=0 && complementPreselect.substring(complementPreselect.length()-1, complementPreselect.length()).equals(" ")){
				complementPreselect = complementPreselect.substring(0,complementPreselect.length()-1);
			}
		}
		return complementPreselect;
	}
	
	/**Change le statut du dossier
	 * @param listeCandidature
	 * @param statut
	 * @param enMasse 
	 * @return true si tout s'est bien passé
	 */
	public Boolean editListCandidatureTypStatut(List<Candidature> listeCandidature, TypeStatut statut, LocalDate date, Boolean enMasse) {
		if (checkLockListCandidature(listeCandidature)){
			return false;
		}
		String user = userController.getCurrentUserLogin();
		
		for (Candidature e : listeCandidature){
			Assert.notNull(e);
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(e)) {
				continue;
			}
			if (statut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_REC)){
				e.setDatReceptDossierCand(date);
			}else if (statut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_COM)){
				e.setDatCompletDossierCand(date);
			}else if (statut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_INC)){
				e.setDatIncompletDossierCand(date);
			}
			e.setTypeStatut(statut);
			e.setDatModTypStatutCand(LocalDateTime.now());
			e.setUserModCand(user);
			candidatureRepository.save(e);
			CandidatureMailBean mailBean = mailController.getCandidatureMailBean(e);
			mailController.sendMailByCod(e.getCandidat().getCompteMinima().getMailPersoCptMin(),NomenclatureUtils.MAIL_STATUT_PREFIX+statut.getCodTypStatut(),null,mailBean);
		}
		
		Notification.show(applicationContext.getMessage("candidature.editStatutDossier.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		if (enMasse){
			unlockListCandidature(listeCandidature);
		}
		return true;
	}
	
	
	/** Voir l'historique des avis d'une candidature
	 * @param candidature
	 */
	public void showHistoAvis(Candidature candidature) {
		UI.getCurrent().addWindow(new CtrCandShowHistoWindow(candidature));
	}

	/** Ouvre la fenetre du choix de l'action sur les candidatures selectionnées
	 * @param candidature
	 * @param listener
	 */
	public void editActionCandidature(Candidature candidature,
			CandidatureListener listener) {
		List<Candidature> liste = new ArrayList<Candidature>();
		liste.add(candidature);
		CtrCandActionCandidatureWindow window = new CtrCandActionCandidatureWindow(liste, false);
		window.addChangeCandidatureWindowListener(new ChangeCandidatureWindowListener() {
			
			/**serialVersionUID **/
			private static final long serialVersionUID = 3285511657032521883L;

			@Override
			public void openCandidature(Candidature candidature) {
				if (candidature!=null){
					listener.openCandidat();
				}
			}
			
			@Override
			public void action(List<Candidature> listeCandidature) {
				if (listeCandidature!=null && listeCandidature.get(0)!=null){
					listener.infosCandidatureModified(listeCandidature.get(0));
				}
			}
		});
		UI.getCurrent().addWindow(window);
		
	}

	/** Exporte les candidatures
	 * @param liste
	 * @param allOptions
	 * @param optionChecked
	 * @return l'InputStream du fichier d'export
	 */
	public InputStream generateExport(final List<Candidature> liste, LinkedHashSet<ExportListCandidatureOption> allOptions , Set<ExportListCandidatureOption> optionChecked) {
		Map<String, Object> beans = new HashMap<String, Object>();
		liste.forEach(candidature->{
			candidature.setDatCreCandStr(candidature.getDatCreCand().format(formatterDate));
			candidature.getCandidat().setAdresseCandididatStr(generateAdresse(candidature.getCandidat().getAdresse()));
			if (candidature.getLastTypeDecision()!=null){
				if (candidature.getLastTypeDecision().getDatValidTypeDecCand()!=null){
					candidature.getLastTypeDecision().setDatValidTypeDecCandStr(candidature.getLastTypeDecision().getDatValidTypeDecCand().format(formatterDate));
				}				
				candidature.getLastTypeDecision().setPreselectStr(getComplementPreselect(candidature.getLastTypeDecision()));
			}
			if (candidature.getDatModTypStatutCand()!=null){
				candidature.setDatModTypStatutCandStr(candidature.getDatModTypStatutCand().format(formatterDate));
			}else{
				candidature.setDatModTypStatutCandStr("");
			}
			if (candidature.getDatReceptDossierCand()!=null){
				candidature.setDatReceptDossierCandStr(candidature.getDatReceptDossierCand().format(formatterDate));
			}else{
				candidature.setDatReceptDossierCandStr("");
			}
			
			if (candidature.getDatTransDossierCand()!=null){
				candidature.setDatTransDossierCandStr(candidature.getDatTransDossierCand().format(formatterDate));
			}else{
				candidature.setDatTransDossierCandStr("");
			}
			
			if (candidature.getDatCompletDossierCand()!=null){
				candidature.setDatCompletDossierCandStr(candidature.getDatCompletDossierCand().format(formatterDate));
			}else{
				candidature.setDatCompletDossierCandStr("");
			}
			
			if (candidature.getDatIncompletDossierCand()!=null){
				candidature.setDatIncompletDossierCandStr(candidature.getDatIncompletDossierCand().format(formatterDate));
			}else{
				candidature.setDatIncompletDossierCandStr("");
			}
			candidature.setDatModPjForm(getDatModPjForm(candidature));
			getLastEtab(candidature.getCandidat());
		});
		
		
		beans.put("candidatures", liste);
		allOptions.stream().forEach(exportOption->{
			addExportOption(exportOption, optionChecked, beans);
		});
		
		ByteArrayOutputStream bos = null;
		try {
			/* Récupération du template */
			InputStream fileIn = new BufferedInputStream(new ClassPathResource(
					"template/candidatures_template.xlsx").getInputStream());
			/* Génération du fichier excel */
			ExcelTransformer transformer = new ExcelTransformer();
			transformer.setSilent(true);
			Workbook workbook;
			workbook = transformer.transform(fileIn, beans);
			bos = new ByteArrayInOutStream();
			workbook.write(bos);
			fileIn.close();
			return new ByteArrayInputStream(bos.toByteArray());
		} catch (InvalidFormatException | IOException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	/** calcul la derniere modif de statut de PJ ou Formulaire
	 * @param candidature
	 * @param formatter
	 * @return la date de derniere modif
	 */
	private String getDatModPjForm(Candidature candidature) {
		LocalDateTime dateMod = null;
		Optional<FormulaireCand> formOpt = candidature.getFormulaireCands().stream().filter(e->e.getDatReponseFormulaireCand()!=null).sorted((e1, e2) -> (e2.getDatReponseFormulaireCand().compareTo(e1.getDatReponseFormulaireCand()))).findFirst();
		Optional<PjCand> pjOpt = candidature.getPjCands().stream().filter(e->e.getDatModStatutPjCand()!=null).sorted((e1, e2) -> (e2.getDatModStatutPjCand().compareTo(e1.getDatModStatutPjCand()))).findFirst();
		if (formOpt.isPresent()){
			dateMod = formOpt.get().getDatCreFormulaireCand();
		}
		if (pjOpt.isPresent()){
			PjCand pj = pjOpt.get();
			if (dateMod==null){
				dateMod = pj.getDatModStatutPjCand();
			}else{
				dateMod = (pj.getDatModStatutPjCand().isAfter(dateMod))?pj.getDatModStatutPjCand():dateMod;
			}
		}
		if (dateMod==null){
			return "";
		}else{
			return dateMod.format(formatterDate);
		}
	}
	
	/**  modifie le last etab d'un candidat
	 * @param candidat
	 */
	private void getLastEtab(Candidat candidat){
		String lastEtab = "";
		String lastFormation = "";
		Integer annee = 0;
		for (CandidatCursusInterne cursus: candidat.getCandidatCursusInternes()){
			if (cursus.getAnneeUnivCursusInterne()>annee){
				annee = cursus.getAnneeUnivCursusInterne();
				lastEtab = applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale());
				lastFormation = cursus.getLibCursusInterne();
			}
		}
		for (CandidatCursusPostBac cursus: candidat.getCandidatCursusPostBacs()){
			if (cursus.getAnneeUnivCursus()>annee && cursus.getSiScolEtablissement()!=null){
				annee = cursus.getAnneeUnivCursus();
				lastEtab = cursus.getSiScolEtablissement().getLibEtb();
				lastFormation = cursus.getLibCursus();
			}
		}
		candidat.setLastEtab(lastEtab);
		candidat.setLastFormation(lastFormation);
	}

	/**
	 * @param adresse
	 * @return adresse formatée
	 */
	private String generateAdresse(Adresse adresse) {
		String libAdr = "";
		if (adresse != null){
			if (adresse.getAdr1Adr()!=null){
				libAdr = libAdr + adresse.getAdr1Adr()+" ";
			}
			if (adresse.getAdr2Adr()!=null){
				libAdr = libAdr + adresse.getAdr2Adr()+" ";
			}
			if (adresse.getAdr3Adr()!=null){
				libAdr = libAdr + adresse.getAdr3Adr()+" ";
			}
			if (adresse.getCodBdiAdr()!=null && adresse.getSiScolCommune()!=null && adresse.getSiScolCommune().getLibCom()!=null){
				libAdr = libAdr + adresse.getCodBdiAdr()+" "+adresse.getSiScolCommune().getLibCom()+" ";
			}else{
				if (adresse.getCodBdiAdr()!=null){
					libAdr = libAdr + adresse.getCodBdiAdr()+" ";
				}
				if (adresse.getSiScolCommune()!=null && adresse.getSiScolCommune().getLibCom()!=null){
					libAdr = libAdr + adresse.getSiScolCommune().getLibCom()+" ";
				}
			}
			if (adresse.getLibComEtrAdr()!=null){
				libAdr = libAdr + adresse.getLibComEtrAdr()+" ";
			}
			if (adresse.getSiScolPays()!=null && !adresse.getSiScolPays().equals(tableRefController.getPaysFrance())){
				libAdr = libAdr + adresse.getSiScolPays().getLibPay();
			}
		}
		return libAdr;
	}

	/**Doit-on cacher ou afficher les colonnes --> true cacher, false afficher
	 * @param exportOption
	 * @param optionChecked
	 * @param beans
	 */
	private void addExportOption(ExportListCandidatureOption exportOption,	Set<ExportListCandidatureOption> optionChecked, Map<String, Object> beans) {
		if (optionChecked.contains(exportOption)){
			beans.put(exportOption.getId(),false);
		}else{
			beans.put(exportOption.getId(),true);
		}
	}


	/**
	 * Ouvre le dossier d'un candidat
	 * @param candidature 
	 */
	public void openCandidat(Candidature candidature) {
		CompteMinima cpt = candidature.getCandidat().getCompteMinima();
		userController.setNoDossierNomCandidat(cpt);
		((MainUI)MainUI.getCurrent()).buildMenuGestCand();		
	}
}
