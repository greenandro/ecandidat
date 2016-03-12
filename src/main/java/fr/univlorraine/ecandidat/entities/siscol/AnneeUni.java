package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


/**
 * The persistent class for the ANNEE_UNI database table.
 * 
 */
@Entity
@Table(name="ANNEE_UNI")
@Data
public class AnneeUni implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="COD_ANU")
	private String codAnu;

	@Column(name="ETA_ANU_IAE")
	private String etaAnuIae;

	@Column(name="LIB_ANU")
	private String libAnu;

	@Column(name="LIC_ANU")
	private String licAnu;
}