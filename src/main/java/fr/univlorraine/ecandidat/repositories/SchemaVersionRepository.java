package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.SchemaVersion;

@Repository
public interface SchemaVersionRepository extends JpaRepository<SchemaVersion, String> {
	List<SchemaVersion> findFirst1BySuccessOrderByVersionRankDesc(Boolean success);
}
