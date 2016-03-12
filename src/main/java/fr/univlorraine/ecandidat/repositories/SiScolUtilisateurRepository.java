package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolUtilisateur;

@Repository
public interface SiScolUtilisateurRepository extends JpaRepository<SiScolUtilisateur, String> {

	List<SiScolUtilisateur> findByCodUtiAndTemEnSveUtiAndSiScolCentreGestionIsNotNull(String userName, Boolean enService);

}
