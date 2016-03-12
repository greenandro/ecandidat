package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the MENTION database table.
 * 
 */
@Entity
@Data @EqualsAndHashCode(of="codMen")
public class Mention implements Serializable {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = 1383399169282846852L;

	@Id
	@Column(name="COD_MEN", unique=true, nullable=false, length=2)
	@Size(max = 2) 
	@NotNull
	private String codMen;

	@Column(name="LIB_MEN", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String libMen;

	@Column(name="LIC_MEN", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licMen;

	@Column(name="TEM_EN_SVE_MEN", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temEnSveMen;

}