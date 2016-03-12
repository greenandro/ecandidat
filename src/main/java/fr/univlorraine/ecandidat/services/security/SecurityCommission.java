package fr.univlorraine.ecandidat.services.security;

import java.io.Serializable;

import lombok.Data;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;

/**
 * La classe de commission d'un user
 * @author Kevin Hergalant
 *
 */
@Data
public class SecurityCommission implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 521681334340754635L;
	
	private Integer idComm;
	private String libComm;

	public SecurityCommission(Commission comm) {
		this.idComm = comm.getIdComm();
		this.libComm = comm.getLibComm();
	}
}
