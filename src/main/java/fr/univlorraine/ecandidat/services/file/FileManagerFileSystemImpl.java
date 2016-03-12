package fr.univlorraine.ecandidat.services.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;


/**
 * Class d'implementation de l'interface de manager de fichier pour le File System
 * @author Kevin Hergalant
 *
 */
@Component(value="fileManagerFileSystemImpl")
public class FileManagerFileSystemImpl implements FileManager {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5678691565524342452L;
	
	private Logger logger = LoggerFactory.getLogger(FileManagerFileSystemImpl.class);
	
	/*applicationContext pour les messages*/
	@Resource
	private transient ApplicationContext applicationContext;
	
	/*Informations de context*/
	private String folderCandidat;
	private String folderGestionnaire;
	

	/**
	 * Constructeur par défaut
	 */
	public FileManagerFileSystemImpl() {
		super();
	}
	
	/** Constructeur et affectation des variables
	 * @param folderGestionnaire
	 * @param folderCandidat
	 */
	public FileManagerFileSystemImpl(String folderGestionnaire, String folderCandidat) {
		super();
		this.folderGestionnaire = folderGestionnaire;
		this.folderCandidat = folderCandidat;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#getType()
	 */
	@Override
	public String getType() {
		return ConstanteUtils.TYPE_FICHIER_STOCK_FILE_SYSTEM;
	}	
	
	/**
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#testSession()
	 */
	@Override
	public Boolean testSession() {
		Boolean testGest = directoryExistFileSystem(folderCandidat);			
		Boolean testCand = directoryExistFileSystem(folderCandidat);
		if (!testGest || !testCand){
			return false;
		}
		return true;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#deleteFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier)
	 */
	@Override
	public void deleteFile(Fichier fichier) throws FileException {
		try {
			File file = new File(getFilePath(fichier));
			if (!file.delete()) {
				throw new FileException(applicationContext.getMessage("file.error.delete", null, UI.getCurrent().getLocale()));
			}
		} catch (Exception e) {
			logger.error("Stockage de fichier - FileSystem : erreur de suppression du fichier ",e);
			throw new FileException(applicationContext.getMessage("file.error.delete", null, UI.getCurrent().getLocale()),e);
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#getInputStreamFromFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier)
	 */
	@Override
	public InputStream getInputStreamFromFile(Fichier fichier)
			throws FileException {
		try {
			return new FileInputStream(new File(getFilePath(fichier)));
		} catch (FileNotFoundException e) {
			logger.error("Stockage de fichier - FileSystem : erreur de recuperation du fichier ",e);
			throw new FileException(applicationContext.getMessage("file.error.stream", null, UI.getCurrent().getLocale()),e);
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#createFileFromUpload(fr.univlorraine.ecandidat.utils.ByteArrayInOutStream, java.lang.String, java.lang.String, long, java.lang.String, java.lang.String)
	 */
	@Override
	public FileCustom createFileFromUpload(ByteArrayInOutStream file,
			String mimeType, String filename, long length, String typeFichier, String prefixe) throws FileException{
		try{
			String name = prefixe+"_"+MethodUtils.cleanFileName(filename);
			String path = "";
			if (typeFichier.equals(ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE)){
				path = folderGestionnaire+name;
			}else{
				path = folderCandidat+name;
			}
			OutputStream outputStream = new FileOutputStream (path);
			file.writeTo(outputStream);
			outputStream.close();
			file.close();
			return getFileFromDoc(name,filename,prefixe);
		}catch (Exception e){
			logger.error("Stockage de fichier - FileSystem : erreur de creation du fichier ",e);
			throw new FileException(applicationContext.getMessage("file.error.create", null, UI.getCurrent().getLocale()),e);
		}
		
	}
	
	/** Retourne le path d'un fichier suivant son type
	 * @param fichier
	 * @return le path d'un fichier suivant son type
	 */
	private String getFilePath(Fichier fichier){
		if (fichier.getTypFichier().equals(ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE)){
			return folderGestionnaire+fichier.getFileFichier();
		}else{
			return folderCandidat+fichier.getFileFichier();
		}
	}

	/** Renvoi un customFIle a partir d'un document fileSystem
	 * @param doc
	 * @return un customFIle a partir d'un document fileSystem
	 */
	private FileCustom getFileFromDoc(String id, String fileName, String cod){
		return new FileCustom(id,cod,fileName, "");
	}
	
	/** Verifie qu'un dossier existe en mode filesystem
	 * @param path
	 * @return true si le directory exist
	 */
	private Boolean directoryExistFileSystem(String path) {
		if (path == null || path.equals("")) {
			return false;
		}
		File f = new File(path);
		if (f.exists() && f.isDirectory()) {
			return true;
		}
		logger.error("Stockage de fichier - FileSystem : l'arborescence de dossier est invalide pour "+path);
		return false;
	}
}
