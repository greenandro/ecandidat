package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the siScol_etablissement database table.
 * 
 */
@Entity
@Table(name="siscol_etablissement")
@Data @EqualsAndHashCode(of="codEtb")
public class SiScolEtablissement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_etb", nullable=false, length=8)
	@Size(max = 8) 
	@NotNull
	private String codEtb;

	@Column(name="lib_etb", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libEtb;

	@Column(name="lib_web_etb", length=120)
	@Size(max = 120) 
	private String libWebEtb;

	@Column(name="lic_etb", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licEtb;

	@Column(name="tem_en_sve_etb", nullable=false)
	@NotNull
	private Boolean temEnSveEtb;

	//bi-directional many-to-one association to ApoDepartement
	@ManyToOne
	@JoinColumn(name="cod_dep", nullable=false)
	@NotNull
	private SiScolDepartement siScolDepartement;
	
	//bi-directional many-to-one association to SiScolCommune
	@ManyToOne
	@JoinColumn(name="cod_com")
	private SiScolCommune siScolCommune;

	//bi-directional many-to-one association to CandidatBacOuEqu
	@OneToMany(mappedBy="siScolEtablissement")
	private List<CandidatBacOuEqu> candidatBacOuEqus;

	//bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy="siScolEtablissement")
	private List<CandidatCursusPostBac> candidatCursusPostBacs;

	
	public SiScolEtablissement() {
		super();
	}

	public SiScolEtablissement(String codEtb, String libEtb, String libWebEtb,
			String licEtb, Boolean temEnSveEtb) {
		super();
		this.codEtb = codEtb;
		this.libEtb = libEtb;
		this.libWebEtb = libWebEtb;
		this.licEtb = licEtb;
		this.temEnSveEtb = temEnSveEtb;
	}
	
	
}