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
@Table(name="siscol_typ_diplome")
@Data @EqualsAndHashCode(of="codTpdEtb")
public class SiScolTypDiplome implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_tpd_etb", nullable=false, length=2)
	@Size(max = 2) 
	@NotNull
	private String codTpdEtb;

	@Column(name="lib_tpd", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libTpd;

	@Column(name="lic_tpd", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licTpd;

	@Column(name="tem_en_sve_tpd", nullable=false)
	@NotNull
	private Boolean temEnSveTpd;

	//bi-directional many-to-one association to Formation
	@OneToMany(mappedBy="siScolTypDiplome")
	private List<Formation> formations;
	
	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.codTpdEtb+"/"+this.libTpd;
	}

	public SiScolTypDiplome() {
		super();
	}

	public SiScolTypDiplome(String codTpdEtb, String libTpd, String licTpd,
			Boolean temEnSveTpd) {
		super();
		this.codTpdEtb = codTpdEtb;
		this.libTpd = libTpd;
		this.licTpd = licTpd;
		this.temEnSveTpd = temEnSveTpd;
	}

	public SiScolTypDiplome(String codTpdEtb) {
		super();
		this.codTpdEtb = codTpdEtb;
	}
}