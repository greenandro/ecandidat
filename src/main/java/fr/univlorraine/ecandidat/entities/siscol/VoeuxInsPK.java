package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the VOEUX_INS database table.
 * 
 */
@ToString(of={"codIndOpi","codCge","codEtp","codVrsVet"})
@Embeddable
@Data @EqualsAndHashCode(of={"codIndOpi","codCge","codEtp","codVrsVet"})
public class VoeuxInsPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="COD_IND_OPI",nullable=false, precision=8)
	private long codIndOpi;

	@Column(name="COD_CGE", nullable=false, length=3)
	private String codCge;

	@Column(name="COD_ETP", nullable=false, length=6)
	private String codEtp;

	@Column(name="COD_VRS_VET", nullable=false, precision=3)
	private long codVrsVet;
}