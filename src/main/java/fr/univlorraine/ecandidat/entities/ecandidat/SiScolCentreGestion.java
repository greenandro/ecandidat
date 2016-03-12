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
 * The persistent class for the siScol_centre_gestion database table.
 * 
 */
@Entity
@Table(name="siscol_centre_gestion")
@Data @EqualsAndHashCode(of="codCge")
public class SiScolCentreGestion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_cge", nullable=false, length=3)
	@Size(max = 3) 
	@NotNull
	private String codCge;

	@Column(name="lib_cge", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libCge;

	@Column(name="lic_cge", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licCge;

	@Column(name="tem_en_sve_cge", nullable=false)
	@NotNull
	private Boolean temEnSveCge;

	//bi-directional many-to-one association to ApoUtilisateur
	@OneToMany(mappedBy="siScolCentreGestion")
	private List<SiScolUtilisateur> siScolUtilisateurs;

	//bi-directional many-to-one association to Formation
	@OneToMany(mappedBy="siScolCentreGestion")
	private List<Formation> formations;

	//bi-directional many-to-one association to Formation
	@OneToMany(mappedBy="siScolCentreGestion")
	private List<Gestionnaire> gestionnaires;
	
	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.codCge+"/"+this.libCge;
	}
	
	public SiScolCentreGestion() {
		super();
	}
	
	public SiScolCentreGestion(String codCge) {
		super();
		this.codCge = codCge;
	}

	public SiScolCentreGestion(String codCge, String libCge, String licCge,
			Boolean temEnSveCge) {
		super();
		this.codCge = codCge;
		this.libCge = libCge;
		this.licCge = licCge;
		this.temEnSveCge = temEnSveCge;
	}
}