package fr.univlorraine.ecandidat.services.security;

import java.util.Collection;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.security.core.GrantedAuthority;

/**
 * La classe utilisateur candidat de l'application
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SecurityUserCandidat extends SecurityUser{
	
	/*** serialVersionUID */
	private static final long serialVersionUID = -2172536978450199780L;
	
	private Integer idCptMin;
	private Integer idCandidat;
	private String noDossierOPI;
	private Boolean cptMinValid;
	private String codLangue;
	private Boolean mailValid;

	public SecurityUserCandidat(String username,String displayName,Collection<? extends GrantedAuthority> authorities, Integer idCptMin, String noDossierOPI, Boolean cptMinValid, Boolean mailValid, String codLangue) {
		super(username, displayName, authorities);
		this.idCptMin = idCptMin;
		this.noDossierOPI = noDossierOPI;
		this.cptMinValid = cptMinValid;
		this.mailValid = mailValid;
		this.codLangue = codLangue;
	}
}
