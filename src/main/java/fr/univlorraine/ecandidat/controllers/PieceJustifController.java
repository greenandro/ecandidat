package fr.univlorraine.ecandidat.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.repositories.PieceJustifRepository;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.PieceJustifWindow;
import fr.univlorraine.ecandidat.views.windows.UploadWindow;

/**
 * Gestion de l'entité pieceJustif
 * @author Kevin Hergalant
 */
@Component
public class PieceJustifController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient PieceJustifRepository pieceJustifRepository;
	@Resource
	private transient FileController fileController;
	
	/**
	 * @return liste des pieceJustifs
	 */
	public List<PieceJustif> getPieceJustifs() {
		return pieceJustifRepository.findAll();
	}
	
	/**
	 * @param cand
	 * @return la liste des PJ à afficher pour une candidature
	 * Toute les commune de la scol + toute les commune du ctr + toutes les pieces de la formation + les pièces effacées
	 */
	public List<PieceJustif> getPjForCandidature(Candidature cand){
		Formation formation = cand.getFormation();
		List<PieceJustif> liste = new ArrayList<PieceJustif>();
		liste.addAll(getPieceJustifsByCtrCandEnService(null, true));
		liste.addAll(getPieceJustifsByCtrCandEnService(formation.getCommission().getCentreCandidature().getIdCtrCand(), true));
		liste.addAll(formation.getPieceJustifs().stream().filter(e->e.getTesPj()).collect(Collectors.toList()));
		cand.getPjCands().forEach(e->{
			liste.add(e.getPieceJustif());
		});
		return liste.stream().distinct().collect(Collectors.toList());
	}
	

	/**
	 * @param idCtrCand
	 * @return a liste des PJ d'un ctr
	 */
	public List<PieceJustif> getPieceJustifsByCtrCand(Integer idCtrCand) {
		return pieceJustifRepository.findByCentreCandidatureIdCtrCand(idCtrCand);
	}
	
	/**
	 * @param idCtrCand
	 * @return la liste des PJ en service d'un ctr
	 */
	public List<PieceJustif> getPieceJustifsByCtrCandEnService(Integer idCtrCand, Boolean commun) {
		return pieceJustifRepository.findByCentreCandidatureIdCtrCandAndTesPjAndTemCommunPj(idCtrCand,true,commun);
	}
	
	/**
	 * @return la liste des PJ communes de la scol
	 */
	public List<PieceJustif> getPieceJustifsCommunScolEnService() {
		return pieceJustifRepository.findByCentreCandidatureIdCtrCandAndTesPjAndTemCommunPj(null,true,true);
	}
	
	/**
	 * @return la liste des PJ communes de la scol
	 */
	public List<PieceJustif> getPieceJustifsCommunCtrCandEnService(Integer idCtrCand) {
		List<PieceJustif> liste = new ArrayList<PieceJustif>();
		liste.addAll(pieceJustifRepository.findByCentreCandidatureIdCtrCandAndTesPjAndTemCommunPj(null,true,true));
		liste.addAll(pieceJustifRepository.findByCentreCandidatureIdCtrCandAndTesPjAndTemCommunPj(idCtrCand,true,true));
		return liste;
	}
	
	/** Renvoie la liste des pj pour un ctrCand +
	 *  scol
	 * @param idCtrCand
	 * @return la liste des PJ
	 */
	public List<PieceJustif> getPieceJustifsByCtrCandAndScolCentral(Integer idCtrCand) {
		List<PieceJustif> liste = new ArrayList<PieceJustif>();
		liste.addAll(getPieceJustifsByCtrCandEnService(null, false));
		liste.addAll(getPieceJustifsByCtrCandEnService(idCtrCand, false));
		return liste;
	}
	
	/**
	 * Ouvre une fenêtre d'édition d'un nouveau pieceJustif.
	 * @param ctrCand 
	 */
	public void editNewPieceJustif(CentreCandidature ctrCand) {
		PieceJustif pj = new PieceJustif(userController.getCurrentUserLogin());
		pj.setI18nLibPj(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_PJ_LIB)));
		pj.setCentreCandidature(ctrCand);
		UI.getCurrent().addWindow(new PieceJustifWindow(pj));
	}
	
	/**
	 * Ouvre une fenêtre d'édition de pieceJustif.
	 * @param pieceJustif
	 */
	public void editPieceJustif(PieceJustif pieceJustif) {
		Assert.notNull(pieceJustif);

		/* Verrou */
		if (!lockController.getLockOrNotify(pieceJustif, null)) {
			return;
		}
		PieceJustifWindow window = new PieceJustifWindow(pieceJustif);
		window.addCloseListener(e->lockController.releaseLock(pieceJustif));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un pieceJustif
	 * @param pieceJustif
	 */
	public void savePieceJustif(PieceJustif pieceJustif) {
		Assert.notNull(pieceJustif);

		/* Verrou */
		if (pieceJustif.getIdPj()!=null && !lockController.getLockOrNotify(pieceJustif, null)) {
			return;
		}
		pieceJustif.setUserModPj(userController.getCurrentUserLogin());
		pieceJustif.setI18nLibPj(i18nController.saveI18n(pieceJustif.getI18nLibPj()));
		pieceJustif = pieceJustifRepository.saveAndFlush(pieceJustif);
		
		lockController.releaseLock(pieceJustif);
	}

	/**
	 * Supprime une pieceJustif
	 * @param pieceJustif
	 */
	public void deletePieceJustif(PieceJustif pieceJustif) {
		Assert.notNull(pieceJustif);

		/*Verification que le mode de fichier et celui de l'application sont identiques*/
		if (!fileController.isModeStockageOk(pieceJustif.getFichier(),true)){
			return;
		}
		
		/*Verification que la pice n'est rattachée à rien*/
		if (pieceJustifRepository.findOne(pieceJustif.getIdPj()).getPjCands().size()>0){
			Notification.show(applicationContext.getMessage("pieceJustif.error.delete", new Object[]{PjCand.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		/* Verrou */
		if (!lockController.getLockOrNotify(pieceJustif, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("pieceJustif.window.confirmDelete", new Object[]{pieceJustif.getCodPj()}, UI.getCurrent().getLocale()), applicationContext.getMessage("pieceJustif.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {			
			/*On vérifie que la PJ est utilisée par des formation ou est commune, dans ce cas-->2eme confirmation*/
			String question = null;			
			if (pieceJustifRepository.findOne(pieceJustif.getIdPj()).getFormations().size()>0){
				question = applicationContext.getMessage("pieceJustif.window.confirmDelete.form", null, UI.getCurrent().getLocale());
			}else if (pieceJustif.getTemCommunPj()){
				question = applicationContext.getMessage("pieceJustif.window.confirmDelete.commun", null, UI.getCurrent().getLocale()); 
			}
			
			if (question==null){
				deletePj(pieceJustif);
			}else{
				/* Verrou */
				if (!lockController.getLockOrNotify(pieceJustif, null)) {
					return;
				}
				ConfirmWindow confirmWindowPJUse = new ConfirmWindow(question, applicationContext.getMessage("pieceJustif.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
				confirmWindowPJUse.addBtnOuiListener(y -> {
					deletePj(pieceJustif);
				});
				confirmWindowPJUse.addCloseListener(y -> {
					/* Suppression du lock */
					lockController.releaseLock(pieceJustif);			
				});
				UI.getCurrent().addWindow(confirmWindowPJUse);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(pieceJustif);			
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	/** SUpprime une pièce justificative
	 * @param pieceJustif
	 */
	private void deletePj(PieceJustif pieceJustif){
		/* Contrôle que le client courant possède toujours le lock */
		if (lockController.getLockOrNotify(pieceJustif, null)) {				
			try{
				deletePjDbAndFile(pieceJustif);
				/* Suppression du lock */
				lockController.releaseLock(pieceJustif);
			}catch(Exception ex){
				Notification.show(applicationContext.getMessage("file.error.delete", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
		}
	}
	
	/** Supprime une PJ
	 * @param pieceJustif
	 * @throws FileException
	 */
	@Transactional(rollbackFor=FileException.class)
	private void deletePjDbAndFile(PieceJustif pieceJustif) throws FileException{
		Fichier fichier = pieceJustif.getFichier();		
		pieceJustifRepository.delete(pieceJustif);
		if (fichier != null){
			fileController.deleteFichier(fichier);
		}
	}
	
	/** AJoute un fichier à une pièce justif
	 * @param pieceJustif
	 */
	public void addFileToPieceJustificative(PieceJustif pieceJustif) {
		String user = userController.getCurrentUserLogin();
		String cod = ConstanteUtils.TYPE_FICHIER_PJ_GEST+"_"+pieceJustif.getIdPj();
		UploadWindow uw = new UploadWindow(cod,ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE);
		uw.addUploadWindowListener(file->{
			if (file == null){
				return;
			}
			Fichier fichier = fileController.createFile(file,user,ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE);
			pieceJustif.setFichier(fichier);
			pieceJustifRepository.save(pieceJustif);
			Notification.show(applicationContext.getMessage("window.upload.success", new Object[]{file.getFileName()}, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
			uw.close();
		});
		UI.getCurrent().addWindow(uw);
	}
	
	/** Supprime un fichier d'une pieceJustif
	 * @param pieceJustif
	 */
	public void deleteFileToPieceJustificative(PieceJustif pieceJustif) {
		if (!fileController.isModeStockageOk(pieceJustif.getFichier(),true)){
			return;
		}
		Fichier fichier = pieceJustif.getFichier();		
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("file.window.confirmDelete", new Object[]{fichier.getNomFichier()}, UI.getCurrent().getLocale()), applicationContext.getMessage("file.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(file -> {
			try{
				removeFileToPj(pieceJustif,fichier);
			}catch (FileException e){
				pieceJustif.setFichier(fichier);
				Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
			}
			
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	/** Supprime un fichier d'une PJ
	 * @param pieceJustif
	 * @param fichier
	 * @throws FileException
	 */
	@Transactional(rollbackFor=FileException.class)
	private void removeFileToPj(PieceJustif pieceJustif, Fichier fichier) throws FileException{
		pieceJustif.setFichier(null);
		pieceJustifRepository.save(pieceJustif);
		fileController.deleteFichier(fichier);			
	}
	
	/** Verifie l'unicité du code
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodPjUnique(String cod, Integer id) {
		PieceJustif pieceJustif = pieceJustifRepository.findByCodPj(cod);
		if (pieceJustif==null){
			return true;
		}else{
			if (pieceJustif.getIdPj().equals(id)){
				return true;
			}
		}
		return false;
	}	
}
