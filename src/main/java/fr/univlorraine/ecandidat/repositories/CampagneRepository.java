package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;

@Repository
public interface CampagneRepository extends JpaRepository<Campagne, Integer> {

	List<Campagne> findByTesCamp(Boolean enService);

	List<Campagne> findByTesCampAndDatArchivCampIsNull(Boolean enService);

	Campagne findByCodCamp(String cod);

	List<Campagne> findByDatActivatPrevCampIsNotNullAndDatActivatEffecCampIsNull();

	List<Campagne> findByDatActivatEffecCampIsNullAndDatActivatPrevCampIsNotNull();
}
