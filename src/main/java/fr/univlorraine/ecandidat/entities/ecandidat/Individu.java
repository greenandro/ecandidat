package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.univlorraine.ecandidat.services.ldap.PeopleLdap;


/**
 * The persistent class for the individu database table.
 * 
 */
@Entity
@Table(name="individu")
@Data @EqualsAndHashCode(of="loginInd")
public class Individu implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="login_ind", nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String loginInd;

	@Column(name="libelle_ind", nullable=false, length=255)
	@NotNull
	@Size(max = 255)
	private String libelleInd;	

	@Column(name="mail_ind", length=255)
	@Size(max = 255)
	private String mailInd;

	//bi-directional many-to-one association to DroitProfilInd
	@OneToMany(mappedBy="individu")
	private List<DroitProfilInd> droitProfilInds;

	public Individu() {
	}

	public Individu(String loginInd, String libelleInd,	String mailInd) {
		super();
		this.loginInd = loginInd;
		this.libelleInd = libelleInd;
		this.mailInd = mailInd;
	}

	public Individu(PeopleLdap people) {
		this(people.getUid(),people.getDisplayName(),people.getMail());
	}
	
}