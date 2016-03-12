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
 * The persistent class for the TYP_DIPLOME database table.
 * 
 */
@Entity
@Table(name="TYP_DIPLOME")
@Data @EqualsAndHashCode(of="codTpdEtb")
public class TypDiplome implements Serializable {

	/*** serialVersionUID */
	private static final long serialVersionUID = -8789223824862632227L;

	@Id
	@Column(name="COD_TPD_ETB", unique=true, nullable=false, length=2)
	@Size(max = 2) 
	@NotNull
	private String codTpdEtb;

	@Column(name="LIB_TPD", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libTpd;

	@Column(name="LIC_TPD", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licTpd;

	@Column(name="TEM_EN_SVE_TPD", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temEnSveTpd;
}