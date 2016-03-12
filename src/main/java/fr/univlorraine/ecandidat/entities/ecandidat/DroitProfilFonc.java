package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the droit_profil_fonc database table.
 * 
 */
@Entity
@Table(name="droit_profil_fonc")
@Data @EqualsAndHashCode(of="id")
@ToString(exclude={"droitFonctionnalite","droitProfil"})
public class DroitProfilFonc implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DroitProfilFoncPK id;

	@Column(name="tem_read_only", nullable=false)
	private Boolean temReadOnly;

	//bi-directional many-to-one association to DroitFonctionnalite
	@ManyToOne
	@JoinColumn(name="cod_fonc", nullable=false, insertable=false, updatable=false)
	@NotNull
	private DroitFonctionnalite droitFonctionnalite;

	//bi-directional many-to-one association to DroitProfil
	@ManyToOne
	@JoinColumn(name="id_profil", nullable=false, insertable=false, updatable=false)
	@NotNull
	private DroitProfil droitProfil;

	public DroitProfilFonc() {
		super();
	}

	public DroitProfilFonc(DroitFonctionnalite droitFonctionnalite,
			DroitProfil droitProfil, Boolean temReadOnly) {
		super();
		this.id = new DroitProfilFoncPK(droitProfil.getIdProfil(),droitFonctionnalite.getCodFonc());
		this.droitFonctionnalite = droitFonctionnalite;
		this.droitProfil = droitProfil;
		this.temReadOnly = temReadOnly;
	}
	
	
}