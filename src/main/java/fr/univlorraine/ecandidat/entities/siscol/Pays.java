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
 * The persistent class for the PAYS database table.
 * 
 */
@Entity
@Table(name="PAYS")
@Data @EqualsAndHashCode(of="codPay")
public class Pays implements Serializable {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = 4731947068905653314L;

	@Id
	@Column(name="COD_PAY", unique=true, nullable=false, length=3)
	@Size(max = 3) 
	@NotNull
	private String codPay;

	@Column(name="LIB_NAT", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libNat;

	@Column(name="LIB_PAY", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libPay;

	@Column(name="LIC_PAY", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licPay;

	@Column(name="TEM_EN_SVE_PAY", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temEnSvePay;
}