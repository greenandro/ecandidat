package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the pj_cand database table.
 * 
 */
@Data @EqualsAndHashCode(of={"idPj","idCand"})
@Embeddable
@ToString(of={"idPj","idCand"})
public class PjCandPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="id_pj", nullable=false)
	@NotNull
	private Integer idPj;

	@Column(name="id_cand", nullable=false)
	@NotNull
	private Integer idCand;

	public PjCandPK() {
	}
	public PjCandPK(Integer idPj, Integer idCand) {
		super();
		this.idPj = idPj;
		this.idCand = idCand;
	}
}