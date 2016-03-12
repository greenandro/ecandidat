package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Formation;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Integer> {

	List<Formation> findByCommissionCentreCandidatureIdCtrCand(Integer idCtrCand);
	
	List<Formation> findByCommissionCentreCandidatureIdCtrCandAndSiScolCentreGestionCodCge(Integer idCtrCand, String codCGE);

	Formation findByCodForm(String cod);

	
}
