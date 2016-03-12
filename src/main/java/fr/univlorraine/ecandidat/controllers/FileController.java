package fr.univlorraine.ecandidat.controllers;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.validation.constraints.Size;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier_;
import fr.univlorraine.ecandidat.repositories.FichierRepository;
import fr.univlorraine.ecandidat.services.file.FileCustom;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.services.file.FileManager;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Controller gérant les appels fichier
 * @author Kevin Hergalant
 *
 */
@Component
public class FileController {
		
	/*applicationContext pour les messages*/
	@Resource
	private transient ApplicationContext applicationContext;
	
	@Resource
	private transient FileManager fileManager;
	
	@Resource
	private transient UserController userController;
	
	@Resource
	private transient ParametreController parametreController;
	
	@Resource
	private transient FichierRepository fichierRepository;
	
	/** Mode de dematerialisation
	 * @return le mode de demat
	 */
	public String getModeDemat(){
		if (parametreController.getIsUtiliseDemat() && fileManager!=null){
			return fileManager.getType();
		}
		return ConstanteUtils.TYPE_FICHIER_STOCK_NONE;
	}
	
	/** Mode de dematerialisation
	 * @return le mode de demat pour les pièces backoffice-->pas besoin du paramètre IsUtiliseDemat
	 */
	public String getModeDematBackoffice(){
		if (fileManager!=null){
			return fileManager.getType();
		}
		return ConstanteUtils.TYPE_FICHIER_STOCK_NONE;
	}
	
	/**
	 * Teste la démat
	 */
	public void testDemat(){
		if (fileManager!=null){
			if (!fileManager.testSession()){
				Notification.show(applicationContext.getMessage("parametre.demat.check.ko", null, UI.getCurrent().getLocale()));
			}else{
				Notification.show(applicationContext.getMessage("parametre.demat.check.ok", null, UI.getCurrent().getLocale()));
			}
		}else{
			Notification.show(applicationContext.getMessage("parametre.demat.check.disable", null, UI.getCurrent().getLocale()));			
		}
	}
	
	/** Verifie quele nom de fichier n'est pas trop long
	 * @return la taille max d'un nom de fichier
	 */
	public Integer getSizeMaxFileName(){
		try {
			return Fichier.class.getDeclaredField(Fichier_.nomFichier.getName()).getAnnotation(Size.class).max();
		} catch (NoSuchFieldException | SecurityException e) {
			return 0;
		}
	}
	
	/** Verifie si le nom du fichier est correct
	 * @param fileName
	 * @param sizeMax
	 * @return true si le nom de fichier est ok
	 */
	public Boolean isFileNameOk(String fileName, Integer sizeMax){
		if (fileName==null || fileName.length()==0){
			return false;
		}else{
			return fileName.length()<sizeMax;
		}
	}

	/** Créé un fichier provenant de l'upload
	 * @param file
	 * @param mimeType
	 * @param filename
	 * @param length
	 * @param typFile
	 * @param prefixe
	 * @return le fichier
	 */
	public FileCustom createFileFromUpload(ByteArrayInOutStream file,
			String mimeType, String filename, long length, String typFile, String prefixe) {
		try {
			return fileManager.createFileFromUpload(file,mimeType,filename,length, typFile, prefixe);
		} catch (FileException e) {
			Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
			return null;
		}
	}
	
	/** Renvoie l'inputstream d'un fichier
	 * @param fichier
	 * @param isBackoffice boolean pour indiquer que les pièces proviennent du backoffice
	 * @return l'InputStream d'un fichier
	 */
	public InputStream getInputStreamFromFichier(Fichier fichier, Boolean isBackoffice){
		if (!isModeStockageOk(fichier, isBackoffice)){
			return null;
		}
		try {
			return fileManager.getInputStreamFromFile(fichier);
		} catch (FileException e) {
			Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
			return null;
		}
	}
	
	/** Verifie le mode de stockage d'un fichier et de l'application, si différent --> erreur
	 * @param fichier
	 * @param isBackoffice boolean pour indiquer que les pièces proviennent du backoffice
	 * @return true si le mode de stockage est ok
	 */
	public Boolean isModeStockageOk(Fichier fichier, Boolean isBackoffice){
		if (fichier == null){
			return true;
		}
		
		String modeDemat = null;
		if (isBackoffice){
			modeDemat = getModeDematBackoffice();
		}else{
			modeDemat = getModeDemat();
		}
		
		if (!fichier.getTypStockageFichier().equals(modeDemat)){
			Notification.show(applicationContext.getMessage("file.error.mode", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return false;
		}
		return true;
	}
		
	/** Créé un fichier à partir d'un customFile
	 * @param file
	 * @param user 
	 * @return le fichier créé
	 */
	public Fichier createFile(FileCustom file, String user, String typFichier){
		/**TODO : try catch si erreur!*/
		String lib = file.getFileName().replaceAll(" ", "_");
		return fichierRepository.save(new Fichier(file.getCod(),file.getId(),lib,typFichier,fileManager.getType(),user));
	}
	
	/** Supprime un fichier
	 * @param fichier
	 * @throws FileException
	 */
	@Transactional(rollbackFor=FileException.class)
	public void deleteFichier(Fichier fichier) throws FileException{
		fichierRepository.delete(fichier);
		fileManager.deleteFile(fichier);		 
	}
}
