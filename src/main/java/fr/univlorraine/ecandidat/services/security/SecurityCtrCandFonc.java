package fr.univlorraine.ecandidat.services.security;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * La classe de fonctionnalité de centre candidature d'un user
 * @author Kevin Hergalant
 *
 */
@Data
public class SecurityCtrCandFonc implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 6191054620315480607L;
	
	private Integer idCtrCand;
	private Boolean readOnly;
	private Boolean isGestAllCommission;
	private List<Integer> listeIdCommission;
	
	public SecurityCtrCandFonc(Integer idCtrCand, Boolean readOnly, Boolean isGestAllCommission, List<Integer> listeIdCommission) {
		super();
		this.idCtrCand = idCtrCand;
		this.readOnly = readOnly;
		this.isGestAllCommission = isGestAllCommission;
		this.listeIdCommission = listeIdCommission;
	}
}
