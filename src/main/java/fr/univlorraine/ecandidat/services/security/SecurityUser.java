package fr.univlorraine.ecandidat.services.security;

import java.util.Collection;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/** La classe utilisateur de l'application
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SecurityUser extends User{
	
	private String displayName;
	
	/*** serialVersionUID */
	private static final long serialVersionUID = -8810007809059984415L;

	public SecurityUser(String username, String displayName,Collection<? extends GrantedAuthority> authorities) {
		super(username, "x", authorities);
		setDisplayName(displayName);
	}
}
