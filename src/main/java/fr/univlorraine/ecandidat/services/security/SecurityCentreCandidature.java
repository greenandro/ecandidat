package fr.univlorraine.ecandidat.services.security;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;

/**
 * La classe de centre candidature d'un user
 * @author Kevin Hergalant
 *
 */
@Data
public class SecurityCentreCandidature implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = -6390831536479919293L;
	
	private Integer idCtrCand;
	private String libCtrCand;
	private String codCGE;
	private Boolean isAdmin;
	private List<DroitProfilFonc> listFonctionnalite;
	private Boolean isGestAllCommission;
	private List<Integer> listeIdCommission;

	public SecurityCentreCandidature(CentreCandidature centre, List<DroitProfilFonc> listFonctionnalite, String codCGE, Boolean isAdmin, Boolean isGestAllCommission, List<Integer> listeIdCommission) {
		this.idCtrCand = centre.getIdCtrCand();
		this.libCtrCand = centre.getLibCtrCand();
		this.listFonctionnalite = listFonctionnalite;
		this.codCGE = codCGE;
		this.isAdmin = isAdmin;
		this.isGestAllCommission = isGestAllCommission;
		this.listeIdCommission = listeIdCommission;
	}
}
