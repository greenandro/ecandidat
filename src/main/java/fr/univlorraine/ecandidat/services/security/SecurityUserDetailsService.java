package fr.univlorraine.ecandidat.services.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/** UserDetailsService perso
 * @author Kevin Hergalant
 *
 */
public class SecurityUserDetailsService implements UserDetailsService{

	/**
	 * Le mapper
	 */
	private SecurityUserDetailMapper userDetailsMapper = new SecurityUserDetailMapper();
	

	/**
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsMapper.mapUserFromContext(username);
    }

	/**
	 * @param userDetailsMapper
	 */
	public void setUserDetailsMapper(SecurityUserDetailMapper userDetailsMapper) {
        Assert.notNull(userDetailsMapper, "userDetailsMapper must not be null");
        this.userDetailsMapper = userDetailsMapper;
    }
}
