package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the TYP_RESULTAT database table.
 * 
 */
@Entity
@Table(name="TYP_RESULTAT")
@Data @EqualsAndHashCode(of="codTre")
public class TypResultat implements Serializable {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = 1383399169282846852L;

	@Id
	@Column(name="COD_TRE", unique=true, nullable=false, length=4)
	@Size(max = 4) 
	@NotNull
	private String codTre;

	@Column(name="LIB_TRE", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String libTre;

	@Column(name="LIC_TRE", nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String licTre;

	@Column(name="TEM_EN_SVE_TRE", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temEnSveTre;

}