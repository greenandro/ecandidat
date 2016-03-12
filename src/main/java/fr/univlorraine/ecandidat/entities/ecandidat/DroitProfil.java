package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
 * The persistent class for the droit_profil database table.
 * 
 */
@Entity @EntityListeners(EntityPushEntityListener.class)
@Table(name="droit_profil")
@Data @EqualsAndHashCode(of="idProfil")
@ToString(exclude="droitProfilFoncs")
public class DroitProfil implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_profil", nullable=false)
	private Integer idProfil;

	@Column(name="cod_profil", unique=true, nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String codProfil;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_cre_profil", nullable=false)
	@NotNull
	private LocalDateTime datCreProfil;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_mod_profil", nullable=false)
	@NotNull
	private LocalDateTime datModProfil;

	@Column(name="lib_profil", nullable=false, length=255)
	@Size(max = 255) 
	@NotNull
	private String libProfil;

	@Column(name="user_cre_profil", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String userCreProfil;

	@Column(name="user_mod_profil", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String userModProfil;
	
	@Column(name="tem_admin_profil", nullable=false)
	@NotNull
	private Boolean temAdminProfil;
	
	@Column(name="tem_ctr_cand_profil", nullable=false)
	@NotNull
	private Boolean temCtrCandProfil;
	
	@Column(name="tem_updatable", nullable=false)
	@NotNull
	private Boolean temUpdatable;	
	
	@Column(name="tes_profil", nullable=false)
	@NotNull
	private Boolean tesProfil;

	//bi-directional many-to-one association to DroitProfilFonc
	@OneToMany(mappedBy="droitProfil", orphanRemoval=true,cascade = CascadeType.ALL)	
	private List<DroitProfilFonc> droitProfilFoncs;

	//bi-directional many-to-one association to DroitProfilInd
	@OneToMany(mappedBy="droitProfil")
	private List<DroitProfilInd> droitProfilInds;

	@PrePersist
	private void onPrePersist() {
		this.datCreProfil = LocalDateTime.now();
		this.datModProfil = LocalDateTime.now();
	}
	@PreUpdate
	private void onPreUpdate() {
		this.datModProfil = LocalDateTime.now();
	}
	public DroitProfil() {
		super();
		this.temAdminProfil = false;
		this.temUpdatable = true;
		this.tesProfil = true;
	}
	
	public DroitProfil(String user, Boolean temCtrCandProfil) {
		this();
		this.userCreProfil = user;
		this.userModProfil = user;
		this.temCtrCandProfil = temCtrCandProfil;
	}
	
	public DroitProfil(String codProfil, String libProfil,
			String userCreProfil, String userModProfil, Boolean tesAdminProfil, Boolean temCtrCandProfil, Boolean temUpdatable, Boolean tesProfil) {
		super();
		this.codProfil = codProfil;
		this.libProfil = libProfil;
		this.userCreProfil = userCreProfil;
		this.userModProfil = userModProfil;
		this.temAdminProfil = tesAdminProfil;
		this.temCtrCandProfil = temCtrCandProfil;
		this.temUpdatable = temUpdatable;
		this.tesProfil = tesProfil;
	}
	
	/** Ajoute une fonctionnalité à la liste
	 * @param droitProfilFonc
	 */
	public void addFonctionnalite(DroitProfilFonc droitProfilFonc) {
		if (new ArrayList<DroitProfilFonc>(this.droitProfilFoncs).stream().filter(s -> s.getId().equals(droitProfilFonc.getId())).findFirst()!=null){
			droitProfilFoncs.add(droitProfilFonc);
		}
	}
	
}