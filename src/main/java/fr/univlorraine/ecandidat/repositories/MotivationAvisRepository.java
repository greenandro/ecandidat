package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;

@Repository
public interface MotivationAvisRepository extends JpaRepository<MotivationAvis, Integer> {

	public MotivationAvis findByCodMotiv(String cod);

	public List<MotivationAvis> findByTesMotiv(Boolean b);
}
