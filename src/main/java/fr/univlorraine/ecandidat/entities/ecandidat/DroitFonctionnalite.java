package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the droit_fonctionnalite database table.
 * 
 */
@Entity
@Table(name="droit_fonctionnalite")
@Data @EqualsAndHashCode(of="codFonc")
@ToString(exclude="droitProfilFoncs")
public class DroitFonctionnalite implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_fonc", nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String codFonc;

	@Column(name="lib_fonc", nullable=false, length=255)
	@Size(max = 255) 
	@NotNull
	private String libFonc;

	//bi-directional many-to-one association to DroitProfilFonc
	@OneToMany(mappedBy="droitFonctionnalite")
	private List<DroitProfilFonc> droitProfilFoncs;

	public DroitFonctionnalite() {
		super();
	}

	public DroitFonctionnalite(String codFonc, String libFonc) {
		super();
		this.codFonc = codFonc;
		this.libFonc = libFonc;
	}
	
}