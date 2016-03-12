package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the droit_profil_fonc database table.
 * 
 */
@Data @EqualsAndHashCode(of={"idProfil","codFonc"})
@Embeddable
@ToString(of={"idProfil","codFonc"})
public class DroitProfilFoncPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="id_profil", nullable=false)
	@NotNull
	private Integer idProfil;

	@Column(name="cod_fonc", nullable=false)
	@NotNull
	private String codFonc;

	public DroitProfilFoncPK() {
	}
	
	public DroitProfilFoncPK(Integer idProfil, String codFonc) {
		super();
		this.idProfil = idProfil;
		this.codFonc = codFonc;
	}
}