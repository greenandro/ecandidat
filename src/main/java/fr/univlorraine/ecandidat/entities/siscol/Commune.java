package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the COMMUNE database table.
 * 
 */
@Entity
@Data @EqualsAndHashCode(of="codCom")
public class Commune implements Serializable {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = -1736371871772499701L;

	@Id
	@Column(name="COD_COM", unique=true, nullable=false, length=5)
	@Size(max = 5) 
	@NotNull
	private String codCom;

	@Column(name="LIB_COM", nullable=false, length=32)
	@Size(max = 32) 
	@NotNull
	private String libCom;

	@Column(name="TEM_EN_SVE_COM", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temEnSveCom;
	
	//bi-directional many-to-one association to Departement
	@ManyToOne
	@JoinColumn(name="COD_DEP")
	private Departement departement;
	
	//bi-directional many-to-one association to Etablissement
	@OneToMany(mappedBy="commune")
	private List<Etablissement> etablissements;
}