package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;


/**
 * The persistent class for the ANNEE_UNI database table.
 * 
 */
@Entity
@Table(name="siscol_annee_uni")
@Data
public class SiScolAnneeUni implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_anu", nullable=false, length=4)
	@Size(max = 4) 
	@NotNull
	private String codAnu;

	@Column(name="eta_anu_iae", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String etaAnuIae;

	@Column(name="lib_anu", nullable=false, length=40)
	@Size(max = 40) 
	@NotNull
	private String libAnu;

	@Column(name="lic_anu", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licAnu;

	public SiScolAnneeUni(String codAnu, String etaAnuIae, String libAnu,
			String licAnu) {
		super();
		this.codAnu = codAnu;
		this.etaAnuIae = etaAnuIae;
		this.libAnu = libAnu;
		this.licAnu = licAnu;
	}

	public SiScolAnneeUni() {
		super();
	}
}