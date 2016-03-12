package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire;

@Repository
public interface GestionnaireRepository extends JpaRepository<Gestionnaire, Integer> {
	
	/** Retourne le nombre de Gestionnaire par centreCandidature
	 * @param idCtrCand
	 * @return le nombre de Gestionnaires
	 */
	@Query("select count(g) from Gestionnaire g where g.centreCandidature.idCtrCand=:idCtrCand")
	Long getNbByCtrCand(@Param("idCtrCand") Integer idCtrCand);
	
	List<Gestionnaire> findByCentreCandidatureIdCtrCand(Integer idCtrCand);

	Gestionnaire findByCentreCandidatureIdCtrCandAndDroitProfilIndIndividuLoginInd(
			Integer idCtrCand, String currentUserName);

}
