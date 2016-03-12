package fr.univlorraine.ecandidat.services.siscol;

import java.util.List;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
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
import fr.univlorraine.ecandidat.entities.siscol.Vet;

/** Interface d'acces aux données du SI Scol
 * @author Kevin Hergalant
 *
 */
public interface SiScolGenericService {
	
	/**
	 * @return true si on il s'agit de l'implémentation apogee
	 */
	default Boolean isImplementationApogee(){
		return false;		
	}
	
	/**
	 * @return la liste des BacOuxEqu
	 * @throws SiScolException 
	 */
	List<SiScolBacOuxEqu> getListSiScolBacOuxEqu() throws SiScolException;
	
	/**
	 * @return la liste des CentreGestion
	 */
	List<SiScolCentreGestion> getListSiScolCentreGestion() throws SiScolException;
	
	/**
	 * @return la liste des Commune
	 */
	List<SiScolCommune> getListSiScolCommune() throws SiScolException;
	
	/**
	 * @return la liste des Departements
	 */
	List<SiScolDepartement> getListSiScolDepartement() throws SiScolException;
	
	/**
	 * @return la liste des DipAutCur
	 */
	List<SiScolDipAutCur> getListSiScolDipAutCur() throws SiScolException;
	
	/**
	 * @return la liste des Etablissement
	 */
	List<SiScolEtablissement> getListSiScolEtablissement() throws SiScolException;
	
	/**
	 * @return la liste des Mention
	 */
	List<SiScolMention> getListSiScolMention() throws SiScolException;
	
	/**
	 * @return la liste des TypResultat
	 */
	List<SiScolTypResultat> getListSiScolTypResultat() throws SiScolException;
	
	/**
	 * @return la liste des MentionNivBac
	 */
	List<SiScolMentionNivBac> getListSiScolMentionNivBac() throws SiScolException;
	
	/**
	 * @return la liste des Pays
	 */
	List<SiScolPays> getListSiScolPays() throws SiScolException;
	
	/**
	 * @return la liste des TypDiplome
	 */
	List<SiScolTypDiplome> getListSiScolTypDiplome() throws SiScolException;
	
	/**
	 * @return la liste des Utilisateurs
	 */
	List<SiScolUtilisateur> getListSiScolUtilisateur() throws SiScolException;
	
	/**
	 * @return la liste des ComBdi
	 */
	List<SiScolComBdi> getListSiScolComBdi() throws SiScolException;
	
	/**
	 * @return la liste des AnneeUni
	 */
	List<SiScolAnneeUni> getListSiScolAnneeUni() throws SiScolException;
	
	/**
	 * @return la version du SI Scol
	 */
	Version getVersion() throws SiScolException;

	/** Renvoi la liste des formations apogée pour un utilisateur
	 * @param codCgeUser
	 * @param search
	 * @return la liste des formations
	 * @throws SiScolException
	 */
	default List<Vet> getListFormation(String codCgeUser, String search) throws SiScolException{
		return null;
	}

	/**
	 * @param codEtu
	 * @param ine
	 * @param cleIne
	 * @return un individu Apogee
	 * @throws SiScolException
	 */
	default WSIndividu getIndividu(String codEtu, String ine, String cleIne) throws SiScolException{
		return null;
	}
	
	/** Renvoie l'adresse Apogee d'un individu
	 * @param codEtu
	 * @return l'adresse Apogee d'un individu
	 * @throws SiScolException
	 */
	/*default AdresseSiScol getAdresse(String codEtu) throws SiScolException{
		return null;
	}*/

	/** Renvoie le bac Apogée d'un individu
	 * @param codEtu
	 * @return le bac Apogée d'un individu
	 * @throws SiScolException
	 */
	/*default IndBac getBac(String codEtu) throws SiScolException{
		return null;
	}*/
	
	/** Renvoie le cursus interne
	 * @param codEtu
	 * @return le cursus interne Apogée d'un individu
	 * @throws SiScolException
	 */
	/*default List<CursusInterne> getCursusInterne(String codEtu) throws SiScolException{
		return null;
	}*/
	
	/**
	 * Creation OPI par WS
	 */
	default void creerOpiViaWS(Candidat candidat, String user){
	}
}
