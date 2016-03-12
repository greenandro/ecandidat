package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the candidat_cursus_interne database table.
 * 
 */
@Entity
@Table(name="candidat_cursus_interne")
@Data @EqualsAndHashCode(of="idCursusInterne")
@ToString(exclude={"candidat"})
public class CandidatCursusInterne implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_cursus_interne", unique=true, nullable=false)
	private Integer idCursusInterne;

	@Column(name="annee_univ_cursus_interne")
	private Integer anneeUnivCursusInterne;

	@Column(name="cod_vet_cursus_interne", length=100)
	@Size(max = 100) 
	private String codVetCursusInterne;

	@Column(name="lib_cursus_interne", length=255)
	@Size(max = 255) 
	private String libCursusInterne;
	
	//bi-directional many-to-one association to SiScolMention
	@ManyToOne
	@JoinColumn(name="cod_men_cursus_interne")
	private SiScolMention siScolMention;
	
	//bi-directional many-to-one association to SiScolMention
	@ManyToOne
	@JoinColumn(name="cod_tre_cursus_interne")
	private SiScolTypResultat siScolTypResultat;
	
	//bi-directional many-to-one association to Candidat
	@ManyToOne
	@JoinColumn(name="id_candidat", nullable=false)
	@NotNull
	private Candidat candidat;

	public CandidatCursusInterne() {
		super();
	}

	public CandidatCursusInterne(Integer anneeUnivCursusInterne,
			String codVetCursusInterne, String libCursusInterne,
			SiScolTypResultat siScolTypResultat, SiScolMention siScolMention,
			Candidat candidat) {
		super();
		this.anneeUnivCursusInterne = anneeUnivCursusInterne;
		this.codVetCursusInterne = codVetCursusInterne;
		this.libCursusInterne = libCursusInterne;
		this.siScolTypResultat = siScolTypResultat;
		this.siScolMention = siScolMention;
		this.candidat = candidat;
	}
}