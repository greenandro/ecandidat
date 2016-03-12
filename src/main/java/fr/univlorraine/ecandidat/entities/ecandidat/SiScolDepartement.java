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
 * The persistent class for the siScol_departement database table.
 * 
 */
@Entity
@Table(name="siscol_departement")
@Data @EqualsAndHashCode(of="codDep")
public class SiScolDepartement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_dep", nullable=false, length=3)
	@Size(max = 3) 
	@NotNull
	private String codDep;

	@Column(name="lib_dep", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libDep;

	@Column(name="lic_dep", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licDep;

	@Column(name="tem_en_sve_dep", nullable=false)
	@NotNull
	private Boolean temEnSveDep;
	
	//bi-directional many-to-one association to Candidat
	@OneToMany(mappedBy="siScolDepartement")
	private List<Candidat> candidats;

	//bi-directional many-to-one association to ApoCommune
	@OneToMany(mappedBy="siScolDepartement")
	private List<SiScolCommune> siScolCommunes;

	//bi-directional many-to-one association to ApoEtablissement
	@OneToMany(mappedBy="siScolDepartement")
	private List<SiScolEtablissement> siScolEtablissements;

	//bi-directional many-to-one association to CandidatBacOuEqu
	@OneToMany(mappedBy="siScolDepartement")
	private List<CandidatBacOuEqu> candidatBacOuEqus;

	//bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy="siScolDepartement")
	private List<CandidatCursusPostBac> candidatCursusPostBacs;
	
	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.codDep+"/"+this.libDep;
	}
	
	public SiScolDepartement() {
		super();
	}
	
	public SiScolDepartement(String codDep) {
		super();
		this.codDep = codDep;
	}

	public SiScolDepartement(String codDep, String libDep, String licDep,
			Boolean temEnSveDep) {
		super();
		this.codDep = codDep;
		this.libDep = libDep;
		this.licDep = licDep;
		this.temEnSveDep = temEnSveDep;
	}
}