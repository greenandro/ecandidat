package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;


/**
 * The persistent class for the VERSION_APO database table.
 * 
 */
@Entity
@Table(name="VERSION_APO")
@Data
public class VersionApo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private VersionApoPK id;

	@Temporal(TemporalType.DATE)
	@Column(name="DAT_CRE")
	private Date datCre;

	@Column(name="LIB_COM")
	private String libCom;

	@Column(name="TEM_BASE")
	private String temBase;

	@Column(name="TEM_EN_SVE_VER")
	private String temEnSveVer;
}