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


/**
 * The persistent class for the type_avis database table.
 * 
 */
@Entity
@Table(name="type_avis")
@Data @EqualsAndHashCode(of="codTypAvis")
public class TypeAvis implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_typ_avis", nullable=false, length=2)
	@Size(max = 2) 
	@NotNull
	private String codTypAvis;

	@Column(name="libelle_typ_avis", nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String libelleTypAvis;

	//bi-directional many-to-one association to Mail
	@OneToMany(mappedBy="typeAvis")
	private List<Mail> mails;

	//bi-directional many-to-one association to TypeDecision
	@OneToMany(mappedBy="typeAvis")
	private List<TypeDecision> typeDecisions;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.codTypAvis+"/"+this.libelleTypAvis;
	}

	
	public TypeAvis(String codTypAvis, String libelleTypAvis) {
		super();
		this.codTypAvis = codTypAvis;
		this.libelleTypAvis = libelleTypAvis;
	}
	
	public TypeAvis(String codTypAvis) {
		super();
		this.codTypAvis = codTypAvis;
	}

	public TypeAvis() {
		super();
	}
	
	
}