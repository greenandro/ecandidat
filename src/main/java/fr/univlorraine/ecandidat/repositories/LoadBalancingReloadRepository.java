package fr.univlorraine.ecandidat.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.LoadBalancingReload;

@Repository
public interface LoadBalancingReloadRepository extends JpaRepository<LoadBalancingReload, String> {
	//List<LoadBalancingReload> findByDatCreLbReloadGreaterThan(LocalDateTime datLastCheckLbReloadRun);

	List<LoadBalancingReload> findByDatCreLbReloadAfterOrDatCreLbReload(LocalDateTime datLastCheckLbReloadRun, LocalDateTime datLastCheckLbReloadRun2);
}
