package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the candidat_bac_ou_equ database table.
 * 
 */
@Entity
@Table(name="candidat_bac_ou_equ")
@Data @EqualsAndHashCode(of="idCandidat")
@ToString(exclude={"candidat"})
public class CandidatBacOuEqu implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id_candidat", nullable=false)
	private Integer idCandidat;

	@Column(name="annee_obt_bac")
	private Integer anneeObtBac;

	//bi-directional many-to-one association to SiScolBacOuxEqu
	@ManyToOne
	@JoinColumn(name="cod_bac", nullable=false)
	@NotNull
	private SiScolBacOuxEqu siScolBacOuxEqu;

	//bi-directional many-to-one association to SiScolCommune
	@ManyToOne
	@JoinColumn(name="cod_com")
	private SiScolCommune siScolCommune;

	//bi-directional many-to-one association to SiScolDepartement
	@ManyToOne
	@JoinColumn(name="cod_dep")
	private SiScolDepartement siScolDepartement;

	//bi-directional many-to-one association to SiScolEtablissement
	@ManyToOne
	@JoinColumn(name="cod_etb")
	private SiScolEtablissement siScolEtablissement;

	//bi-directional many-to-one association to SiScolMentionNivBac
	@ManyToOne
	@JoinColumn(name="cod_mnb")
	private SiScolMentionNivBac siScolMentionNivBac;

	//bi-directional many-to-one association to SiScolPays
	@ManyToOne
	@JoinColumn(name="cod_pay")
	private SiScolPays siScolPays;

	//bi-directional one-to-one association to Candidat
	@OneToOne
	@JoinColumn(name="id_candidat", nullable=false, insertable=false, updatable=false)
	@NotNull
	private Candidat candidat;
	
	@Column(name="tem_updatable_bac", nullable=false)
	@NotNull
	private Boolean temUpdatableBac;

	public CandidatBacOuEqu() {
		super();
	}

	public CandidatBacOuEqu(Integer idCandidat, Integer anneeObtBac,
			SiScolBacOuxEqu siScolBacOuxEqu, SiScolCommune siScolCommune,
			SiScolDepartement siScolDepartement,
			SiScolEtablissement siScolEtablissement,
			SiScolMentionNivBac siScolMentionNivBac, SiScolPays siScolPays,
			Candidat candidat, Boolean temUpdatableBac) {
		super();
		this.idCandidat = idCandidat;
		this.anneeObtBac = anneeObtBac;
		this.siScolBacOuxEqu = siScolBacOuxEqu;
		this.siScolCommune = siScolCommune;
		this.siScolDepartement = siScolDepartement;
		this.siScolEtablissement = siScolEtablissement;
		this.siScolMentionNivBac = siScolMentionNivBac;
		this.siScolPays = siScolPays;
		this.candidat = candidat;
		this.temUpdatableBac = temUpdatableBac;
	}
	
	
}