package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;

@Repository
public interface SiScolAnneeUniRepository extends JpaRepository<SiScolAnneeUni, String> {
	
	List<SiScolAnneeUni> findByEtaAnuIaeAndCodAnuNotIn(String typBooleanYes, List<String> notInCampagne);

	List<SiScolAnneeUni> findByEtaAnuIae(String typBooleanYes);
}
