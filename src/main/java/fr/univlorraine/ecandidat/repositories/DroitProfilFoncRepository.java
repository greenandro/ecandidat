package fr.univlorraine.ecandidat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFoncPK;

@Repository
public interface DroitProfilFoncRepository extends JpaRepository<DroitProfilFonc, DroitProfilFoncPK> {
	
}
