package fr.univlorraine.ecandidat.services.siscol;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

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

/**Gestion du SI Scol par défaut
 * @author Kevin Hergalant
 *
 */
@Component(value="siScolDefaultServiceImpl")
public class SiScolDefaultServiceImpl implements SiScolGenericService, Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = 9178268055903906100L;

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolBacOuxEqu()
	 */
	@Override
	public List<SiScolBacOuxEqu> getListSiScolBacOuxEqu()
			throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCentreGestion()
	 */
	@Override
	public List<SiScolCentreGestion> getListSiScolCentreGestion()
			throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCommune()
	 */
	@Override
	public List<SiScolCommune> getListSiScolCommune() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDepartement()
	 */
	@Override
	public List<SiScolDepartement> getListSiScolDepartement()
			throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDipAutCur()
	 */
	@Override
	public List<SiScolDipAutCur> getListSiScolDipAutCur()
			throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolEtablissement()
	 */
	@Override
	public List<SiScolEtablissement> getListSiScolEtablissement()
			throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolMention()
	 */
	@Override
	public List<SiScolMention> getListSiScolMention() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolMentionNivBac()
	 */
	@Override
	public List<SiScolMentionNivBac> getListSiScolMentionNivBac()
			throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolPays()
	 */
	@Override
	public List<SiScolPays> getListSiScolPays() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolTypDiplome()
	 */
	@Override
	public List<SiScolTypDiplome> getListSiScolTypDiplome()
			throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolUtilisateur()
	 */
	@Override
	public List<SiScolUtilisateur> getListSiScolUtilisateur()
			throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolComBdi()
	 */
	@Override
	public List<SiScolComBdi> getListSiScolComBdi() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolAnneeUni()
	 */
	@Override
	public List<SiScolAnneeUni> getListSiScolAnneeUni() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getVersion()
	 */
	@Override
	public Version getVersion() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolTypResultat()
	 */
	@Override
	public List<SiScolTypResultat> getListSiScolTypResultat()
			throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}
}
