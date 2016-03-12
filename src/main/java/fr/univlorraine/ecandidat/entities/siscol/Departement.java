package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the DEPARTEMENT database table.
 * 
 */
@Entity
@Data @EqualsAndHashCode(of="codDep")
public class Departement implements Serializable {

	/*** serialVersionUID */
	private static final long serialVersionUID = 4032295488701804810L;

	@Id
	@Column(name="COD_DEP", unique=true, nullable=false, length=3)
	@Size(max = 3) 
	@NotNull
	private String codDep;

	@Column(name="LIB_DEP", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libDep;

	@Column(name="LIC_DEP", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licDep;

	@Column(name="TEM_EN_SVE_DEP", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temEnSveDep;

	//bi-directional many-to-one association to Commune
	@OneToMany(mappedBy="departement")
	private List<Commune> communes;

	//bi-directional many-to-one association to Etablissement
	@OneToMany(mappedBy="departement")
	private List<Etablissement> etablissements;
}