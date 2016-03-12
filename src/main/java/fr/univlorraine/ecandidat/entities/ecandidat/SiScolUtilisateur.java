package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the siscol_utilisateur database table.
 * 
 */
@Entity
@Table(name="siscol_utilisateur")
@Data @EqualsAndHashCode(of="codUti")
public class SiScolUtilisateur implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id_uti", nullable=false)
	@NotNull
	private Integer idUti; 
	
	@Column(name="cod_uti", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String codUti;

	@Column(name="adr_mail_uti", length=200)
	@Size(max = 200) 
	private String adrMailUti;

	@Column(name="lib_cmt_uti", length=200)
	@Size(max = 200) 
	private String libCmtUti;

	@Column(name="tem_en_sve_uti", nullable=false)
	@NotNull
	private Boolean temEnSveUti;

	//bi-directional many-to-one association to ApoCentreGestion
	@ManyToOne
	@JoinColumn(name="cod_cge")
	private SiScolCentreGestion siScolCentreGestion;

	public SiScolUtilisateur() {
		super();
	}

	public SiScolUtilisateur(String codUti, String adrMailUti,
			String libCmtUti, Boolean temEnSveUti) {
		super();
		this.codUti = codUti;
		this.adrMailUti = adrMailUti;
		this.libCmtUti = libCmtUti;
		this.temEnSveUti = temEnSveUti;
	}
	
	
}