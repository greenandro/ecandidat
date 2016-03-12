package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Langue;

@Repository
public interface LangueRepository extends JpaRepository<Langue, String> {

	public Langue findByTemDefautLangue(Boolean def);

	List<Langue> findByTemDefautLangueAndTesLangue(Boolean def, Boolean active);
}
