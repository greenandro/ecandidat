package fr.univlorraine.ecandidat.services.file;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.client.util.FileUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Class d'implementation de l'interface de manager de fichier pour CMIS
 * @author Kevin Hergalant
 *
 */
@Component(value="fileManagerCmisImpl")
public class FileManagerCmisImpl implements FileManager {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4858252315964410899L;
	
	private Logger logger = LoggerFactory.getLogger(FileManagerCmisImpl.class);
	
	/*applicationContext pour les messages*/
	@Resource
	private transient ApplicationContext applicationContext;

	/*La session CMIS*/
	/*
	 * CMIS itself is stateless. OpenCMIS uses the concept of a session to cache data across calls and to deal with user authentication. 
	 * The session object is also used as entry point to all CMIS operations and objects. 
	 * Because a session is only a client side concept, the session object needs not to be closed or released when it's not needed anymore.
	 **/
	private Session cmisSession;
	
	/*Les informations de context*/
	String user;
	String password;
	String url;
	String repository;
	String idFolderGestionnaire;
	String idFolderCandidat;
	
	/**
	 * Constructeur par défaut
	 */
	public FileManagerCmisImpl() {
		super();
	}

	/** Constructeur et affectation des variables
	 * @param user
	 * @param password
	 * @param url
	 * @param repository
	 * @param idFolderGestionnaire
	 * @param idFolderCandidat
	 */
	public FileManagerCmisImpl(String user, String password, String url, String repository, String idFolderGestionnaire,
			String idFolderCandidat) {
		super();
		this.user = user;
		this.password = password;
		this.url = url;
		this.repository = repository;
		this.idFolderGestionnaire = idFolderGestionnaire;
		this.idFolderCandidat = idFolderCandidat;
	}

	/**
	 * @return la session CMIS
	 */
	public Session getCmisSession(){
		if (cmisSession == null){
			cmisSession = cmisSession();
		}
		return cmisSession;
	}
	
	/**
	 * @return la session CMIS
	 */
	private Session cmisSession(){
		if (url == null || url.equals("") || repository == null || repository.equals("") || user == null || user.equals("")){
			return null;
		}		
		
		try{
			// default factory implementation
			SessionFactory factory = SessionFactoryImpl.newInstance();
			Map<String, String> parameter = new HashMap<String, String>();

			// user credentials
			parameter.put(SessionParameter.USER, user);
			parameter.put(SessionParameter.PASSWORD, password);

			// connection settings
			parameter.put(SessionParameter.ATOMPUB_URL,	url);
			parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			parameter.put(SessionParameter.REPOSITORY_ID, repository);
			// create session
			Session session =  factory.createSession(parameter);
			if (session == null){
				logger.error("Stockage de fichier - Impossible de se connecter au serveur de fichier CMIS");
				return null;
			}else{
				if (directoryExistCMIS(idFolderGestionnaire,session) && directoryExistCMIS(idFolderCandidat,session)){
					return session;
				}
			}
			return null;		
		}catch (Exception e){
			logger.error("Stockage de fichier - Impossible de se connecter au serveur de fichier CMIS", e);
			return null;
		}
	}
	
	@Override
	public Boolean testSession() {
		Session cmisSession = cmisSession();
		if (cmisSession != null){
			Boolean testGest = directoryExistCMIS(idFolderGestionnaire,getCmisSession());			
			Boolean testCand = directoryExistCMIS(idFolderCandidat,getCmisSession());
			if (!testGest || !testCand){
				return false;
			}
			return true;
		}
		return false;
	}
	
	/** Verifie qu'un dossier existe en mode CMIS
	 * @param idFolder
	 * @return
	 */
	private Boolean directoryExistCMIS(String idFolder, Session cmisSession) {
		if (idFolder == null || idFolder.equals("")) {
			return false;
		}
		try{
			CmisObject object = cmisSession.getObject(cmisSession.createObjectId(idFolder));
			if (!(object instanceof Folder)){
				logger.error("Stockage de fichier - CMIS : l'object CMIS "+idFolder+" n'est pas un dossier");
				return false;
			}
		}catch(Exception e){
			logger.error("Stockage de fichier - CMIS : erreur sur l'object CMIS "+idFolder);
			return false;
		}
		return true;
	}
	
	/**
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#getType()
	 */
	@Override
	public String getType() {
		return ConstanteUtils.TYPE_FICHIER_STOCK_CMIS;
	}
	
	/**
	 * @param id
	 * @return l'objet CMIS par son id
	 */
	public CmisObject getObjectById(String id){
		Session session = getCmisSession();
		CmisObject object = session.getObject(session.createObjectId(id));
		return object;
	}
	
	
	/**
	 * @return le folder CMIS des candidat
	 */
	public Folder getFolderCandidat(){
		CmisObject object = getObjectById(idFolderCandidat);
		Folder folder = (Folder) object;
		return folder;
	}
	
	/**
	 * @return le folder CMIS des gestionnaires
	 */
	public Folder getFolderGestionnaire(){
		CmisObject object = getObjectById(idFolderGestionnaire);
		Folder folder = (Folder) object;
		return folder;
	}
	
	/** Renvoi un customFile a partir d'un document cmis
	 * @param doc
	 * @return le fichier
	 */
	private FileCustom getFileFromDoc(Document doc, String fileName, String cod){
		return new FileCustom(doc.getId(), cod, fileName, doc.getContentStreamMimeType());
	}
	

	/**
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#createFileFromUpload(fr.univlorraine.ecandidat.utils.ByteArrayInOutStream, java.lang.String, java.lang.String, long, java.lang.String, java.lang.String)
	 */
	@Override
	public FileCustom createFileFromUpload(ByteArrayInOutStream file,
			String mimeType, String filename, long length, String typeFichier, String prefixe) throws FileException{
		try{
			String name = prefixe+"_"+filename;
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			properties.put(PropertyIds.NAME, name);

			ContentStream contentStream = new ContentStreamImpl(name, BigInteger.valueOf(length), mimeType, file.getInputStream());
			Folder master;
			if (typeFichier.equals(ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE)){
				master = getFolderGestionnaire();
			}else{
				master = getFolderCandidat();
			}
			Document d = master.createDocument(properties, contentStream, VersioningState.MAJOR);
			file.close();
			return getFileFromDoc(d,filename, prefixe);
		}catch(Exception e){
			logger.error("Stockage de fichier - CMIS : erreur de creation du fichier ",e);
			throw new FileException(applicationContext.getMessage("file.error.create", null, UI.getCurrent().getLocale()),e);
		}		
	}

	/** Supprime un fichier par son id
	 * @param id
	 */
	public void deleteFileById(String id) {
		FileUtils.delete(id, getCmisSession());
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#deleteFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier)
	 */
	@Override
	public void deleteFile(Fichier fichier) throws FileException{		
		try{
			Document doc = (Document)getObjectById(fichier.getFileFichier());
			deleteFileById(doc.getId());
		}catch(Exception e){
			logger.error("Stockage de fichier - CMIS : erreur de suppression du fichier ",e);
			throw new FileException(applicationContext.getMessage("file.error.delete", null, UI.getCurrent().getLocale()),e);
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#getInputStreamFromFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier)
	 */
	@Override
	public InputStream getInputStreamFromFile(Fichier file) throws FileException{
		try{
			Document doc = (Document)getObjectById(file.getFileFichier());		
			return doc.getContentStream().getStream();
		}catch(Exception e){
			logger.error("Stockage de fichier - CMIS : erreur de recuperation du fichier ",e);
			throw new FileException(applicationContext.getMessage("file.error.stream", null, UI.getCurrent().getLocale()),e);
		}	
	}

}
