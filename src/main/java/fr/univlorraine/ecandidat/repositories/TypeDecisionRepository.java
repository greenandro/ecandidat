package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;

@Repository
public interface TypeDecisionRepository extends JpaRepository<TypeDecision, Integer> {
	
	TypeDecision findByCodTypDec(String codTypDec);

	List<TypeDecision> findByTesTypDec(Boolean tes);

	List<TypeDecision> findByTesTypDecAndTypeAvisCodTypAvis(Boolean enService, String codTypDecFavorable);
}
