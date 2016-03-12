package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the VERSION_APO database table.
 * 
 */
@Data @EqualsAndHashCode(of={"codVer","codPatch","codPerso"})
@Embeddable
@ToString(of={"codVer","codPatch","codPerso"})
public class VersionApoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="COD_VER")
	private String codVer;

	@Column(name="COD_PATCH")
	private long codPatch;

	@Column(name="COD_PERSO")
	private long codPerso;
}