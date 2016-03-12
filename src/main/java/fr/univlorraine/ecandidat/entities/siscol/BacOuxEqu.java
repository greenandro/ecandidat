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
 * The persistent class for the BAC_OUX_EQU database table.
 * 
 */
@Entity
@Table(name="BAC_OUX_EQU")
@Data @EqualsAndHashCode(of="codBac")
public class BacOuxEqu implements Serializable {

	/*** serialVersionUID */
	private static final long serialVersionUID = -8534062041720967928L;

	@Id
	@Column(name="COD_BAC", unique=true, nullable=false, length=4)
	@Size(max =4) 
	@NotNull
	private String codBac;

	@Column(name="LIB_BAC", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libBac;

	@Column(name="LIC_BAC", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licBac;
	
	@Column(name="DAA_DEB_VLD_BAC", nullable=true)
	@Size(max =4)
	private String daaDebVldBac;
	
	@Column(name="DAA_FIN_VLD_BAC", nullable=true)
	@Size(max =4)
	private String daaFinVldBac;

	@Column(name="TEM_EN_SVE_BAC", length=1)
	@Size(max = 1) 
	private String temEnSveBac;

	@Column(name="TEM_NAT_BAC", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temNatBac;
}