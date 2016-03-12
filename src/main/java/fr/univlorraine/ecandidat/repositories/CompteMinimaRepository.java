package fr.univlorraine.ecandidat.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;

@Repository
public interface CompteMinimaRepository extends JpaRepository<CompteMinima, Integer> {

	CompteMinima findByNumDossierOpiCptMin(String numDossier);

	CompteMinima findByMailPersoCptMin(String eMail);

	CompteMinima findByLoginCptMin(String username);

	List<CompteMinima> findByTemValidCptMinAndDatFinValidCptMinBefore(Boolean enService, LocalDateTime now);	

	List<CompteMinima> findBySupannEtuIdCptMinAndIdCptMinNot(String supannEtuId, Integer idCptMin);
	
	List<CompteMinima> findByLoginCptMinAndIdCptMinNot(String login, Integer idCptMin);

	CompteMinima findByNumDossierOpiCptMinAndCampagneCodCamp(String numDossier,	String codCamp);

	CompteMinima findByLoginCptMinAndCampagneCodCamp(String username, String codCamp);

	CompteMinima findByMailPersoCptMinAndCampagneCodCamp(String eMail,String codCamp);
	
	List<CompteMinima> findByLoginCptMinLikeIgnoreCaseOrNomCptMinLikeIgnoreCaseOrNumDossierOpiCptMinLikeIgnoreCaseOrSupannEtuIdCptMinLikeIgnoreCase(String login, String nom, String noDossier, String supannEtuId);
	
	@Query("select cpt from CompteMinima cpt left outer join cpt.candidat cand "
			+ "where cpt.campagne.codCamp=:codCamp "
			+ "and ("
			+ "cpt.loginCptMin like :filter "
			+ "or cpt.nomCptMin like :filter "
			+ "or cpt.numDossierOpiCptMin like :filter "
			+ "or cpt.supannEtuIdCptMin like :filter "
			+ "or cand.nomPatCandidat like :filter "
			+ ")")
	List<CompteMinima> findByFilter(@Param("codCamp") String codCamp, @Param("filter") String filter, Pageable pageable);
	//List<CompteMinima>SupannEtuIdCptMinLikeIgnoreCase(String login, String nom, String noDossier, String supannEtuId);
}
