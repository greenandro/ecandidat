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
 * The persistent class for the MENTION_NIV_BAC database table.
 * 
 */
@Entity
@Table(name="MENTION_NIV_BAC")
@Data @EqualsAndHashCode(of="codMnb")
public class MentionNivBac implements Serializable {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = 7496569857238710271L;

	@Id
	@Column(name="COD_MNB", unique=true, nullable=false, length=2)
	@Size(max = 2) 
	@NotNull
	private String codMnb;

	@Column(name="LIB_MNB", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libMnb;

	@Column(name="LIC_MNB", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licMnb;

	@Column(name="TEM_EN_SVE_MNB", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temEnSveMnb;
}