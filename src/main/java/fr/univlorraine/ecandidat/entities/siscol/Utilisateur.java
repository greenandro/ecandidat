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
 * The persistent class for the UTILISATEUR database table.
 * 
 */
@Entity
@Data @EqualsAndHashCode(of="codUti")
public class Utilisateur implements Serializable {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = 6806478024550984011L;

	@Id
	@Column(name="COD_UTI", unique=true, nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String codUti;

	@Column(name="ADR_MAIL_UTI", length=200)
	@Size(max = 200) 
	private String adrMailUti;

	@Column(name="LIB_CMT_UTI", length=200)
	@Size(max = 200) 
	private String libCmtUti;

	@Column(name="TEM_EN_SVE_UTI", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temEnSveUti;

	//bi-directional many-to-one association to CentreGestion
	@ManyToOne
	@JoinColumn(name="COD_CGE")
	private CentreGestion centreGestion;
}