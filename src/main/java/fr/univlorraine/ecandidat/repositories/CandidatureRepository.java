package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Integer> {
	
	List<Candidature> findByFormationIdFormAndCandidatIdCandidatAndDatAnnulCandIsNull(Integer idForm, Integer idCandidat);

	List<Candidature> findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNull(
			Integer idComm, String codCamp);

	List<Candidature> findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNotNull(
			Integer idComm, String codCamp);
	
	List<Candidature> findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneDatArchivCampIsNotNull(Integer idComm);
	
	@Query("select count(c) from Candidature c where c.formation.commission.centreCandidature.idCtrCand=:idCtrCand and c.datAnnulCand is null and c.candidat.idCandidat=:idCandidat")
	Long getNbCandByCtrCand(@Param("idCtrCand") Integer idCtrCand, @Param("idCandidat") Integer idCandidat);
	
	@Query("select count(c) from Candidature c where c.datAnnulCand is null and c.candidat.idCandidat=:idCandidat")
	Long getNbCandByEtab(@Param("idCandidat") Integer idCandidat);
	
}
