package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;

@Repository
public interface DroitProfilRepository extends JpaRepository<DroitProfil, Integer> {
	
	DroitProfil findByCodProfil(String codProfil);
	
	List<DroitProfil> findByTemAdminProfil(Boolean isAdmin);
	
	List<DroitProfil> findByTemCtrCandProfil(Boolean temCtrCandProfil);
}
