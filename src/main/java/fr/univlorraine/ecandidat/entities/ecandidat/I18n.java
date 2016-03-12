package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.eclipse.persistence.annotations.CascadeOnDelete;


/**
 * The persistent class for the i18n database table.
 * 
 */
@Entity
@Table(name="i18n")
@Data @EqualsAndHashCode(of="idI18n")
@ToString(exclude="i18nTraductions")
@CascadeOnDelete
public class I18n implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_i18n", nullable=false)
	private Integer idI18n;
	
	//bi-directional many-to-one association to TypeTraduction
	@ManyToOne
	@JoinColumn(name="cod_typ_trad", nullable=false)
	@NotNull
	private TypeTraduction typeTraduction;

	//bi-directional many-to-one association to I18nTraduction
	@OneToMany(mappedBy="i18n",orphanRemoval=true,cascade = CascadeType.ALL)
	private List<I18nTraduction> i18nTraductions;

	//bi-directional many-to-one association to Mail
	@OneToMany(mappedBy="i18nCorpsMail")
	private List<Mail> mailsCorpsMail;

	//bi-directional many-to-one association to Mail
	@OneToMany(mappedBy="i18nSujetMail")
	private List<Mail> mailsSujetMail;
	
	//bi-directional many-to-one association to Mail
	@OneToMany(mappedBy="i18nUrlFormulaire")
	private List<Formulaire> urlFormulaire;

	//bi-directional many-to-one association to Mail
	@OneToMany(mappedBy="i18nLibFormulaire")
	private List<Formulaire> libFormulaire;

	//bi-directional many-to-one association to MotivationAvis
	@OneToMany(mappedBy="i18nLibMotiv")
	private List<MotivationAvis> motivationAvis;

	//bi-directional many-to-one association to PieceJustif
	@OneToMany(mappedBy="i18nLibPj")
	private List<PieceJustif> pieceJustifsLibPj;

	//bi-directional many-to-one association to TypeDecision
	@OneToMany(mappedBy="i18nLibTypDec")
	private List<TypeDecision> typeDecisions;
	
	//bi-directional many-to-one association to TypeTraitement
	@OneToMany(mappedBy="i18nLibTypTrait")
	private List<TypeTraitement> typeTraitements;
	
	//bi-directional many-to-one association to TypeDecision
	@OneToMany(mappedBy="i18nLibTypStatut")
	private List<TypeStatut> typeStatuts;
	
	//bi-directional many-to-one association to TypeDecision
	@OneToMany(mappedBy="i18nLibTypStatutPiece")
	private List<TypeStatutPiece> typeStatutPieces;

	public I18n() {
		super();
	}

	public I18n(TypeTraduction typeTraduction) {
		super();
		this.typeTraduction = typeTraduction;
		this.i18nTraductions = new ArrayList<I18nTraduction>();
	}
}