package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;

@Repository
public interface CentreCandidatureRepository extends JpaRepository<CentreCandidature, Integer> {
	public CentreCandidature findByCodCtrCand(String cod);

	public List<CentreCandidature> findByTesCtrCand(Boolean enService);
}
