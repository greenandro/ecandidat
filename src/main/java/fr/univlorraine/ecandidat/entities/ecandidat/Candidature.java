package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDatePersistenceConverter;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the candidature database table.
 * 
 */
@Entity @EntityListeners(EntityPushEntityListener.class)
@Table(name="candidature")
@Data @EqualsAndHashCode(of="idCand")
@ToString(exclude={"pjCands","formulaireCands","lastTypeDecision","formation","opi"})
public class Candidature implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_cand", nullable=false)
	private Integer idCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_accept_cand")
	private LocalDateTime datAcceptCand;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_annul_cand")
	private LocalDateTime datAnnulCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_cre_cand", nullable=false)
	@NotNull
	private LocalDateTime datCreCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_mod_cand", nullable=false)
	@NotNull
	private LocalDateTime datModCand;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_mod_typ_statut_cand")
	private LocalDateTime datModTypStatutCand;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_trans_dossier_cand")
	private LocalDateTime datTransDossierCand;
	
	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_recept_dossier_cand")
	private LocalDate datReceptDossierCand;
	
	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_complet_dossier_cand")
	private LocalDate datCompletDossierCand;
	
	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_incomplet_dossier_cand")
	private LocalDate datIncompletDossierCand;

	@Column(name="tem_proposition_cand", nullable=false)
	@NotNull
	private Boolean temPropositionCand;

	@Column(name="tem_valid_typ_trait_cand", nullable=false)
	@NotNull
	private Boolean temValidTypTraitCand;		
	
	@Column(name="tem_accept_cand")
	private Boolean temAcceptCand;

	@Column(name="user_accept_cand", length=20)
	@Size(max = 20) 
	private String userAcceptCand;
	
	@Column(name="user_annul_cand", length=20)
	@Size(max = 20) 
	private String userAnnulCand;

	@Column(name="user_cre_cand", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String userCreCand;

	@Column(name="user_mod_cand", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String userModCand;

	//bi-directional many-to-one association to Candidat
	@ManyToOne
	@JoinColumn(name="id_candidat", nullable=false)
	@NotNull
	private Candidat candidat;

	//bi-directional many-to-one association to Formation
	@ManyToOne
	@JoinColumn(name="id_form", nullable=false)
	@NotNull
	private Formation formation;

	//bi-directional one-to-one association to OpiAttente
	@OneToOne(mappedBy="candidature", cascade = CascadeType.REMOVE)
	private Opi opi;

	//bi-directional many-to-one association to TypeStatut
	@ManyToOne
	@JoinColumn(name="cod_typ_statut", nullable=false)
	@NotNull
	private TypeStatut typeStatut;

	//bi-directional many-to-one association to TypeTraitement
	@ManyToOne
	@JoinColumn(name="cod_typ_trait", nullable=false)
	@NotNull
	private TypeTraitement typeTraitement;

	//bi-directional many-to-one association to FormulaireCand
	@OneToMany(mappedBy="candidature", cascade = CascadeType.REMOVE)
	private List<FormulaireCand> formulaireCands;

	//bi-directional many-to-one association to PjCand
	@OneToMany(mappedBy="candidature")
	private List<PjCand> pjCands;

	//bi-directional many-to-one association to TypeDecisionCandidature
	@OneToMany(mappedBy="candidature", cascade = CascadeType.REMOVE)
	private List<TypeDecisionCandidature> typeDecisionCandidatures;
	
	@Transient
	private Boolean check;
	
	@Transient
	private TypeDecisionCandidature lastTypeDecision;
	
	@Transient
	private String datCreCandStr;
	
	@Transient
	private String datModTypStatutCandStr;
	
	@Transient
	private String datReceptDossierCandStr;
	@Transient
	private String datTransDossierCandStr;
	@Transient
	private String datCompletDossierCandStr;
	@Transient
	private String datIncompletDossierCandStr;
	
	@Transient
	private String datModPjForm;

	@PrePersist
	private void onPrePersist() {
		this.datCreCand = LocalDateTime.now();
		this.datModCand = LocalDateTime.now();
	}
	
	@PreUpdate
	private void onPreUpdate() {
		this.datModCand = LocalDateTime.now();
	}

	public Candidature(String user, Candidat candidat, Formation formation, TypeTraitement typeTraitement, TypeStatut statut, Boolean temPropositionCand, Boolean temValidTypTraitCand) {
		super();
		this.temPropositionCand = temPropositionCand;
		this.temValidTypTraitCand = temValidTypTraitCand;
		this.userCreCand = user;
		this.userModCand = user;
		this.typeTraitement = typeTraitement;
		this.candidat = candidat;
		this.typeStatut = statut;
		this.formation = formation;
	}

	public Candidature() {
		super();
	}

	/** Modifie la liste des PJ
	 * @param pjCand
	 */
	public void updatePjCand(PjCand pjCand) {
		removePjCand(pjCand);
		getPjCands().add(pjCand);
	}
	
	/** Modifie la liste des Form
	 * @param formulaireCand
	 */
	public void updateFormulaireCand(FormulaireCand formulaireCand) {
		removeFormulaireCand(formulaireCand);
		getFormulaireCands().add(formulaireCand);
	}

	/**
	 * @param pjCand
	 */
	public void removePjCand(PjCand pjCand) {
		if (getPjCands().contains(pjCand)){
			getPjCands().remove(pjCand);
		}
	}
	
	/**
	 * @param formulaireCand
	 */
	public void removeFormulaireCand(FormulaireCand formulaireCand) {
		if (getFormulaireCands().contains(formulaireCand)){
			getFormulaireCands().remove(formulaireCand);
		}
	}

	public void setTypeDecision(TypeDecisionCandidature typeDecision) {
		this.getTypeDecisionCandidatures().remove(typeDecision);
		this.getTypeDecisionCandidatures().add(typeDecision);		
	}

}