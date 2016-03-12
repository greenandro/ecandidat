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


/**
 * The persistent class for the siscol_typ_diplome database table.
 * 
 */
@Entity
@Table(name="siscol_typ_resultat")
@Data @EqualsAndHashCode(of="codTre")
public class SiScolTypResultat implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_tre", nullable=false, length=4)
	@Size(max = 4) 
	@NotNull
	private String codTre;

	@Column(name="lib_tre", nullable=false, length=50)
	@Size(max =50) 
	@NotNull
	private String libTre;

	@Column(name="lic_tre", nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String licTre;

	@Column(name="tem_en_sve_tre", nullable=false)
	@NotNull
	private Boolean temEnSveTre;

	//bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy="siScolTypResultat")
	private List<CandidatCursusInterne> candidatCursusInternes;
	
	
	public SiScolTypResultat() {
		super();
	}

	public SiScolTypResultat(String codTre, String libTre, String licTre,
			Boolean temEnSveTre) {
		super();
		this.codTre = codTre;
		this.libTre = libTre;
		this.licTre = licTre;
		this.temEnSveTre = temEnSveTre;
	}

	public SiScolTypResultat(String codTre) {
		super();
		this.codTre = codTre;
	}
}