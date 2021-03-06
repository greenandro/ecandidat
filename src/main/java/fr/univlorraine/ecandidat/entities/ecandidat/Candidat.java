package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.LocalDatePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the candidat database table.
 * 
 */
@Entity
@Table(name="candidat")
@Data @EqualsAndHashCode(of="idCandidat")
@ToString(exclude={"compteMinima","candidatures"})
public class Candidat implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_candidat", nullable=false)
	private Integer idCandidat;

	@Column(name="autre_pren_candidat", length=50)
	@Size(max = 50) 
	private String autrePrenCandidat;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_naiss_candidat", nullable=false)
	@NotNull
	private LocalDate datNaissCandidat;

	@Column(name="ine_candidat", length=10)
	@Size(min = 10, max = 10) 
	private String ineCandidat;
	
	@Column(name="cle_ine_candidat", length=1)
	@Size(min = 1, max = 1) 
	private String cleIneCandidat;

	@Column(name="lib_ville_naiss_candidat", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String libVilleNaissCandidat;

	@Column(name="nom_pat_candidat", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String nomPatCandidat;

	@Column(name="nom_usu_candidat", length=50)
	@Size(max = 50) 
	private String nomUsuCandidat;

	@Column(name="prenom_candidat", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String prenomCandidat;

	@Column(name="tel_candidat", length=20)
	@Size(max = 20) 
	private String telCandidat;

	@Column(name="tel_port_candidat", length=20)
	@Size(max = 20) 
	private String telPortCandidat;

	//bi-directional many-to-one association to Civilite
	@ManyToOne
	@NotNull
	@JoinColumn(name="cod_civ", nullable=false)
	private Civilite civilite;
	
	//bi-directional many-to-one association to Adresse
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_adr")
	private Adresse adresse;

	//bi-directional many-to-one association to SiScolPays
	@ManyToOne
	@JoinColumn(name="cod_pay_naiss", nullable=false)
	@NotNull
	private SiScolPays siScolPaysNaiss;
	
	//bi-directional many-to-one association to SiScolDepartement
	@ManyToOne
	@JoinColumn(name="cod_dep_naiss_candidat", nullable=true)
	private SiScolDepartement siScolDepartement;

	//bi-directional many-to-one association to SiScolPays
	@ManyToOne
	@JoinColumn(name="cod_pay_nat", nullable=false)
	@NotNull
	private SiScolPays siScolPaysNat;

	//bi-directional many-to-one association to Langue
	@ManyToOne
	@JoinColumn(name="cod_langue", nullable=false)
	@NotNull
	private Langue langue;
	
	//bi-directional one-to-one association to Candidat
	@OneToOne
	@JoinColumn(name="id_cpt_min", nullable=false)
	@NotNull
	private CompteMinima compteMinima;

	//bi-directional one-to-one association to CandidatBacOuEqu
	@OneToOne(mappedBy="candidat", cascade = CascadeType.REMOVE)
	private CandidatBacOuEqu candidatBacOuEqu;

	//bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy="candidat", cascade = CascadeType.REMOVE)
	private List<CandidatCursusPostBac> candidatCursusPostBacs;
	
	//bi-directional many-to-one association to CandidatCursusInterne
	@OneToMany(mappedBy="candidat", cascade = CascadeType.REMOVE)
	private List<CandidatCursusInterne> candidatCursusInternes;

	//bi-directional many-to-one association to CandidatCursusPro
	@OneToMany(mappedBy="candidat", cascade = CascadeType.REMOVE)
	private List<CandidatCursusPro> candidatCursusPros;
	
	//bi-directional many-to-one association to CandidatCursusPro
	@OneToMany(mappedBy="candidat", cascade = CascadeType.REMOVE)
	private List<CandidatStage> candidatStage;

	//bi-directional many-to-one association to Candidature
	@OneToMany(mappedBy="candidat", cascade = CascadeType.REMOVE)
	private List<Candidature> candidatures;
	
	@Column(name="tem_updatable_candidat", nullable=false)
	@NotNull
	private Boolean temUpdatableCandidat;
	
	@Transient
	private String numDossierOpiCandidat;
	
	@Transient
	private String adresseCandididatStr;
	
	@Transient
	private String lastEtab;
	
	@Transient
	private String lastFormation;
	
	public Candidat() {
		super();
	}
	
	public Candidat(CompteMinima cptMin, Langue langue) {
		this.compteMinima = cptMin;
		this.nomPatCandidat = cptMin.getNomCptMin();
		this.prenomCandidat = cptMin.getPrenomCptMin();
		this.langue = langue;
		this.temUpdatableCandidat = true;
	}

	/** Ajoute un cursus
	 * @param e
	 */
	public void addCursusPostBac(CandidatCursusPostBac e) {
		if (getCandidatCursusPostBacs().contains(e)){
			getCandidatCursusPostBacs().remove(e);
		}
		getCandidatCursusPostBacs().add(e);
	}

	/** Ajoute un cursus pro
	 * @param e
	 */
	public void addCursusPro(CandidatCursusPro e) {
		if (getCandidatCursusPros().contains(e)){
			getCandidatCursusPros().remove(e);
		}
		getCandidatCursusPros().add(e);
	}

	/** Ajoute un stage
	 * @param e
	 */
	public void addStage(CandidatStage e) {
		if (getCandidatStage().contains(e)){
			getCandidatStage().remove(e);
		}
		getCandidatStage().add(e);
	}
}