package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Batch;

@Repository
public interface BatchRepository extends JpaRepository<Batch, String> {	
	List<Batch> findByTesBatch(Boolean tes);
}
