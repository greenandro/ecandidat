package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.domain.Persistable;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the lock_candidat database table.
 * 
 */
@Entity @Table(name="lock_candidat")
@Data @EqualsAndHashCode(of="id")
public class LockCandidat implements Serializable, Persistable<LockCandidatPK> {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LockCandidatPK id;

	@Column(name="instance_id_lock", nullable=false, length=20)
	@Size(max = 20)
	@NotNull
	private String instanceIdLock;
	
	@Column(name="ui_id_lock", nullable=false)
	@NotNull
	private Integer uiIdLock;

	@Override
	public boolean isNew() {
		return true;
	}

	public LockCandidat(LockCandidatPK id, String instanceIdLock, Integer uiIdLock) {
		super();
		this.id = id;
		this.instanceIdLock = instanceIdLock;
		this.uiIdLock = uiIdLock;
	}

	public LockCandidat() {
		super();
	}
}