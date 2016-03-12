package fr.univlorraine.ecandidat.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCand;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCandPK;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCandPK;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.FormulaireCandRepository;
import fr.univlorraine.ecandidat.repositories.PjCandRepository;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.mail.CandidatureMailBean;
import fr.univlorraine.ecandidat.utils.bean.presentation.FormulairePresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandActionPjWindow;
import fr.univlorraine.ecandidat.views.windows.InfoWindow;
import fr.univlorraine.ecandidat.views.windows.UploadWindow;

/**
 * Gestion des pièces
 * @author Kevin Hergalant
 */
@Component
public class CandidaturePieceController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient PieceJustifController pjController;
	@Resource
	private transient FormulaireController formulaireController;
	@Resource
	private transient LockCandidatController lockCandidatController;
	@Resource
	private transient MailController mailController;
	
	@Resource
	private transient CandidatureRepository candidatureRepository;
	@Resource
	private transient PjCandRepository pjCandRepository;
	@Resource
	private transient FormulaireCandRepository formulaireCandRepository;
	
	/**
	 * @return la légende pour les pices justificatives
	 */
	/*public String getLegendeStatutPiece(){
		String txt = "";
		for (TypeStatutPiece typ : tableRefController.getListeTypeStatutPiece()){
			txt = txt+" "+typ.getCodTypStatutPiece()+"="+i18nController.getI18nTraduction(typ.getI18nLibTypStatutPiece())+" / ";
		}
		txt = txt+NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE+"="+applicationContext.getMessage("candidature.no.pj", null, UI.getCurrent().getLocale());
		return txt;
	}*/
	
	/**
	 * @param candidature
	 * @return la liste des pj d'une candidature
	 */
	public List<PjPresentation> getPjCandidature(Candidature candidature){		
		List<PjPresentation> liste = new ArrayList<PjPresentation>();
		TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
		
		pjController.getPjForCandidature(candidature).forEach(e->{
			String libPj = i18nController.getI18nTraduction(e.getI18nLibPj());
			PjCand pjCand = getPjCandFromList(e,candidature.getPjCands());
			String libStatut = null;
			String codStatut = null;
			String commentaire = null;
			Fichier fichier = null;
			if (pjCand != null){
				fichier = pjCand.getFichier();
				if (pjCand.getTypeStatutPiece()!=null){
					libStatut = i18nController.getI18nTraduction(pjCand.getTypeStatutPiece().getI18nLibTypStatutPiece());
					codStatut = pjCand.getTypeStatutPiece().getCodTypStatutPiece();					
				}
				commentaire = pjCand.getCommentPjCand();
			}else{
				libStatut = i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece());
				codStatut = statutAtt.getCodTypStatutPiece();
			}
			liste.add(new PjPresentation(e,libPj,fichier,codStatut,libStatut,commentaire,e.getTemConditionnelPj()));			
		});
		return liste;
	}
	
	/** 
	 * @param piece
	 * @param listPjCand
	 * @return Renvoi un fichier si il existe
	 */
	private PjCand getPjCandFromList(PieceJustif piece, List<PjCand> listPjCand){
		Optional<PjCand> pjCandOpt = listPjCand.stream().filter(e->e.getId().getIdPj() == piece.getIdPj()).findAny();
		if (pjCandOpt.isPresent()){
			return pjCandOpt.get();
		}
		return null;
	}
	
	/**
	 * @param candidature
	 * @return la liste des formulaires d'une candidature
	 */
	public List<FormulairePresentation> getFormulaireCandidature(Candidature candidature) {
		String numDossier = candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin();
		List<FormulairePresentation> liste = new ArrayList<FormulairePresentation>();
		TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
		formulaireController.getFormulaireForCandidature(candidature).forEach(e->{
			String libForm = i18nController.getI18nTraduction(e.getI18nLibFormulaire());
			String urlForm = i18nController.getI18nTraduction(e.getI18nUrlFormulaire());
			/*Possibilité d'ajout du numdossier dans l'url sous la forme ${numDossierOpi}*/
			if (urlForm != null){				
				urlForm = urlForm.replaceAll(ConstanteUtils.VAR_REGEX_FORM_NUM_DOSSIER, numDossier);
			}
			
			FormulaireCand formulaireCand = getFormulaireCandFromList(e,candidature.getFormulaireCands());
			String libStatut = null;
			String codStatut = null;
			String commentaire = null;
			String reponses = null;
			if (formulaireCand != null){
				if (formulaireCand.getTypeStatutPiece()!=null){
					codStatut = formulaireCand.getTypeStatutPiece().getCodTypStatutPiece();
					libStatut = i18nController.getI18nTraduction(formulaireCand.getTypeStatutPiece().getI18nLibTypStatutPiece());
				}
				reponses = formulaireCand.getReponsesFormulaireCand();
			}else{
				libStatut = i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece());
				codStatut = statutAtt.getCodTypStatutPiece();
			}
			liste.add(new FormulairePresentation(e,libForm,urlForm,codStatut,libStatut,commentaire,e.getTemConditionnelFormulaire(),reponses));			
		});
		
		return liste;
	}

	/**
	 * @param formulaire
	 * @param listFormulaireCand
	 * @return recherche une reponse a formulaire
	 */
	private FormulaireCand getFormulaireCandFromList(Formulaire formulaire,
			List<FormulaireCand> listFormulaireCand) {
		Optional<FormulaireCand> formulaireCandOpt = listFormulaireCand.stream().filter(e->e.getId().getIdFormulaire() == formulaire.getIdFormulaire()).findAny();
		if (formulaireCandOpt.isPresent()){
			return formulaireCandOpt.get();
		}
		return null;
	}
	
	/**
	 * @param codStatut
	 * @param notification
	 * @return true si le statut de dossier de la candidatures permet de transmettre le dossier
	 */
	public Boolean isOkToTransmettreCandidatureStatutDossier(String codStatut, Boolean notification){
		if (codStatut.equals(NomenclatureUtils.TYPE_STATUT_ATT) || (codStatut.equals(NomenclatureUtils.TYPE_STATUT_INC))){
			return true;
		}else{
			if (notification){
				Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.statut", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			return false;
		}
	}
	
	/**
	 * @param listePj
	 * @param notification
	 * @return true si les pieces de la candidatures permettent de transmettre le dossier
	 */
	public Boolean isOkToTransmettreCandidatureStatutPiece(List<PjPresentation> listePj, Boolean notification){
		for (PjPresentation pj : listePj){
			if (pj.getCodStatut()==null){
				if (notification){
					Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.pj", new Object[]{pj.getLibPj()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
				return false;
			}else if (pj.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE)){
				if (notification){
					Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.pj.attente", new Object[]{pj.getLibPj()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
				return false;
			}
			else if (pj.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_REFUSE)){
				if (notification){
					Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.pj.refus", new Object[]{pj.getLibPj()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param candidature
	 * @param listePj
	 * @param listener
	 * @param notification
	 * @return vérifie que l'etat de la candidature permet de transmettre le dossier
	 */
	private Boolean isOkToTransmettreCandidature(Candidature candidature, List<PjPresentation> listePj, CandidatureListener listener, Boolean notification){		
		return (isOkToTransmettreCandidatureStatutDossier(candidature.getTypeStatut().getCodTypStatut(), notification) && isOkToTransmettreCandidatureStatutPiece(listePj, notification));
	}
	
	/** Transmet le dossier apres le click sur le bouton transmettre
	 * @param candidature
	 * @param listePj
	 * @param listener
	 */
	public void transmettreCandidatureAfterClick(Candidature candidature, List<PjPresentation> listePj, CandidatureListener listener){
		if (isOkToTransmettreCandidature(candidature, listePj, listener, true)){
			transmettreCandidature(candidature, listener, applicationContext.getMessage("candidature.validPJ.window.confirm", null, UI.getCurrent().getLocale()));
		}
	}
	
	/** Transmet le dossier apres un depot de pièce
	 * @param candidature
	 * @param listePj
	 * @param listener
	 * @param dateLimiteRetour 
	 */
	public void transmettreCandidatureAfterDepot(Candidature candidature, List<PjPresentation> listePj, CandidatureListener listener, String dateLimiteRetour){
		if (isOkToTransmettreCandidature(candidature, listePj, listener, false)){
			UI.getCurrent().addWindow(new InfoWindow(
									applicationContext.getMessage("informationImportanteWindow.tite", null, UI.getCurrent().getLocale()),
									applicationContext.getMessage("candidature.validPJ.window.info.afteraction", new Object[]{dateLimiteRetour}, UI.getCurrent().getLocale()),
									425, null));
			//transmettreCandidature(candidature, listener, applicationContext.getMessage("candidature.validPJ.window.confirm.afteraction", null, UI.getCurrent().getLocale()));
		}
	}
	
	/** Transmet la canidature
	 * @param candidature
	 * @param listener
	 * @param message
	 */
	public void transmettreCandidature(Candidature candidature, CandidatureListener listener, String message){
		ConfirmWindow confirmWindow = new ConfirmWindow(message, applicationContext.getMessage("candidature.validPJ.window.confirmTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(event -> {
			candidature.setTypeStatut(tableRefController.getTypeStatutReceptionne());
			candidature.setDatReceptDossierCand(LocalDate.now());
			candidature.setDatModTypStatutCand(LocalDateTime.now());
			candidature.setDatTransDossierCand(LocalDateTime.now());
			
			CandidatureMailBean mailBean = mailController.getCandidatureMailBean(candidature);
			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),NomenclatureUtils.MAIL_STATUT_RE,null,mailBean);
			
			listener.transmissionDossier(candidatureRepository.save(candidature));
			
			Notification.show(applicationContext.getMessage("candidature.validPJ.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Ajoute un fichier a une pj 
	 * @param pieceJustif
	 * @param candidature
	 * @param listener
	 */
	public void addFileToPieceJustificative(PjPresentation pieceJustif, Candidature candidature,
			CandidatureListener listener) {
		
		Assert.notNull(candidature);
		
		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		
		String user = userController.getCurrentUserLogin();
		
		String cod = ConstanteUtils.TYPE_FICHIER_PJ_CAND+"_"+candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin()+"_"+pieceJustif.getPieceJustif().getIdPj();
		UploadWindow uw = new UploadWindow(cod,ConstanteUtils.TYPE_FICHIER_CANDIDAT);
		uw.addUploadWindowListener(file->{
			if (file == null){
				return;
			}
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}
			PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(),candidature.getIdCand());
			PjCand pjCand = pjCandRepository.findOne(pk);
			
			if (pjCand==null){
				pjCand = new PjCand(pk, user, candidature, pieceJustif.getPieceJustif());
			}
						
			Fichier fichier = fileController.createFile(file,user,ConstanteUtils.TYPE_FICHIER_CANDIDAT);
			
			pjCand.setLibFilePjCand(fichier.getNomFichier());
			pjCand.setUserModPjCand(user);
			pjCand.setFichier(fichier);
			
			TypeStatutPiece statutTr = tableRefController.getTypeStatutPieceTransmis(); 
			pjCand.setTypeStatutPiece(statutTr);
			
			pjCand = pjCandRepository.save(pjCand);
			
			pieceJustif.setFilePj(fichier);
			pieceJustif.setCodStatut(statutTr.getCodTypStatutPiece());
			pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutTr.getI18nLibTypStatutPiece()));
			
			candidature.setUserModCand(user);
			candidature.updatePjCand(pjCand);
			candidature.setDatModCand(LocalDateTime.now());
			Candidature candidatureSave = candidatureRepository.save(candidature);
			
			
			listener.pjModified(pieceJustif, candidatureSave);
			
			Notification.show(applicationContext.getMessage("window.upload.success", new Object[]{file.getFileName()}, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
			uw.close();
			
		});
		UI.getCurrent().addWindow(uw);
	}
	
	/** Change le statut est concerne d'une pj
	 * @param pieceJustif
	 * @param isConcerned
	 * @param candidature
	 * @param listener
	 */
	public void setIsConcernedPieceJustificative(PjPresentation pieceJustif,
			Boolean isConcerned, Candidature candidature, CandidatureListener listener) {
		Assert.notNull(candidature);
		
		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		String user = userController.getCurrentUserLogin();
		if (isConcerned){				
			ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("pj.window.concerne", new Object[]{pieceJustif.getLibPj()}, UI.getCurrent().getLocale()), applicationContext.getMessage("pj.window.conditionnel.title", null, UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}
				PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(),candidature.getIdCand());
				PjCand pjCand = pjCandRepository.findOne(pk);	
				if (pjCand!=null && pjCand.getFichier()==null){				
					pjCandRepository.delete(pjCand);
					candidature.setUserModCand(user);
					candidature.setDatModCand(LocalDateTime.now());
					candidature.removePjCand(pjCand);
					
					TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
					pieceJustif.setCodStatut(statutAtt.getCodTypStatutPiece());
					pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
					
					Candidature candidatureSave = candidatureRepository.save(candidature);				
					listener.pjModified(pieceJustif, candidatureSave);
				}				
			});
			UI.getCurrent().addWindow(confirmWindow);
		}else{
			ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("pj.window.nonConcerne", new Object[]{pieceJustif.getLibPj()}, UI.getCurrent().getLocale()), applicationContext.getMessage("pj.window.conditionnel.title", null, UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}
				PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(),candidature.getIdCand());
				PjCand pjCand = pjCandRepository.findOne(pk);	
				if (pjCand==null){
					
					pjCand = new PjCand(pk, user, candidature, pieceJustif.getPieceJustif());
					pjCand.setLibFilePjCand(null);
					pjCand.setUserModPjCand(user);
					pjCand.setFichier(null); 
					
					TypeStatutPiece statutNotConcern = tableRefController.getTypeStatutPieceNonConcerne();
					pieceJustif.setCodStatut(statutNotConcern.getCodTypStatutPiece());
					pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutNotConcern.getI18nLibTypStatutPiece()));
					
					pjCand.setTypeStatutPiece(statutNotConcern);
					pjCand = pjCandRepository.save(pjCand);
					pieceJustif.setFilePj(null);
					pieceJustif.setCodStatut(statutNotConcern.getCodTypStatutPiece());
					pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutNotConcern.getI18nLibTypStatutPiece()));
					
					candidature.setUserModCand(user);
					candidature.updatePjCand(pjCand);
					candidature.setDatModCand(LocalDateTime.now());
					Candidature candidatureSave = candidatureRepository.save(candidature);
					listener.pjModified(pieceJustif, candidatureSave);
				}
				
			});
			
			UI.getCurrent().addWindow(confirmWindow);
		}		
	}
	
	/** Change le statut est concerné d'un formulaire
	 * @param formulaire
	 * @param isConcerned
	 * @param candidature
	 * @param listener
	 */
	public void setIsConcernedFormulaire(FormulairePresentation formulaire,
			Boolean isConcerned, Candidature candidature, CandidatureListener listener) {
		Assert.notNull(candidature);
		
		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		String user = userController.getCurrentUserLogin();
		if (isConcerned){				
			ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("formulaire.window.concerne", new Object[]{formulaire.getLibFormulaire()}, UI.getCurrent().getLocale()), applicationContext.getMessage("formulaire.window.conditionnel.title", null, UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}
				FormulaireCandPK pk = new FormulaireCandPK(formulaire.getFormulaire().getIdFormulaire(),candidature.getIdCand());
				FormulaireCand formulaireCand = formulaireCandRepository.findOne(pk);	
				if (formulaireCand!=null){				
					formulaireCandRepository.delete(formulaireCand);
					candidature.setUserModCand(user);
					candidature.setDatModCand(LocalDateTime.now());
					candidature.removeFormulaireCand(formulaireCand);

					TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
					formulaire.setCodStatut(statutAtt.getCodTypStatutPiece());
					formulaire.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
					
					Candidature candidatureSave = candidatureRepository.save(candidature);				
					listener.formulaireModified(formulaire, candidatureSave);
				}				
			});
			UI.getCurrent().addWindow(confirmWindow);
		}else{
			ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("formulaire.window.nonConcerne", new Object[]{formulaire.getLibFormulaire()}, UI.getCurrent().getLocale()), applicationContext.getMessage("formulaire.window.conditionnel.title", null, UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}
				FormulaireCandPK pk = new FormulaireCandPK(formulaire.getFormulaire().getIdFormulaire(),candidature.getIdCand());
				FormulaireCand formulaireCand = formulaireCandRepository.findOne(pk);	
				if (formulaireCand==null){
					
					formulaireCand = new FormulaireCand(pk, user, candidature, formulaire.getFormulaire());
					formulaireCand.setUserModFormulaireCand(user);
					
					TypeStatutPiece statutNotConcern = tableRefController.getTypeStatutPieceNonConcerne();
					formulaire.setCodStatut(statutNotConcern.getCodTypStatutPiece());
					formulaire.setLibStatut(i18nController.getI18nTraduction(statutNotConcern.getI18nLibTypStatutPiece()));
					
					formulaireCand.setTypeStatutPiece(statutNotConcern);
					formulaireCand = formulaireCandRepository.save(formulaireCand);
					
					candidature.setUserModCand(user);
					candidature.updateFormulaireCand(formulaireCand);
					candidature.setDatModCand(LocalDateTime.now());
					Candidature candidatureSave = candidatureRepository.save(candidature);
					listener.formulaireModified(formulaire, candidatureSave);
				}
				
			});
			
			UI.getCurrent().addWindow(confirmWindow);
		}	
	}

	/** Ajoute un fichier en PJ
	 * @param pieceJustif
	 * @param candidature
	 * @param listener
	 */
	public void deleteFileToPieceJustificative(PjPresentation pieceJustif, Candidature candidature,
			CandidatureListener listener) {
		Assert.notNull(candidature);
		
		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		if (!fileController.isModeStockageOk(pieceJustif.getFilePj(),false)){
			return;
		}
		Fichier fichier = pieceJustif.getFilePj();		
		System.out.println("Question supression = "+fichier);
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("file.window.confirmDelete", new Object[]{fichier.getNomFichier()}, UI.getCurrent().getLocale()), applicationContext.getMessage("file.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(file -> {
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}
			try{
				PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(),candidature.getIdCand());
				PjCand pjCand = pjCandRepository.findOne(pk);
				
				String user = userController.getCurrentUserLogin();
				
				removeFileToPj(pjCand);
				
				TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
				pieceJustif.setFilePj(null);
				
				pieceJustif.setCodStatut(statutAtt.getCodTypStatutPiece());
				pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
				
				candidature.setUserModCand(user);
				candidature.setDatModCand(LocalDateTime.now());
				candidature.removePjCand(pjCand);
				Candidature candidatureSave = candidatureRepository.save(candidature);
				listener.pjModified(pieceJustif, candidatureSave);
			}catch (FileException e){
				Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
			}
			
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	//@Transactional(rollbackFor=FileException.class)
	public void removeFileToPj(PjCand pjCand) throws FileException{
		System.out.println("Je tente de supprimer pjcand = "+pjCand);
		Fichier fichier = pjCand.getFichier();
		pjCandRepository.delete(pjCand);
		if (fichier != null){
			System.out.println("Fichier non null = "+fichier);
			fileController.deleteFichier(fichier);
		}		
	}
	
	/** Change le statut d'une liste de pj
	 * @param listePj
	 * @param candidature
	 * @param listener
	 */
	public void changeStatutPj(List<PjPresentation> listePj, Candidature candidature,
			CandidatureListener listener){
		Assert.notNull(candidature);
		
		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		
		CtrCandActionPjWindow window = new CtrCandActionPjWindow(listePj);
		window.addChangeStatutPieceWindowListener((t,c)->{
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}
			String user = userController.getCurrentUserLogin();
			listePj.forEach(e->{
				PjCandPK pk = new PjCandPK(e.getPieceJustif().getIdPj(),candidature.getIdCand());
				PjCand pjCand = pjCandRepository.findOne(pk);		
				if (pjCand==null){
					pjCand = new PjCand(pk, user, candidature, e.getPieceJustif());
				}
				pjCand.setTypeStatutPiece(t);
				pjCand.setCommentPjCand(c);
				pjCand.setUserModPjCand(user);
				pjCand.setDatModStatutPjCand(LocalDateTime.now());
				pjCand.setUserModStatutPjCand(user);
				pjCand = pjCandRepository.save(pjCand);
				candidature.updatePjCand(pjCand);
				if (pjCand.getTypeStatutPiece()!=null){
					e.setLibStatut(i18nController.getI18nTraduction(pjCand.getTypeStatutPiece().getI18nLibTypStatutPiece()));
					e.setCodStatut(pjCand.getTypeStatutPiece().getCodTypStatutPiece());
				}
				e.setCommentaire(c);
			});
			candidature.setUserModCand(user);
			Candidature candidatureSave = candidatureRepository.save(candidature);
			listener.pjsModified(listePj, candidatureSave);
		});
		UI.getCurrent().addWindow(window);
	}
}
