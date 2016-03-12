package fr.univlorraine.ecandidat.config;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import fr.univlorraine.ecandidat.services.file.FileManager;
import fr.univlorraine.ecandidat.services.file.FileManagerCmisImpl;
import fr.univlorraine.ecandidat.services.file.FileManagerFileSystemImpl;

/**
 * Configuration de dematerialisation
 * 
 * @author Kevin Hergalant
 *
 */
@Configuration
public class FileConfig {

	@Resource
	private Environment environment;
	
	private Logger logger = LoggerFactory.getLogger(FileConfig.class);
	
	/**
	 * @return le fileManager de l'application
	 */
	@Bean
	public FileManager fileManager(){
		/*Variables CMIS*/
		String userCmis = environment.getProperty("file.cmis.user");
		String passwordCmis = environment.getProperty("file.cmis.pwd");
		String urlCmis = environment.getProperty("file.cmis.atompub.url");
		String repositoryCmis = environment.getProperty("file.cmis.repository");
		String folderGestionnaireCmis = environment.getProperty("file.cmis.gestionnaire.id");
		String folderCandidatCmis = environment.getProperty("file.cmis.candidat.id");
		
		/*Variables FileSystem*/
		String pathCandidatFs = environment.getProperty("file.filesystem.candidat.path");
		String pathGestFs = environment.getProperty("file.filesystem.gestionnaire.path");
		
		/*On vérifie si il n'existe pas d'incohérence dans les variables de context*/
		if (
			(isNotVarEmpty(userCmis) || isNotVarEmpty(passwordCmis) || isNotVarEmpty(urlCmis) || isNotVarEmpty(repositoryCmis) || isNotVarEmpty(folderGestionnaireCmis) || isNotVarEmpty(folderCandidatCmis))
			&&
			(isNotVarEmpty(pathCandidatFs) || isNotVarEmpty(pathGestFs))
			){
			logger.error("Stockage de fichier - Il existe des incoherences dans la definition de vos variables de dematerialisation - Mode de stockage de fichier : Aucun");
			return null;
		}
		/*Tout les parametres CMIS sont renseignés-->Implementation CMIS*/
		else if (isNotVarEmpty(userCmis) && isNotVarEmpty(passwordCmis) && isNotVarEmpty(urlCmis) && isNotVarEmpty(repositoryCmis) && isNotVarEmpty(folderGestionnaireCmis) && isNotVarEmpty(folderCandidatCmis)){
			logger.info("Stockage de fichier - Mode de stockage de fichier : CMIS");
			FileManager fm = new FileManagerCmisImpl(userCmis, passwordCmis, urlCmis, repositoryCmis, folderGestionnaireCmis, folderCandidatCmis);
			fm.testSession();
			return fm;
		}
		/*Tout les parametres FileSystem sont renseignés-->Implementation FileSystem*/
		else if (isNotVarEmpty(pathCandidatFs) && isNotVarEmpty(pathGestFs)){
			logger.info("Stockage de fichier - Mode de stockage de fichier : FileSystem");
			FileManager fm = new FileManagerFileSystemImpl(pathGestFs, pathCandidatFs);
			fm.testSession();
			return fm;
		}
		logger.info("Stockage de fichier - Mode de stockage de fichier : Aucun");
		return null;
	}
	
	/**
	 * @param var
	 * @return true si la variable est renseignee
	 */
	private Boolean isNotVarEmpty(String var){
		if (var != null && !var.equals("")){
			return true;
		}
		return false;
	}	
	
}
