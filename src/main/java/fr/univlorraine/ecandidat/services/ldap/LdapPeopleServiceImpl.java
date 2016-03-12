package fr.univlorraine.ecandidat.services.ldap;

import java.util.List;

import javax.annotation.Resource;
import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.SizeLimitExceededException;
import org.springframework.ldap.TimeLimitExceededException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**Implementation du service Ldap de people
 * @author Kevin Hergalant
 *
 */
@Component(value="ldapPeopleServiceImpl")
public class LdapPeopleServiceImpl implements LdapGenericService<PeopleLdap> {

	/**serialVersionUID**/
	private static final long serialVersionUID = -5154026091229021611L;
	/**
	 * Logger
	 */
	static final Logger LOG = LoggerFactory.getLogger(LdapPeopleServiceImpl.class);
	
	/**
	 * le base DN pour les recherches ldap
	 */	
	@Value("${ldap.branche.people}")
	private String baseDn;
	@Value("${ldap.champs.uid}")
	private String champsUid;
	@Value("${ldap.champs.displayName}")
	private String champsDisplayName;
	@Value("${ldap.champs.mail}")
	private String champsMail;
	@Value("${ldap.champs.sn}")
	private String champsSn;
	@Value("${ldap.champs.cn}")
	private String champsCn;
	@Value("${ldap.champs.supannEtuId}")
	private String champsSupannEtuId;
	@Value("${ldap.champs.supannCivilite}")
	private String champsSupannCivilite;
	@Value("${ldap.champs.givenName}")
	private String champsGivenName;

	/**
	 * Ldap Template de lecture
	 */
	@Resource
	private LdapTemplate ldapTemplateRead;
	
	/**
	 * @see fr.univlorraine.ecandidat.services.ldap.LdapGenericService#findByPrimaryKey(java.lang.String)
	 */
	@Override
	public PeopleLdap findByPrimaryKey(String uid) throws LdapException {
		Name dn = LdapNameBuilder.newInstance(baseDn)
			.add(champsUid, uid)
			.build();
		try{
			PeopleLdap p = (PeopleLdap) ldapTemplateRead.lookup(dn, getContextMapper());
			return p;
		}catch (NameNotFoundException e) {
			if(e.getMessage().contains("error code 32 - No Such Object")){
				return null;
			}
			return null;
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.ldap.LdapGenericService#findEntityByFilter(java.lang.String)
	 */
	@Override
	public PeopleLdap findEntityByFilter(final String filter) throws LdapException {
		PeopleLdap r = new PeopleLdap();
		try{
			List<PeopleLdap> l = ldapTemplateRead.search(baseDn, filter,SearchControls.SUBTREE_SCOPE, getContextMapper());
			if(l!=null && l.size()>0){
				r = (PeopleLdap) l.get(0);
			}else{
				r = null;
			}
		}catch (NameNotFoundException e) {
			return null;
		}catch (TimeLimitExceededException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			throw new LdapException("ldap.search.timeexceeded", e.getCause());
		}
		return r;
	}
	
	/**
	 * @see fr.univlorraine.ecandidat.services.ldap.LdapGenericService#findEntitiesByFilter(java.lang.String)
	 */
	@Override
	public List<PeopleLdap> findEntitiesByFilter(String filter) throws LdapException {
		List<PeopleLdap> l = null;
		try{
			/* Utilisation du ldap de lecture pour nombre de résultat illimité */
			String[] attributes = new String[] { "*", "createTimestamp", "modifyTimestamp" };
			l = ldapTemplateRead.search(baseDn, filter,SearchControls.SUBTREE_SCOPE,attributes, getContextMapper());
			if (l.size()>ConstanteUtils.NB_MAX_RECH_PERS){
				throw new LdapException("ldap.search.toomuchresult");
			}
		}catch (NameNotFoundException e) {
			return null;
		}catch (TimeLimitExceededException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			throw new LdapException("ldap.search.timeexceeded", e.getCause());
		}catch (SizeLimitExceededException e) {
			throw new LdapException("ldap.search.toomuchresult", e.getCause());
		}
		return l;
	}

	/* (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.tools.ldap.LdapGenericService#getContextMapper()
	 */
	@Override
	public ContextMapper<PeopleLdap> getContextMapper() {
		return new PeopleContextMapper();
	}

	/** Le mapper de people
	 * @author Kevin
	 *
	 */
	private class PeopleContextMapper extends AbstractContextMapper<PeopleLdap> {
		public PeopleLdap doMapFromContext(DirContextOperations context) {
			PeopleLdap o = new PeopleLdap();
			o.setObjectClass(context.getStringAttributes("objectClass"));
			o.setUid(context.getStringAttribute(champsUid));
			o.setSn(context.getStringAttribute(champsSn));
			o.setCn(context.getStringAttribute(champsCn));
			o.setDisplayName(context.getStringAttribute(champsDisplayName));
			o.setMail(context.getStringAttribute(champsMail));
			o.setSupannEtuId(context.getStringAttribute(champsSupannEtuId));
			o.setGivenName(context.getStringAttribute(champsGivenName));
			o.setSupannCivilite(context.getStringAttribute(champsSupannCivilite));
			return o;
		}
	}

}
