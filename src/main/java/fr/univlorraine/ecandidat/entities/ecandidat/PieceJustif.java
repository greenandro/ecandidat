package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;


/**
 * The persistent class for the piece_justif database table.
 * 
 */
@Entity @EntityListeners(EntityPushEntityListener.class)
@Table(name="piece_justif")
@Data @EqualsAndHashCode(of="idPj")
public class PieceJustif implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_pj", nullable=false)
	private Integer idPj;

	@Column(name="cod_pj", unique=true, nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String codPj;
	
	@Column(name="lib_pj", nullable=false, length=50)
	@NotNull
	@Size(max = 50)
	private String libPj;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_cre_pj", nullable=false)
	@NotNull
	private LocalDateTime datCrePj;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_mod_pj", nullable=false)
	@NotNull
	private LocalDateTime datModPj;

	@Column(name="tem_commun_pj", nullable=false)
	@NotNull
	private Boolean temCommunPj;

	@Column(name="tem_conditionnel_pj", nullable=false)
	@NotNull
	private Boolean temConditionnelPj;

	@Column(name="tes_pj", nullable=false)
	@NotNull
	private Boolean tesPj;

	@Column(name="user_cre_pj", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String userCrePj;

	@Column(name="user_mod_pj", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String userModPj;

	//bi-directional many-to-many association to Formation
	@ManyToMany(mappedBy="pieceJustifs")
	private List<Formation> formations;

	//bi-directional many-to-one association to CentreCandidature
	@ManyToOne
	@JoinColumn(name="id_ctr_cand")
	private CentreCandidature centreCandidature;

	//bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name="id_i18n_lib_pj")
	@NotNull
	private I18n i18nLibPj;

	//bi-directional many-to-one association to Fichier
	@ManyToOne
	@JoinColumn(name="id_fichier")
	private Fichier fichier;

	//bi-directional many-to-one association to PjCand
	@OneToMany(mappedBy="pieceJustif")
	private List<PjCand> pjCands;

	@PrePersist
	private void onPrePersist() {
		this.datCrePj = LocalDateTime.now();
		this.datModPj = LocalDateTime.now();
	}
	
	@PreUpdate
	private void onPreUpdate() {
		this.datModPj = LocalDateTime.now();
	}

	public PieceJustif() {
		super();
	}
	
	public PieceJustif(String currentUserName) {
		super();
		this.userCrePj = currentUserName;
		this.userModPj = currentUserName;
		this.tesPj = false;
		this.temCommunPj = false;
		this.temConditionnelPj = false;
	}

	
}