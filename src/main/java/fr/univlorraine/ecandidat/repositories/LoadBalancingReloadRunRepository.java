package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.LoadBalancingReloadRun;
import fr.univlorraine.ecandidat.entities.ecandidat.LoadBalancingReloadRunPK;

@Repository
public interface LoadBalancingReloadRunRepository extends JpaRepository<LoadBalancingReloadRun, LoadBalancingReloadRunPK> {
	
	List<LoadBalancingReloadRun> findByIdInstanceIdLbReloadRun(String instanceId);
	
}
