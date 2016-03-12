package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the compte_minima database table.
 * 
 */
@Entity
@Table(name="compte_minima")
@Data @EqualsAndHashCode(of="idCptMin")
@ToString(exclude={"candidat","campagne"})
public class CompteMinima implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_cpt_min", nullable=false)
	private Integer idCptMin;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_cre_cpt_min", nullable=false)
	@NotNull
	private LocalDateTime datCreCptMin;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_fin_valid_cpt_min", nullable=false)
	@NotNull
	private LocalDateTime datFinValidCptMin;
	

	@Column(name="login_cpt_min", nullable=true, length=30)
	@Size(max = 30) 
	private String loginCptMin;
	
	@Column(name="mail_perso_cpt_min", nullable=false, length=80)
	@Size(max = 80) 
	@NotNull
	private String mailPersoCptMin;

	@Column(name="nom_cpt_min", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String nomCptMin;

	@Column(name="num_dossier_opi_cpt_min", nullable=false, length=8)
	@Size(max = 8) 
	@NotNull
	private String numDossierOpiCptMin;

	@Column(name="prenom_cpt_min", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String prenomCptMin;

	@Column(name="pwd_cpt_min", nullable=false, length=150)
	@Size(max = 150) 
	@NotNull
	private String pwdCptMin;
	
	@Column(name="supann_etu_id_cpt_min", nullable=true, length=30)
	@Size(max = 30) 
	private String supannEtuIdCptMin;
	

	@Column(name="tem_valid_cpt_min", nullable=false)
	@NotNull
	private Boolean temValidCptMin;
	
	@Column(name="tem_valid_mail_cpt_min", nullable=false)
	@NotNull
	private Boolean temValidMailCptMin;	

	//bi-directional many-to-one association to Campagne
	@ManyToOne
	@JoinColumn(name="id_camp", nullable=false)
	@NotNull
	private Campagne campagne;
	
	//bi-directional one-to-one association to CandidatBacOuEqu
	@OneToOne(mappedBy="compteMinima", cascade = CascadeType.REMOVE)
	private Candidat candidat;
	
	@Transient
	@Size(max = 80)
	private String confirmMailPersoCptMin;
	
	/*@Column(name="test_cpt_min", nullable=true, length=50)
	@Size(max = 50) 
	private String test;*/
	
	@PrePersist
	private void onPrePersist() {
		this.datCreCptMin = LocalDateTime.now();
	}

	public CompteMinima() {
		super();
		this.temValidCptMin = false;
		this.temValidMailCptMin = false;
	}
}