package fr.univlorraine.ecandidat.services.security;

import java.io.Serializable;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/** AuthenticationProvider perso
 * @author Kevin Hergalant
 *
 */
public class SecurityAuthenticationProvider implements AuthenticationProvider, Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 735080362002894490L;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		 return (RememberMeAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
