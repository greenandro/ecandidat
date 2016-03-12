package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;

@Repository
public interface DroitProfilIndRepository extends JpaRepository<DroitProfilInd, Integer> {	
	public List<DroitProfilInd> findByIndividuLoginInd(String login);
	
	public List<DroitProfilInd> findByIndividuLoginIndAndDroitProfilTemAdminProfil(String login, Boolean isAdmin);
	
	public List<DroitProfilInd> findByIndividuLoginIndAndDroitProfilCodProfil(String login, String codProfil);
	
	public List<DroitProfilInd> findByIndividuLoginIndAndDroitProfilTemCtrCandProfil(String login, Boolean isCtrCandProfil);

	public List<DroitProfilInd> findByDroitProfilTemAdminProfil(Boolean b);

	public List<DroitProfilInd> findByDroitProfilCodProfil(String droitProfilScolCentrale);

	public List<DroitProfilInd> findByDroitProfilCodProfilAndGestionnaireCentreCandidatureIdCtrCandAndIndividuLoginInd(String codProfil, Integer idCtrCand, String loginInd);
	
	public List<DroitProfilInd> findByGestionnaireCentreCandidatureIdCtrCandAndIndividuLoginInd(Integer idCtrCand, String loginInd);
	
	public List<DroitProfilInd> findByCommissionMembreCommissionIdCommAndIndividuLoginInd(Integer idComm, String loginInd);
	
	public List<DroitProfilInd> findByDroitProfilCodProfilAndIndividuLoginInd(String codDroitProfil, String login);

	public List<DroitProfilInd> findByIndividuLoginIndAndCommissionMembreIsNotNull(String username);

}
