package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Opi;

@Repository
public interface OpiRepository extends JpaRepository<Opi, Integer> {

	List<Opi> findByCandidatureCandidatIdCandidat(Integer idCandidat);

	List<Opi> findByDatPassageOpiIsNull();
	
}
