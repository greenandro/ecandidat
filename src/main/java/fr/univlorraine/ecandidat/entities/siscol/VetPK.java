package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the Vet database table.
 * 
 */
@Embeddable
@ToString(of={"codEtpVet","codVrsVet","codCge","codTpd"})
@Data @EqualsAndHashCode(of={"codEtpVet","codVrsVet","codCge","codTpd"})
public class VetPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String codEtpVet;
	private String codVrsVet;
	private String codCge;
	private String codTpd;
	

}