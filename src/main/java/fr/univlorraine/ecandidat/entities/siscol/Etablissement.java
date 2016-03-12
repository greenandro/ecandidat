package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the ETABLISSEMENT database table.
 * 
 */
@Entity
@Data @EqualsAndHashCode(of="codEtb")
public class Etablissement implements Serializable {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = -6573012204144939644L;

	@Id
	@Column(name="COD_ETB", unique=true, nullable=false, length=8)
	@Size(max = 8) 
	@NotNull
	private String codEtb;

	@Column(name="LIB_ETB", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libEtb;

	@Column(name="LIB_WEB_ETB", length=120)
	@Size(max = 120) 
	private String libWebEtb;

	@Column(name="LIC_ETB", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licEtb;

	@Column(name="TEM_EN_SVE_ETB", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temEnSveEtb;

	//bi-directional many-to-one association to Departement
	@ManyToOne
	@JoinColumn(name="COD_DEP")
	private Departement departement;
	
	//bi-directional many-to-one association to Departement
	@ManyToOne
	@JoinColumn(name="COD_COM_ADR_ETB")
	private Commune commune;
}