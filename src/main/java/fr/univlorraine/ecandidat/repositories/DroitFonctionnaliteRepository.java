package fr.univlorraine.ecandidat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;

@Repository
public interface DroitFonctionnaliteRepository extends JpaRepository<DroitFonctionnalite, String> {
	
}
