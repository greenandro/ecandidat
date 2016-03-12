package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Commission;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Integer> {
	
	Commission findByCodComm(String cod);
	
	List<Commission> findByCentreCandidatureIdCtrCand(Integer idCtrCand);

	@Query("select c from Commission c where c.centreCandidature.idCtrCand = :idCtrCand and c.idComm in :listeIdCommission")
	List<Commission> findByCentreCandidatureIdCtrCandAndIdCommIn(@Param("idCtrCand") Integer idCtrCand, @Param("listeIdCommission") List<Integer> listeIdCommission);
}
