package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the formulaire_cand database table.
 * 
 */
@Entity
@Table(name="formulaire_cand")
@Data @EqualsAndHashCode(of="id")
public class FormulaireCand implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FormulaireCandPK id;
	
	@Lob
	@Column(name="reponses_formulaire_cand", nullable=true, columnDefinition="TEXT")
	private String reponsesFormulaireCand;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_cre_formulaire_cand", nullable=false)
	@NotNull
	private LocalDateTime datCreFormulaireCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_reponse_formulaire_cand")
	private LocalDateTime datReponseFormulaireCand;

	@Column(name="user_cre_formulaire_cand", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String userCreFormulaireCand;
	
	/*@Column(name="tem_concern_formulaire_cand")
	private Boolean temConcernFormulaireCand;*/
	
	@Column(name="user_mod_formulaire_cand", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String userModFormulaireCand;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_mod_formulaire_cand", nullable=false)
	@NotNull
	private LocalDateTime datModFormulaireCand;

	//bi-directional many-to-one association to Candidature
	@ManyToOne
	@JoinColumn(name="id_cand", nullable=false, insertable=false, updatable=false)
	@NotNull
	private Candidature candidature;

	//bi-directional many-to-one association to Formulaire
	@ManyToOne
	@JoinColumn(name="id_formulaire", nullable=false, insertable=false, updatable=false)
	@NotNull
	private Formulaire formulaire;

	//bi-directional many-to-one association to TypeStatutPiece
	@ManyToOne
	@JoinColumn(name="cod_typ_statut_piece", nullable=false)
	private TypeStatutPiece typeStatutPiece;
	
	@PrePersist
	private void onPrePersist() {
		this.datCreFormulaireCand = LocalDateTime.now();
		this.datModFormulaireCand = LocalDateTime.now();
	}
	
	@PreUpdate
	private void onPreUpdate() {
		this.datModFormulaireCand = LocalDateTime.now();
	}
	
	public FormulaireCand(FormulaireCandPK id, String userCreFormulaireCand, Candidature candidature,
			Formulaire formulaire) {
		super();
		this.id = id;
		this.userCreFormulaireCand = userCreFormulaireCand;
		this.candidature = candidature;
		this.formulaire = formulaire;
	}

	public FormulaireCand() {
		super();
	}
}