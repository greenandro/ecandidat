package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Integer> {

	List<Candidat> findByIneCandidatAndCleIneCandidat(String ineValue, String cleIneValue);
}
