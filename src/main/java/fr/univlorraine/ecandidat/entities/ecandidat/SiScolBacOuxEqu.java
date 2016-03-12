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
 * The persistent class for the siScol_bac_oux_equ database table.
 * 
 */
@Entity
@Table(name="siscol_bac_oux_equ")
@Data @EqualsAndHashCode(of="codBac")
public class SiScolBacOuxEqu implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_bac", nullable=false, length=4)
	@Size(max =4) 
	@NotNull
	private String codBac;

	@Column(name="lib_bac", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libBac;

	@Column(name="lic_bac", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licBac;

	@Column(name="tem_en_sve_bac")
	private Boolean temEnSveBac;

	@Column(name="tem_nat_bac", nullable=false)
	@NotNull
	private Boolean temNatBac;
	
	@Column(name="daa_deb_vld_bac", nullable=true, length=4)
	@Size(max = 4) 
	private String daaDebVldBac;
	
	@Column(name="daa_fin_vld_bac", nullable=true, length=4)
	@Size(max = 4) 
	private String daaFinVldBac;

	//bi-directional many-to-one association to CandidatBacOuEqu
	@OneToMany(mappedBy="siScolBacOuxEqu")
	private List<CandidatBacOuEqu> candidatBacOuEqus;
	
	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.libBac;
	}
	
	public SiScolBacOuxEqu() {
		super();
	}

	public SiScolBacOuxEqu(String codBac, String libBac, String licBac,
			Boolean temEnSveBac, Boolean temNatBac, String daaDebVldBac, String daaFinVldBac) {
		super();
		this.codBac = codBac;
		this.libBac = libBac;
		this.licBac = licBac;
		this.temEnSveBac = temEnSveBac;
		this.temNatBac = temNatBac;
		this.daaDebVldBac = daaDebVldBac;
		this.daaFinVldBac = daaFinVldBac;
	}
}