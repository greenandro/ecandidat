package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;

@Repository
public interface FormulaireRepository extends JpaRepository<Formulaire, Integer> {

	List<Formulaire> findByCentreCandidatureIdCtrCand(Integer idCtrCand);

	List<Formulaire> findByCentreCandidatureIdCtrCandAndTesFormulaireAndTemCommunFormulaire(Integer idCtrCand, Boolean enService, Boolean commun);

	List<Formulaire> findByCodFormulaireOrIdFormulaireLimesurvey(String cod, Integer idFormulaireLimesurvey);

	
}
