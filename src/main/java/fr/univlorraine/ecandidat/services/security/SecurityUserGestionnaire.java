package fr.univlorraine.ecandidat.services.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * La classe utilisateur gestionnaire de l'application
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SecurityUserGestionnaire extends SecurityUser{
	
	/*** serialVersionUID */
	private static final long serialVersionUID = 2907919583881016015L;
	
	private SecurityCentreCandidature centreCandidature;
	private SecurityCommission commission;
	private String displayNameCandidat;
	private String noDossierCandidat;

	public SecurityUserGestionnaire(String username,String displayName,Collection<? extends GrantedAuthority> authorities, 
			SecurityCentreCandidature centreCandidature, SecurityCommission commission) {
		super(username, displayName, authorities);
		this.centreCandidature = centreCandidature;
		this.commission = commission;
		this.noDossierCandidat = null;
		this.displayNameCandidat = null;
	}
}
