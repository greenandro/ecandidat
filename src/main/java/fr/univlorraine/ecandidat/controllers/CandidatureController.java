package fr.univlorraine.ecandidat.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureCandidatViewListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.OdfListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierAvis;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierBac;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierCandidat;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierCandidature;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierCursusExterne;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierCursusInterne;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierCursusPro;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierDate;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierMotivationAvis;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierPj;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierStage;
import fr.univlorraine.ecandidat.utils.bean.mail.CandidatureMailBean;
import fr.univlorraine.ecandidat.utils.bean.presentation.FormulairePresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.views.CandidatCandidaturesView;
import fr.univlorraine.ecandidat.views.OffreFormationView;
import fr.univlorraine.ecandidat.views.windows.CandidatureWindow;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandOdfCandidatureWindow;

/**
 * Gestion des Candidatures
 * @author Kevin Hergalant
 *
 */
@Component
public class CandidatureController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private Environment environment;
	@Resource
	private transient UserController userController;
	@Resource
	private transient LockCandidatController lockCandidatController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient AdresseController adresseController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CandidatureCtrCandController ctrCandCandidatureController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient CandidatureGestionController decisionCandidatureController;	
	@Resource
	private transient MailController mailController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient MotivationAvisController motivationAvisController;
	@Resource
	private transient CandidatureRepository candidatureRepository;
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient DateTimeFormatter formatterDate;
	
	/**
	 * Edition d'une nouvelle candidature
	 */
	public void editNewCandidature() {
		((MainUI)MainUI.getCurrent()).navigateToView(OffreFormationView.NAME);
	}
	
	/**
	 * @param idCandidature
	 * @return la candidature chargée
	 */
	public Candidature loadCandidature(Integer idCandidature){
		return candidatureRepository.findOne(idCandidature);
	}
	
	
	/** Candidate à une formation
	 * @param idForm
	 * @param listener
	 */
	public void candidatToFormation(Integer idForm, OdfListener listener, Boolean isTest) {
		if (userController.isAnonymous()){
			return;
		}
		if (!userController.isCandidat() && userController.getNoDossierCandidat()==null){
			Notification.show(applicationContext.getMessage("odf.choose.candidat", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		/*Vérification du compte à minima*/
		CompteMinima cptMin =  candidatController.getCompteMinima();
		if (cptMin==null){
			Notification.show(applicationContext.getMessage("cptmin.load.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		Candidat candidat = cptMin.getCandidat();
		/*Vérification du candidat-->info perso*/
		if (candidat==null){
			Notification.show(applicationContext.getMessage("candidat.load.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		/*Vérification du candidat-->adresse*/
		if (candidat.getAdresse()==null){
			Notification.show(applicationContext.getMessage("candidat.load.adresse.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		/*Vérification du candidat-->bac*/
		if (candidat.getCandidatBacOuEqu()==null){
			Notification.show(applicationContext.getMessage("candidat.load.bac.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		Formation formation = formationRepository.findOne(idForm);
		if (formation == null || !formation.getTesForm()){
			Notification.show(applicationContext.getMessage("candidature.formation.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			if (listener!=null){
				listener.updateOdf();
			}
			return;
		}
		if (!isTest){
			if (candidat.getCandidatures().stream().filter(candidature->candidature.getDatAnnulCand()==null && candidature.getFormation().getIdForm().equals(idForm)).findAny().isPresent()){
				Notification.show(applicationContext.getMessage("candidature.formation.allready", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);			
				return;
			}
		}
		
		/*On défini les variables*/
		String user = userController.getCurrentUserLogin();
		TypeTraitement typTraitForm = formation.getTypeTraitement();
		if (typTraitForm.equals(tableRefController.getTypeTraitementAccesDirect())){
			typTraitForm = tableRefController.getTypeTraitementEnAttente();
		}
		
		if (userController.isGestionnaire()){
			candidatToFormationGestionnaire(candidat,formation, user, typTraitForm);
		}else{
			/*Verif que les dates sont bien dans l'interval*/
			if (!MethodUtils.isDateIncludeInInterval(LocalDate.now(), formation.getDatDebDepotForm(), formation.getDatFinDepotForm())){
				Notification.show(applicationContext.getMessage("candidature.date.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}
			/*Verif que le nb de candidatures du candidat sur ce centre ne depassent pas le nb parametre*/
			CentreCandidature ctrCand = formation.getCommission().getCentreCandidature();
			
			Integer nbMax;
			Long nbCand;
			String message;
			if (parametreController.getNbVoeuxMaxIsEtab()){
				nbMax = parametreController.getNbVoeuxMax();
				nbCand = candidatureRepository.getNbCandByEtab(candidat.getIdCandidat());
				message = applicationContext.getMessage("candidature.etab.error", null, UI.getCurrent().getLocale());
			}else{
				nbMax = ctrCand.getNbMaxVoeuxCtrCand();
				nbCand = candidatureRepository.getNbCandByCtrCand(ctrCand.getIdCtrCand(), candidat.getIdCandidat());
				message = applicationContext.getMessage("candidature.ctrCand.error", null, UI.getCurrent().getLocale());
			}
			
			if (nbCand>=nbMax){
				Notification.show(message, Type.WARNING_MESSAGE);
				return;
			}
			candidatToFormationCandidat(candidat,formation, user, typTraitForm, isTest);
		}		
	}
	
	/** La candiature est faite par un candidat
	 * @param candidat
	 * @param formation
	 * @param user
	 * @param typTraitForm
	 * @param isTest 
	 */
	private void candidatToFormationCandidat(Candidat candidat, Formation formation, String user, TypeTraitement typTraitForm, Boolean isTest){
		if (isTest){
			saveCandidature(new Candidature(user,candidat,
					formation,typTraitForm,tableRefController.getTypeStatutEnAttente(), false, false), false);
		}else{
			ConfirmWindow win = new ConfirmWindow(applicationContext.getMessage("candidature.confirm", new Object[]{formation.getLibForm()}, UI.getCurrent().getLocale()));
			win.addBtnOuiListener(e -> {			
				Candidature candidature = saveCandidature(new Candidature(user,candidat,
						formation,typTraitForm,tableRefController.getTypeStatutEnAttente(), false, false), false);
				if (candidature != null){
					((MainUI)MainUI.getCurrent()).navigateToView(CandidatCandidaturesView.NAME+"/"+candidature.getIdCand());
				}			
			});
			UI.getCurrent().addWindow(win);
		}		
	}
	
	/** La candidature est faite par un gestionnaire
	 * @param candidat 
	 * @param formation
	 * @param user
	 * @param typTraitForm
	 */
	private void candidatToFormationGestionnaire(Candidat candidat, Formation formation, String user, TypeTraitement typTraitForm){
		String msgWin = applicationContext.getMessage("candidature.gest.window.msg", new Object[]{candidat.getNomPatCandidat()+" "+candidat.getPrenomCandidat(), formation.getLibForm()}, UI.getCurrent().getLocale());
		CtrCandOdfCandidatureWindow window = new CtrCandOdfCandidatureWindow(msgWin);
		
		window.addOdfCandidatureListener(typeCandidature->{			
			if (typeCandidature.equals(ConstanteUtils.OPTION_CLASSIQUE)){
				Candidature candidature = saveCandidature(new Candidature(user,candidat,formation,typTraitForm,tableRefController.getTypeStatutEnAttente(), false, false), false);
				if (candidature == null){
					return;
				}
			}else if(typeCandidature.equals(ConstanteUtils.OPTION_PROP)){
				Candidature candidature = new Candidature(user,candidat, formation,typTraitForm,tableRefController.getTypeStatutComplet(), true, true);
				candidature = saveCandidature(candidature,true);
				if (candidature != null){
					ctrCandCandidatureController.saveTypeDecisionCandidature(candidature, formation.getTypeDecisionFav(), false, user);
				}else{
					return;
				}				
			}else{
				return;
			}			
			((MainUI)MainUI.getCurrent()).navigateToView(CandidatCandidaturesView.NAME);
		});
		UI.getCurrent().addWindow(window);
	} 
	
	
	/**Enregistre une candidature
	 * @param candidature
	 * @param isProposition
	 * @return la candidature
	 */
	public Candidature saveCandidature(Candidature candidature, Boolean isProposition){
		/*On vérifie */
		List<Candidature> candidatureCheckAllreadyExist = candidatureRepository.findByFormationIdFormAndCandidatIdCandidatAndDatAnnulCandIsNull(candidature.getFormation().getIdForm(), candidature.getCandidat().getIdCandidat());
		if (candidatureCheckAllreadyExist.size()>0){
			Notification.show(applicationContext.getMessage("unexpected.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return null;
		}		
		
		candidature = candidatureRepository.save(candidature);
		CandidatureMailBean mailBean = mailController.getCandidatureMailBean(candidature);

		if (isProposition){
			/*envoi du mail à la commission*/
			mailController.sendMailByCod(candidature.getFormation().getCommission().getMailComm(),NomenclatureUtils.MAIL_CANDIDATURE_COMMISSION_PROP,null,mailBean);
			Notification.show(applicationContext.getMessage("candidature.proposition.success", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		}else{
			/*envoi du mail au candidat*/
			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),NomenclatureUtils.MAIL_CANDIDATURE,null,mailBean);
			Notification.show(applicationContext.getMessage("candidature.success", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		}		
		return candidature;
	}

	/**Supprime une candidature
	 * @param candidat
	 * @param candidature
	 * @param listener
	 */
	public void deleteCandidature(Candidat candidat, Candidature candidature,
			CandidatureListener listener) {
		ConfirmWindow win = new ConfirmWindow(applicationContext.getMessage("candidature.delete.confirm", new Object[]{candidature.getFormation().getLibForm()}, UI.getCurrent().getLocale()));
		win.addBtnOuiListener(e -> {			
			candidatureRepository.delete(candidature);
			listener.candidatureDeleted(candidature);
		});
		UI.getCurrent().addWindow(win);
	}
	
	/** Ouvre la fenetre pour le candidat
	 * @param candidature
	 * @param listener
	 */
	public void openCandidatureCandidat(Candidature candidature, CandidatureCandidatViewListener listener){
		if (candidature==null){
			return;
		}
		Candidature candidatureLoad = candidatureRepository.findOne(candidature.getIdCand());
		if (candidatureLoad==null || candidatureLoad.getDatAnnulCand()!=null || candidatureLoad.getCandidat().getCompteMinima().getCampagne().getDatArchivCamp()!=null){
			Notification.show(applicationContext.getMessage("candidature.open.error", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
			listener.candidatureCanceled(candidature);
			return;
		}
		candidatureLoad.setLastTypeDecision(getLastTypeDecisionCandidature(candidatureLoad));
		
		/*Si les valeurs ont changé entre temps, on update la vue candidat*/
		if ((!isTypeDecisionEquals(candidatureLoad.getLastTypeDecision(),candidature.getLastTypeDecision()))
				|| 
				(!candidatureLoad.getTypeStatut().equals(candidature.getTypeStatut()))){
			Notification.show(applicationContext.getMessage("candidature.open.modify", null, UI.getCurrent().getLocale()), Notification.Type.TRAY_NOTIFICATION);			
			listener.statutDossierModified(candidatureLoad);
		}
			
			
		/*Si le user est gestionnaire on va vérifier qu'il a le droit d'acceder a ce centre de candidature, sinon, lecture seule*/
		if (userController.isCandidat() || (userController.isGestionnaire() && userController.isAutorizedToLookCandidature(candidatureLoad))){
			openCandidature(candidatureLoad,false,false,listener);
		}else{
			Notification.show(applicationContext.getMessage("candidature.open.noright", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
			UI.getCurrent().addWindow(new CandidatureWindow(candidature,true, false, false));
		}
		
	}
	
	/**
	 * @param t1
	 * @param t2
	 * @return true si les types de decisions sont identiques
	 */
	private Boolean isTypeDecisionEquals(TypeDecisionCandidature t1, TypeDecisionCandidature t2){
		if (t1==null && t2==null){
			return true;
		}else if((t1!=null && t2==null) || (t2!=null && t1==null)){
			return false;
		}
		return t1.getTypeDecision().equals(t2.getTypeDecision());
	}
	
	/**Ouvre une candidature
	 * @param candidature
	 * @param canceled
	 * @param archived
	 * @param listener
	 */
	public void openCandidature(Candidature candidature, Boolean canceled, Boolean archived, CandidatureCandidatViewListener listener){
		Assert.notNull(candidature);

		Boolean locked = false;
		
		/*On ne lock pas si le user est candidat et qu'on est en demat ou que la date est dépassée*/
		/*Boolean isDematerialisation = isCandidatureDematerialise(candidature);
		if (userController.isCandidat() && (!isDematerialisation || (isDematerialisation && candidature.getFormation().getDatRetourForm().isAfter(LocalDate.now())))){
			locked = true;
		}else{
			if (!lockController.getLockOrNotifyCandidature(candidature)) {
				locked = true;
			}
		}*/
		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			locked = true;
		}
		
		CandidatureWindow window = new CandidatureWindow(candidature,locked, canceled, archived);
		if (listener!=null){
			window.addCandidatureCandidatListener(listener);
		}
		window.addCloseListener(e->lockCandidatController.releaseLockCandidature(candidature));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * @param candidature
	 * @return une liste de données perso à afficher
	 */
	public List<SimpleTablePresentation> getInformationsCandidature(Candidature candidature, Boolean isCandidatOfCandidature) {
		List<SimpleTablePresentation> liste = new ArrayList<SimpleTablePresentation>();
		Formation formation = candidature.getFormation();
		TypeDecisionCandidature typeDecision = getLastTypeDecisionCandidature(candidature);

		liste.add(new SimpleTablePresentation("candidature."+Candidature_.formation.getName()+"."+Formation_.libForm.getName(),applicationContext.getMessage("candidature."+Candidature_.formation.getName()+"."+Formation_.libForm.getName(), null, UI.getCurrent().getLocale()), formation.getLibForm()));		
		SimpleTablePresentation stpStatutDossier = new SimpleTablePresentation("candidature."+ConstanteUtils.CANDIDATURE_LIB_STATUT,applicationContext.getMessage("candidature."+ConstanteUtils.CANDIDATURE_LIB_STATUT, null, UI.getCurrent().getLocale()), i18nController.getI18nTraduction(candidature.getTypeStatut().getI18nLibTypStatut())); 
		stpStatutDossier.setShortValue(candidature.getTypeStatut().getCodTypStatut());
		liste.add(stpStatutDossier);
				
		/*Le candidat est gestionnaire-->On affiche le type de traitement*/
		if (!isCandidatOfCandidature){
			String libTypTraitement = i18nController.getI18nTraduction(candidature.getTypeTraitement().getI18nLibTypTrait());
			if (candidature.getTemValidTypTraitCand()){
				libTypTraitement = libTypTraitement +" ("+applicationContext.getMessage("valide", null, UI.getCurrent().getLocale())+")"; 
			}else{
				libTypTraitement = libTypTraitement +" ("+applicationContext.getMessage("non.valide", null, UI.getCurrent().getLocale())+")"; 
			}
			liste.add((new SimpleTablePresentation("candidature."+ConstanteUtils.CANDIDATURE_LIB_TYPE_TRAITEMENT,applicationContext.getMessage("candidature."+ConstanteUtils.CANDIDATURE_LIB_TYPE_TRAITEMENT, null, UI.getCurrent().getLocale()), libTypTraitement)));
		}		
		
		String libTypDecision = getLibLastTypeDecisionCandidature(typeDecision, isCandidatOfCandidature);
		String commentaire = null;
		String codeTypeDecision = NomenclatureUtils.TYP_AVIS_ATTENTE;
		
		/*La decision n'est pas null et le candidat est candidiat avec un avis validé*/
		if (typeDecision!=null && (!isCandidatOfCandidature || (isCandidatOfCandidature && typeDecision.getTemValidTypeDecCand()))){
			if (typeDecision.getTemValidTypeDecCand()){
				if (!isCandidatOfCandidature){
					libTypDecision = libTypDecision +" ("+applicationContext.getMessage("valide", null, UI.getCurrent().getLocale())+")";
				}				
				if (candidature.getTemAcceptCand()!=null && candidature.getTemAcceptCand()){
					libTypDecision = libTypDecision +" : "+applicationContext.getMessage("candidature.confirm.label", null, UI.getCurrent().getLocale());
				}else if (candidature.getTemAcceptCand()!=null && !candidature.getTemAcceptCand()){
					libTypDecision = libTypDecision +" : "+applicationContext.getMessage("candidature.desist.label", null, UI.getCurrent().getLocale());
				}
			}else{
				libTypDecision = libTypDecision +" ("+applicationContext.getMessage("non.valide", null, UI.getCurrent().getLocale())+")";
			}
			if (typeDecision.getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisPreselect())){
				libTypDecision = libTypDecision+"<br>"+ctrCandCandidatureController.getComplementPreselect(typeDecision);
				
			}
			codeTypeDecision = typeDecision.getTypeDecision().getTypeAvis().getCodTypAvis();
			commentaire = typeDecision.getCommentTypeDecCand();;
		}
		SimpleTablePresentation stpDecision = new SimpleTablePresentation("candidature."+ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION,applicationContext.getMessage("candidature."+ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION, null, UI.getCurrent().getLocale()), libTypDecision); 
		stpDecision.setShortValue(codeTypeDecision);
		liste.add(stpDecision);

		/*On ajoute le commentaire lié à l'avis à la suite*/
		if (commentaire!=null && !commentaire.equals("")){
			liste.add(new SimpleTablePresentation("candidature."+ConstanteUtils.CANDIDATURE_COMMENTAIRE, applicationContext.getMessage("candidature."+ConstanteUtils.CANDIDATURE_COMMENTAIRE, null, UI.getCurrent().getLocale()), commentaire));
		}
		
		return liste;
	}
	
	/**
	 * @param candidature
	 * @param isCandidatOfCandidature 
	 * @return les infos de dates de la candidature
	 */
	public List<SimpleTablePresentation> getInformationsDateCandidature(
			Candidature candidature, Boolean isCandidatOfCandidature) {
		List<SimpleTablePresentation> liste = new ArrayList<SimpleTablePresentation>();
		Formation formation = candidature.getFormation();
		if (formation.getDatAnalyseForm()!=null){
			liste.add(new SimpleTablePresentation("candidature."+Candidature_.formation.getName()+"."+Formation_.datAnalyseForm.getName(),applicationContext.getMessage("candidature."+Candidature_.formation.getName()+"."+Formation_.datAnalyseForm.getName(), null, UI.getCurrent().getLocale()), ((formation.getDatAnalyseForm()!=null)?formatterDate.format(formation.getDatAnalyseForm()):"")));
		}
		if (formation.getDatRetourForm()!=null){
			liste.add(new SimpleTablePresentation("candidature."+Candidature_.formation.getName()+"."+Formation_.datRetourForm.getName(),applicationContext.getMessage("candidature."+Candidature_.formation.getName()+"."+Formation_.datRetourForm.getName(), null, UI.getCurrent().getLocale()), ((formation.getDatRetourForm()!=null)?formatterDate.format(formation.getDatRetourForm()):"")));
		}
		if (formation.getDatConfirmForm()!=null){
			liste.add(new SimpleTablePresentation("candidature."+Candidature_.formation.getName()+"."+Formation_.datConfirmForm.getName(),applicationContext.getMessage("candidature."+Candidature_.formation.getName()+"."+Formation_.datConfirmForm.getName(), null, UI.getCurrent().getLocale()), ((formation.getDatConfirmForm()!=null)?formatterDate.format(formation.getDatConfirmForm()):"")));
		}
		if (formation.getDatJuryForm()!=null){
			liste.add(new SimpleTablePresentation("candidature."+Candidature_.formation.getName()+"."+Formation_.datJuryForm.getName(),applicationContext.getMessage("candidature."+Candidature_.formation.getName()+"."+Formation_.datJuryForm.getName(), null, UI.getCurrent().getLocale()), ((formation.getDatJuryForm()!=null)?formatterDate.format(formation.getDatJuryForm()):"")));
		}
		if (formation.getDatPubliForm()!=null){
			liste.add(new SimpleTablePresentation("candidature."+Candidature_.formation.getName()+"."+Formation_.datPubliForm.getName(),applicationContext.getMessage("candidature."+Candidature_.formation.getName()+"."+Formation_.datPubliForm.getName(), null, UI.getCurrent().getLocale()), ((formation.getDatPubliForm()!=null)?formatterDate.format(formation.getDatPubliForm()):"")));
		}		
		/*Le candidat est gestionnaire-->On affiche la date de reception*/
		if (!isCandidatOfCandidature){
			if (candidature.getDatReceptDossierCand()!=null){
				liste.add(new SimpleTablePresentation("candidature."+Candidature_.datReceptDossierCand.getName(),applicationContext.getMessage("candidature."+Candidature_.datReceptDossierCand.getName(), null, UI.getCurrent().getLocale()), ((candidature.getDatReceptDossierCand()!=null)?formatterDate.format(candidature.getDatReceptDossierCand()):"")));
			}
			if (candidature.getDatReceptDossierCand()!=null){
				liste.add(new SimpleTablePresentation("candidature."+Candidature_.datTransDossierCand.getName(),applicationContext.getMessage("candidature."+Candidature_.datTransDossierCand.getName(), null, UI.getCurrent().getLocale()), ((candidature.getDatReceptDossierCand()!=null)?formatterDate.format(candidature.getDatReceptDossierCand()):"")));
			}
			
		}
		return liste;
	}
	
	/**
	 * @param typeDecision la decision
	 * @param isCandidatOfCandidature 
	 * @return le libellé de la derniere decision
	 */
	public String getLibLastTypeDecisionCandidature(TypeDecisionCandidature typeDecision, Boolean isCandidatOfCandidature){
		String decision = applicationContext.getMessage("candidature.no.decision", null, UI.getCurrent().getLocale());
		
		/*La decision n'est pas null et le candidat est gestionnaire ou le candidat est candidiat avec un avis validé*/
		if (typeDecision!=null && (!isCandidatOfCandidature || (isCandidatOfCandidature && typeDecision.getTemValidTypeDecCand()))){
			decision = i18nController.getI18nTraduction(typeDecision.getTypeDecision().getI18nLibTypDec());
			MotivationAvis motiv = typeDecision.getMotivationAvis();
			if (typeDecision.getListCompRangTypDecCand()!=null){
				decision = decision + " - "+applicationContext.getMessage("candidature.rang", null, UI.getCurrent().getLocale())+" " +typeDecision.getListCompRangTypDecCand();
			}
			if (motiv!=null){
				decision = decision + " - " +i18nController.getI18nTraduction(motiv.getI18nLibMotiv());
			}
		}
		return decision;
	}
	
	/**
	 * @param candidature
	 * @return la derniere decision prise
	 */
	public TypeDecisionCandidature getLastTypeDecisionCandidature(Candidature candidature){
		Optional<TypeDecisionCandidature> decOpt = candidature.getTypeDecisionCandidatures().stream()
				.sorted((e1, e2) -> (e2.getDatCreTypeDecCand().compareTo(e1.getDatCreTypeDecCand())))
				//.filter(e->e.getTemValidTypeDecCand())
				.findFirst();
		if (decOpt.isPresent()){
			return decOpt.get();
		}
		return null;
	}
	
	/**Modifie la confirmation ou le desistement
	 * @param candidature
	 * @param confirm
	 * @param listener
	 */
	public void setConfirmationCandidature(Candidature candidature, Boolean confirm, CandidatureListener listener) {
		Assert.notNull(candidature);
		
		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		
		String txt;
		if (confirm){
			txt = applicationContext.getMessage("candidature.confirm.window", null, UI.getCurrent().getLocale());
		}else{
			txt = applicationContext.getMessage("candidature.desist.window", null, UI.getCurrent().getLocale());
		}
		ConfirmWindow confirmWindow = new ConfirmWindow(txt);
		confirmWindow.addBtnOuiListener(e -> {
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}
			candidature.setTemAcceptCand(confirm);
			candidature.setDatAcceptCand(LocalDateTime.now());
			candidature.setUserAcceptCand(userController.getCurrentUserLogin());
			if (confirm){
				decisionCandidatureController.generateOpi(candidature);
			}
			listener.infosCandidatureModified(candidatureRepository.save(candidature));
			String typeMail = (confirm)?NomenclatureUtils.MAIL_CANDIDATURE_CONFIRM:NomenclatureUtils.MAIL_CANDIDATURE_DESIST;
			String msgNotif = (confirm)?applicationContext.getMessage("candidature.confirm.success", null, UI.getCurrent().getLocale()):applicationContext.getMessage("candidature.desist.success", null, UI.getCurrent().getLocale());
			CandidatureMailBean mailBean = mailController.getCandidatureMailBean(candidature);
			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),typeMail,null,mailBean);
			Notification.show(msgNotif, Type.WARNING_MESSAGE);
			if(!confirm){
				decisionCandidatureController.candidatFirstCandidatureListComp(candidature.getFormation());
			}
			
		});
		UI.getCurrent().addWindow(confirmWindow);
		
	}
	
	/**
	 * @param candidature
	 * @return true si l'utilisateur est un candidat valide
	 */
	public Boolean isCandidatOfCandidature(Candidature candidature){
		if (userController.isCandidatValid() && candidatController.getCompteMinima()!=null 
				&& candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin().equals(candidatController.getCompteMinima().getNumDossierOpiCptMin())){
			return true;
		}
		return false;
	}
	
	/**
	 * @param candidature
	 * @return true si l'utilisateur a le droit de modifier la candidature
	 */
	public Boolean isGestionnaireOfCandidature(Candidature candidature){
		if (userController.isScolCentrale()){
			return true;
		}else if (userController.isGestionnaire()){
			SecurityCtrCandFonc securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE);
			if (securityCtrCandFonc!=null && securityCtrCandFonc.getIdCtrCand()!=null && securityCtrCandFonc.getReadOnly()!=null && securityCtrCandFonc.getReadOnly()==false){			
				if (securityCtrCandFonc.getIsGestAllCommission() || isIdCommInListIdComm(candidature.getFormation().getCommission().getIdComm(),securityCtrCandFonc.getListeIdCommission())){
					return true;
				}
			}
		}
		return false;
	}
	
	/** Annule une candidature
	 * @param candidature
	 * @param listener
	 * @param candidatureCandidatListener 
	 */
	public void cancelCandidature(Candidature candidature,
			CandidatureListener listener, CandidatureCandidatViewListener candidatureCandidatListener) {
		Assert.notNull(candidature);
		
		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("candidature.cancel.window", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}			
			
			for (PjCand pjCand : candidature.getPjCands()){
				try {
					candidaturePieceController.removeFileToPj(pjCand);
				} catch (Exception e1) {
					Notification.show(applicationContext.getMessage("candidature.cancel.error.pj", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}
			}
			
			candidature.setPjCands(new ArrayList<PjCand>());
			candidature.setDatAnnulCand(LocalDateTime.now());
			candidature.setUserAnnulCand(userController.getCurrentUserLogin());					
			listener.candidatureCanceled(candidatureRepository.save(candidature));
						
			if (candidatureCandidatListener!=null){
				candidatureCandidatListener.candidatureCanceled(candidature);
			}
			CandidatureMailBean mailBean = mailController.getCandidatureMailBean(candidature);
			/*envoi du mail au candidat*/
			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),NomenclatureUtils.MAIL_CANDIDATURE_ANNULATION,null,mailBean);
			/*envoi du mail à la commission*/
			mailController.sendMailByCod(candidature.getFormation().getCommission().getMailComm(),NomenclatureUtils.MAIL_CANDIDATURE_COMMISSION_ANNUL,null,mailBean);
			
			Notification.show(applicationContext.getMessage("candidature.cancel.success", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	/** Annule l'annulation
	 * @param candidature
	 * @param listener
	 */
	public void annulCancelCandidature(Candidature candidature,	CandidatureListener listener) {
		Assert.notNull(candidature);
		
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("candidature.annul.cancel.window", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}
			candidature.setDatAnnulCand(null);
			candidature.setUserAnnulCand(null);
			listener.candidatureAnnulCanceled(candidatureRepository.save(candidature));
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	/** Génère le nom du fichier téléchargé
	 * @param candidat
	 * @return le nom du fichier telecharge
	 */
	public String getNomFichierDossier(Candidat candidat){
		return candidat.getCompteMinima().getNumDossierOpiCptMin()+"_"+candidat.getNomPatCandidat()+"_"+candidat.getPrenomCandidat()+".pdf";
	}
	
	/** Genere le dossier
	 * @param candidature
	 * @param listePresentation
	 * @param listeDatePresentation
	 * @param adresse
	 * @param listePj
	 * @param listeForm
	 * @return le stream du dossier
	 * @throws IOException
	 * @throws XDocReportException
	 */
	/*public ByteArrayInputStream generateDossier(Candidature candidature, List<SimpleTablePresentation> listePresentation, List<SimpleTablePresentation> listeDatePresentation,
			String adresse, List<PjPresentation> listePj, List<FormulairePresentation> listeForm) throws IOException, XDocReportException{

			Candidat candidat = candidature.getCandidat();
			String nomPrenom = candidat.getNomPatCandidat()+"\n"+candidat.getPrenomCandidat();
			
			ExportDossier dossier = new ExportDossier(candidat.getCompteMinima().getNumDossierOpiCptMin(), 
					nomPrenom,
					MethodUtils.getLibByPresentationCode(listePresentation,"candidature."+Candidature_.formation.getName()+"."+Formation_.libForm.getName()),					 
					MethodUtils.getLibByPresentationCode(listePresentation,"candidature."+ConstanteUtils.CANDIDATURE_LIB_STATUT), 
					MethodUtils.getLibByPresentationCode(listePresentation,"candidature."+ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION), 
					MethodUtils.getLibByPresentationCode(listePresentation,"candidature."+ConstanteUtils.CANDIDATURE_COMMENTAIRE), 
					adresse.replaceAll("<br>", "\n"));
			
			ExportDossierDate dates = new ExportDossierDate(
					MethodUtils.getLibByPresentationCode(listeDatePresentation,"candidature."+Candidature_.formation.getName()+"."+Formation_.datRetourForm.getName()),
					MethodUtils.getLibByPresentationCode(listeDatePresentation,"candidature."+Candidature_.formation.getName()+"."+Formation_.datConfirmForm.getName()),
					MethodUtils.getLibByPresentationCode(listeDatePresentation,"candidature."+Candidature_.formation.getName()+"."+Formation_.datJuryForm.getName()),
					MethodUtils.getLibByPresentationCode(listeDatePresentation,"candidature."+Candidature_.formation.getName()+"."+Formation_.datPubliForm.getName())
					);		
			
			List<ExportPj> listeExportPj = new ArrayList<ExportPj>();
			listePj.forEach(e->listeExportPj.add(new ExportPj(e.getLibPj(),e.getLibStatut(),e.getCommentaire())));
						
			List<ExportFormulaire> listeExportFormulaire = new ArrayList<ExportFormulaire>();
			listeForm.forEach(e->listeExportFormulaire.add(new ExportFormulaire(e.getLibFormulaire(),e.getUrlFormulaire(),e.getLibStatut(),e.getCommentaire())));			
			
			// 1) Load Docx file by filling Velocity template engine and cache
			// it to the registry
			String resourcePath = "/template/";
			String fileNameDefault = "DossierTemplate";
			String extension = ".docx";
			InputStream in = null;			
			
			if (userController.isCandidat() && !candidat.getLangue().equals(tableRefController.getLangueDefault())){
				in = getClass().getResourceAsStream(resourcePath+fileNameDefault+"_"+candidat.getLangue().getCodLangue()+extension);
			}
			
			if (in==null){
				in = getClass().getResourceAsStream(resourcePath+fileNameDefault+extension);
				if (in==null){
					return null;
				}
			}
			
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(
					in, TemplateEngineKind.Velocity);

			// 2) Create fields metadata to manage lazy loop (#foreach velocity) for table row.
	        FieldsMetadata metadata = report.createFieldsMetadata();
			metadata.load( "pieces", ExportPj.class, true );
			metadata.load( "form", ExportFormulaire.class, true );
			
			// 3) Create context Java model
			IContext context = report.createContext();
			// Register project
			context.put("candidature", dossier);
			context.put("date", dates);
			context.put("pieces", listeExportPj);
			context.put("form", listeExportFormulaire);
			context.put("displayPj", listeExportPj.size()>0);
			context.put("displayForm", listeExportFormulaire.size()>0);
			
			// 4) Generate report by merging Java model with the Docx
			ByteArrayInOutStream out = new ByteArrayInOutStream();
			Options options = Options.getTo(ConverterTypeTo.PDF).via(
					ConverterTypeVia.XWPF);
			
			report.convert(context, options, out);
			in.close();
			return out.getInputStream();
		
	}*/
	
	/**
	 * @param candidature
	 * @return true si la formation est demat
	 */
	public Boolean isCandidatureDematerialise(Candidature candidature){
		return candidature.getFormation().getCommission().getCentreCandidature().getTemDematCtrCand() && parametreController.getIsUtiliseDemat();
	}
	
	/**
	 * @param candidature
	 * @param listePresentation
	 * @param listeDatePresentation
	 * @param adresse
	 * @param listePj
	 * @param listeForm
	 * @return l'InputStream d'export
	 * @throws IOException
	 * @throws XDocReportException
	 */
	public ByteArrayInputStream generateDossier(Candidature candidature, List<SimpleTablePresentation> listePresentation, List<SimpleTablePresentation> listeDatePresentation,
			String adresse, List<PjPresentation> listePj, List<FormulairePresentation> listeForm) throws IOException, XDocReportException{

			/*Chargement des données utiles*/
			Candidat candidat = candidature.getCandidat();
			CompteMinima cptMin = candidat.getCompteMinima();
			Formation formation = candidature.getFormation();
			Commission commission = formation.getCommission();
			
			/*Utilisation de la demat*/
			Boolean isDematerialisation = isCandidatureDematerialise(candidature);
			
			/*On place les données dans des bean speciales export*/
			ExportDossierCandidature exportCandidature = new ExportDossierCandidature(cptMin.getCampagne().getLibCamp(), commission.getLibComm(), adresseController.getLibelleAdresse(commission.getAdresse(),"\n"),formation, MethodUtils.formatToExport(commission.getCommentRetourComm()));
			
			ExportDossierCandidat exportCandidat = new ExportDossierCandidat(cptMin,candidat,formatterDate.format(candidat.getDatNaissCandidat()),adresseController.getLibelleAdresse(candidat.getAdresse(),"\n"));
			
			ExportDossierBac exportDossierBac = new ExportDossierBac(candidat);
			
			List<ExportDossierCursusInterne> listeCursusInterne = new ArrayList<ExportDossierCursusInterne>();
			candidat.getCandidatCursusInternes().forEach(e->listeCursusInterne.add(new ExportDossierCursusInterne(e)));
			listeCursusInterne.sort((p1, p2) -> p1.getAnnee().compareTo(p2.getAnnee()));

			List<ExportDossierCursusExterne> listeCursusExterne = new ArrayList<ExportDossierCursusExterne>();
			candidat.getCandidatCursusPostBacs().forEach(e->listeCursusExterne.add(new ExportDossierCursusExterne(e,tableRefController.getLibelleObtenuCursusByCode(e.getObtenuCursus()))));
			listeCursusExterne.sort((p1, p2) -> p1.getAnnee().compareTo(p2.getAnnee()));
			
			List<ExportDossierStage> listeStage = new ArrayList<ExportDossierStage>();
			candidat.getCandidatStage().forEach(e->listeStage.add(new ExportDossierStage(e)));
			listeStage.sort((p1, p2) -> p1.getAnnee().compareTo(p2.getAnnee()));
			
			List<ExportDossierCursusPro> listeCursusPro = new ArrayList<ExportDossierCursusPro>();
			candidat.getCandidatCursusPros().forEach(e->listeCursusPro.add(new ExportDossierCursusPro(e)));
			listeCursusPro.sort((p1, p2) -> p1.getAnnee().compareTo(p2.getAnnee()));
			
			List<ExportDossierMotivationAvis> listeMotivationAvis = new ArrayList<ExportDossierMotivationAvis>();
			List<ExportDossierAvis> listeAvis = new ArrayList<ExportDossierAvis>();
			List<ExportDossierPj> listeExportPj = new ArrayList<ExportDossierPj>();
			
			if (!isDematerialisation){
				listePj.forEach(e->listeExportPj.add(new ExportDossierPj(e.getLibPj(),e.getLibStatut(),e.getCommentaire())));
				motivationAvisController.getMotivationAvisEnService().forEach(e->listeMotivationAvis.add(new ExportDossierMotivationAvis(i18nController.getI18nTraduction(e.getI18nLibMotiv()))));
				typeDecisionController.getTypeDecisionsEnService().forEach(e->listeAvis.add(new ExportDossierAvis(i18nController.getI18nTraduction(e.getI18nLibTypDec()),e.getTypeAvis().getCodTypAvis())));
				listeAvis.sort((p1, p2) -> p1.getOrder().compareTo(p2.getOrder()));
			}else{
				listePj.forEach(e->{
					if (e.getFilePj()!=null){
						ExportDossierPj exportDossierPj = new ExportDossierPj(e.getLibPj(),e.getLibStatut(),e.getCommentaire());						
						if (e.getFilePj().getFileFichier()!=null){
							exportDossierPj.setLibFichier(e.getFilePj().getNomFichier());
						}
						listeExportPj.add(exportDossierPj);
					}
				});
			}
						
			ExportDossierDate listeDates = new ExportDossierDate(
					MethodUtils.getLibByPresentationCode(listeDatePresentation,"candidature."+Candidature_.formation.getName()+"."+Formation_.datRetourForm.getName()),
					MethodUtils.getLibByPresentationCode(listeDatePresentation,"candidature."+Candidature_.formation.getName()+"."+Formation_.datConfirmForm.getName()),
					MethodUtils.getLibByPresentationCode(listeDatePresentation,"candidature."+Candidature_.formation.getName()+"."+Formation_.datJuryForm.getName()),
					MethodUtils.getLibByPresentationCode(listeDatePresentation,"candidature."+Candidature_.formation.getName()+"."+Formation_.datPubliForm.getName())
					);
			
			// 1) Load Docx file by filling Velocity template engine and cache
			// it to the registry
			String resourcePath = "/template/";
			String fileNameDefault = "dossier_export_template";
			String extension = ".docx";
			InputStream in = null;			
			
			String codeLangue = i18nController.getLangueCandidat();
			if (codeLangue!=null && !codeLangue.equals(tableRefController.getLangueDefault().getCodLangue())){
				in = getClass().getResourceAsStream(resourcePath+fileNameDefault+"_"+candidat.getLangue().getCodLangue()+extension);
			}
			
			/*if (candidat.getLangue()!=null && !candidat.getLangue().equals(tableRefController.getLangueDefault())){
				in = getClass().getResourceAsStream(resourcePath+fileNameDefault+"_"+candidat.getLangue().getCodLangue()+extension);
			}*/
			
			if (in==null){
				in = getClass().getResourceAsStream(resourcePath+fileNameDefault+extension);
				if (in==null){
					return null;
				}
			}
			
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(
					in, TemplateEngineKind.Velocity);

			// 2) Create fields metadata to manage lazy loop (#foreach velocity) for table row.
	        /*FieldsMetadata metadata = report.createFieldsMetadata();
	        metadata.load( "cursusInterne", ExportDossierCursusInterne.class, true );*/
			
			// 3) Create context Java model
			IContext context = report.createContext();
			// Register project
			context.put("adresseEcandidat", loadBalancingController.getApplicationPathForCandidat());
			context.put("candidature", exportCandidature);
			context.put("candidat", exportCandidat);
			context.put("bac", exportDossierBac);
			context.put("cursusInternes", listeCursusInterne);
			context.put("affichageCursusInterne", listeCursusInterne.size()>0);
			context.put("cursusExternes", listeCursusExterne);
			context.put("affichageCursusExterne", listeCursusExterne.size()>0);
			context.put("stages", listeStage);
			context.put("affichageStage", listeStage.size()>0);
			context.put("cursusPros", listeCursusPro);
			context.put("affichageCursusPro", listeCursusPro.size()>0);
			context.put("listeAvis", listeAvis);
			context.put("listeMotivationAvis", listeMotivationAvis);
			context.put("dates", listeDates);
			context.put("listePiecesJustifs", listeExportPj);
			context.put("non-dematerialisation", !isDematerialisation);
			context.put("affichagePjDemat", (listeExportPj.size()>0 && isDematerialisation));
			
			// 4) Generate report by merging Java model with the Docx
			ByteArrayInOutStream out = new ByteArrayInOutStream();
			Options options = Options.getTo(ConverterTypeTo.PDF).via(
					ConverterTypeVia.XWPF);
			
			/*PdfOptions pdfOptions = PdfOptions.create();
		    pdfOptions.fontEncoding("iso-8859-15");
		    options.subOptions(pdfOptions);*/
			
			report.convert(context, options, out);
			in.close();
			return out.getInputStream();
		
	}

	/** telecharge le dossier
	 * @param candidature
	 * @param listePresentation
	 * @param listeDatePresentation
	 * @param adresse
	 * @param listePj
	 * @param listeForm
	 * @return l'InputStream du dossier
	 */
	public InputStream downloadDossier(Candidature candidature, List<SimpleTablePresentation> listePresentation, List<SimpleTablePresentation> listeDatePresentation, String adresse, 
			List<PjPresentation> listePj, List<FormulairePresentation> listeForm) {
		try {
			ByteArrayInOutStream out = new ByteArrayInOutStream();
			PDFMergerUtility ut = new PDFMergerUtility();
			ut.addSource(generateDossier(candidature,listePresentation, listeDatePresentation,adresse, listePj,listeForm));
			if (ConstanteUtils.ADD_PJ_TO_DOSSIER){
				listePj.forEach(e->{
					try {
						if (e.getFilePj()!=null){
							Fichier file = e.getFilePj();
							String nameFile = file.getNomFichier();
							
							if (MethodUtils.isPdfFileName(nameFile)){
								ut.addSource(fileController.getInputStreamFromFichier(file,false));
							}else if (MethodUtils.isJpgFileName(nameFile)){							
								ByteArrayInOutStream baosImg = new ByteArrayInOutStream();
								PDDocument document = new PDDocument();
								PDRectangle PAGE_SIZE_A4 = PDPage.PAGE_SIZE_A4;
								
								PDPage page = new PDPage(PAGE_SIZE_A4);
								document.addPage(page); 
								PDPageContentStream contentStream = new PDPageContentStream(document, page);

								PDXObjectImage img = new PDJpeg(document, fileController.getInputStreamFromFichier(file,false));
								
								Float imgWidth = (float) img.getWidth();
								Float imgHeight = (float) img.getHeight();
								Float a4Width = PAGE_SIZE_A4.getWidth();
								Float a4Height = PAGE_SIZE_A4.getHeight();
								
								Float coef = 1.0f;
								if (imgWidth>a4Width){
									coef = a4Width/imgWidth;
									imgWidth = imgWidth * coef;
									imgHeight = imgHeight * coef;
								}
								
								if (imgHeight>a4Height){
									coef = a4Height/imgHeight;
									imgWidth = imgWidth * coef;
									imgHeight = imgHeight * coef;
								}
								
								contentStream.drawXObject(img,0,PAGE_SIZE_A4.getHeight() - imgHeight,imgWidth,imgHeight);
								contentStream.close();
								document.save(baosImg);
								ut.addSource(baosImg.getInputStream());
								baosImg.close();
								document.close();
							}					
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				});
			}
			
			ut.setDestinationFileName(getNomFichierDossier(candidature.getCandidat()));
			ut.setDestinationStream(out);
			ut.mergeDocuments();
			return out.getInputStream();
		}catch (Exception e) {			
		}
		return null;
	}

	/** Renvoi les candidatures non annulées d'un candidat
	 * @param candidat
	 * @return les candidatures non annulées d'un candidat
	 */
	public List<Candidature> getCandidatures(Candidat candidat) {
		List<Candidature> liste = new ArrayList<Candidature>();
		if (userController.getSecurityUserCandidat()!=null){
			liste.addAll(candidat.getCandidatures().stream().filter(e->e.getDatAnnulCand()==null).collect(Collectors.toList()));
		}else{
			SecurityCentreCandidature scc = userController.getCentreCandidature();
			if (scc!=null){
				if (scc.getIsAdmin()){
					liste.addAll(candidat.getCandidatures().stream().filter(e->e.getDatAnnulCand()==null).collect(Collectors.toList()));
				}else{
					if (scc.getIsGestAllCommission()){
						liste.addAll(candidat.getCandidatures().stream().filter(cand->cand.getDatAnnulCand()==null && cand.getFormation().getCommission().getCentreCandidature().getIdCtrCand().equals(scc.getIdCtrCand())).collect(Collectors.toList()));
					}else{
						liste.addAll(candidat.getCandidatures().stream().filter(cand->cand.getDatAnnulCand()==null && isIdCommInListIdComm(cand.getFormation().getCommission().getIdComm(),scc.getListeIdCommission())).collect(Collectors.toList()));
					}
					
				}
			}			
		}
		liste.forEach(e->e.setLastTypeDecision(getLastTypeDecisionCandidature(e)));
		return liste;
	}
	
	/**
	 * @param idComm
	 * @param listeIdComm
	 * @return true si l'id est trouvé dans la liste
	 */
	private Boolean isIdCommInListIdComm(Integer idComm, List<Integer> listeIdComm){
		return listeIdComm.stream().filter(i->i.equals(idComm)).findAny().isPresent();
	}
}
