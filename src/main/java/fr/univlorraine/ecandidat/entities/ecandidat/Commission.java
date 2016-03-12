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

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the commission database table.
 * 
 */
@Entity
@Table(name="commission")
@Data @EqualsAndHashCode(of="idComm") @EntityListeners(EntityPushEntityListener.class)
@ToString(exclude={"commissionMembres","formations"})
public class Commission implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4192238331148950163L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_comm", nullable=false)
	private Integer idComm;

	@Column(name="cod_comm", unique=true, nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String codComm;

	@Column(name="comment_retour_comm", length=500)
	@Size(max = 500) 
	private String commentRetourComm;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_cre_comm", nullable=false)
	@NotNull
	private LocalDateTime datCreComm;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_mod_comm", nullable=false)
	@NotNull
	private LocalDateTime datModComm;

	@Column(name="fax_comm", length=20)
	@Size(max = 20) 
	private String faxComm;

	@Column(name="lib_comm", nullable=false, length=200)
	@Size(max = 200) 
	@NotNull
	private String libComm;

	@Column(name="mail_comm", nullable=false, length=80)
	@Size(max = 80) 
	@NotNull
	private String mailComm;

	@Column(name="tel_comm", length=20)
	@Size(max = 20) 
	private String telComm;

	@Column(name="tes_comm", nullable=false)
	@NotNull
	private Boolean tesComm;

	@Column(name="user_cre_comm", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String userCreComm;

	@Column(name="user_mod_comm", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String userModComm;

	//bi-directional many-to-one association to Adresse
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_adr", nullable=false)
	@NotNull
	private Adresse adresse;

	//bi-directional many-to-one association to CentreCandidature
	@ManyToOne
	@JoinColumn(name="id_ctr_cand", nullable=false)
	@NotNull
	private CentreCandidature centreCandidature;

	//bi-directional many-to-one association to CommissionMembre
	@OneToMany(mappedBy="commission",cascade=CascadeType.ALL)
	private List<CommissionMembre> commissionMembres;

	//bi-directional many-to-one association to Formation
	@OneToMany(mappedBy="commission")
	private List<Formation> formations;
	
	//bi-directional many-to-many association to Gestionnaire
	@ManyToMany(mappedBy="commissions",cascade=CascadeType.MERGE)
	private List<Gestionnaire> gestionnaires;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.codComm+"/"+this.libComm;
	}
	
	@PrePersist
	private void onPrePersist() {
		this.datCreComm = LocalDateTime.now();
		this.datModComm = LocalDateTime.now();
	}
	
	@PreUpdate
	private void onPreUpdate() {
		this.datModComm = LocalDateTime.now();
	}
	
	public Commission(CentreCandidature ctrCand, String user) {
		super();
		this.centreCandidature = ctrCand;
		this.userCreComm = user;
		this.userModComm = user;
		this.tesComm = false;
		this.adresse = new Adresse();
	}

	public Commission() {
		super();
	}
}