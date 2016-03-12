package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


/**
 * The persistent class for the IND_BAC database table.
 * 
 */
@Entity
@Table(name="IND_BAC")
@Data
public class WSBac implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="COD_IND")
	private long codInd;

	@Column(name="COD_BAC", length=4)
	private String codBac;

	@Column(name="COD_DEP", length=3)
	private String codDep;

	@Column(name="COD_ETB", length=8)
	private String codEtb;

	@Column(name="COD_MNB", length=2)
	private String codMnb;

	@Column(name="DAA_OBT_BAC_IBA", length=4)
	private String daaObtBacIba;
	
	@Column(name="TEM_INS_ADM", nullable=false, length=1)
	private String temInsAdm;

	public WSBac() {
		super();
	}

	public WSBac(long codInd, String codBac, String codDep, String codEtb,
			String codMnb, String daaObtBacIba, String temInsAdm) {
		super();
		this.codInd = codInd;
		this.codBac = codBac;
		this.codDep = codDep;
		this.codEtb = codEtb;
		this.codMnb = codMnb;
		this.daaObtBacIba = daaObtBacIba;
		this.temInsAdm = temInsAdm;
	}
}