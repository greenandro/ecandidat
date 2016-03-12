package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.LockCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.LockCandidatPK;

@Repository
public interface LockCandidatRepository extends JpaRepository<LockCandidat, LockCandidatPK> {

	List<LockCandidat> findByInstanceIdLock(String idInstance);

	List<LockCandidat> findByUiIdLockAndInstanceIdLock(Integer uiId, String idInstance);
}
