package fr.univlorraine.ecandidat.services.security;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import fr.univlorraine.ecandidat.controllers.UserController;


/**
 * UserDetailMapper perso
 * @author Kevin Hergalant
 *
 */
public class SecurityUserDetailMapper implements UserDetailsContextMapper{

	@Resource
	private transient UserController userController;
	
	/**
	 * populate l'utilisateur avec les vues autorisées
	 */
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx,String username, Collection<? extends GrantedAuthority> authorities) {
		return new SecurityUser(username, username,new ArrayList<GrantedAuthority>());
	}
	
	public UserDetails mapUserFromContext(String username) {
	    return userController.getSecurityUser(username);
	}
	
	/**
     * The names of any attributes in the user's  entry which represent application
     * roles. These will be converted to <tt>GrantedAuthority</tt>s and added to the
     * list in the returned LdapUserDetails object. The attribute values must be Strings by default.
     *
     * @param roleAttributes the names of the role attributes.
     */
    public void setRoleAttributes(String[] roleAttributes) {
        //this.roleAttributes = roleAttributes;
    }

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		// TODO Auto-generated method stub
		
	}

}
