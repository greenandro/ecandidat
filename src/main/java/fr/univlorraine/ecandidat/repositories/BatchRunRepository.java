package fr.univlorraine.ecandidat.repositories;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.BatchRun;

@Repository
public interface BatchRunRepository extends JpaRepository<BatchRun, LocalDateTime> {
}
