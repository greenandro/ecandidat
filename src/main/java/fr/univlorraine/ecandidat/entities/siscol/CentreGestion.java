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
 * The persistent class for the CENTRE_GESTION database table.
 * 
 */
@Entity
@Table(name="CENTRE_GESTION")
@Data @EqualsAndHashCode(of="codCge")
public class CentreGestion implements Serializable {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = 580788836615056889L;

	@Id
	@Column(name="COD_CGE", unique=true, nullable=false, length=3)
	@Size(max = 3) 
	@NotNull
	private String codCge;

	@Column(name="LIB_CGE", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libCge;

	@Column(name="LIC_CGE", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licCge;

	@Column(name="TEM_EN_SVE_CGE", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temEnSveCge;
}