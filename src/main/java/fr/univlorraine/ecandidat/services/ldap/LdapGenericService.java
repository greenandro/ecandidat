package fr.univlorraine.ecandidat.services.ldap;

import java.io.Serializable;
import java.util.List;

import org.springframework.ldap.core.ContextMapper;

/**Generic service Ldap
 * @author Kevin Hergalant
 *
 * @param <T>
 */
public interface LdapGenericService<T> extends Serializable{

	/**
	 * @param uid
	 * @return un people Ldap
	 * @throws LdapException
	 */
	public T findByPrimaryKey(String uid) throws LdapException;
	
	/**
	 * @param filter
	 * @return une entité
	 * @throws LdapException
	 */
	public T findEntityByFilter(String filter) throws LdapException;
	
	/**
	 * 
	 * @param filter
	 * @return une liste d'entité
	 * @throws LdapException 
	 * @throws LdapServiceException
	 */
	public List<T> findEntitiesByFilter(String filter) throws LdapException;


	/**
	 * Mapping l'entité LDAP vers l'objet
	 * @return le ContextMapper
	 */
	public ContextMapper<?> getContextMapper();

}
