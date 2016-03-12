package fr.univlorraine.ecandidat.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Configuration Ldap
 * 
 * @author Kevin Hergalant
 */
@Configuration
public class LdapConfig {

	@Resource
	private Environment environment;
	
	/**
	 * LdapContextSource
	 * @return le context ldap
	 */
	@Bean
	public LdapContextSource contextSourceLdap() {
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setUrl(environment.getProperty("ldap.url"));
		ldapContextSource.setBase(environment.getProperty("ldap.base"));
		ldapContextSource.setUserDn(environment.getProperty("ldap.user"));
		ldapContextSource.setPassword(environment.getProperty("ldap.pwd"));
		return ldapContextSource;
	}
	
	/**
	 * LdapTemplate
	 * @return le template de lecture
	 */
	@Bean
	public LdapTemplate ldapTemplateRead(){
		LdapTemplate ldapTemplateRead = new LdapTemplate();
		ldapTemplateRead.setContextSource(contextSourceLdap());
		return ldapTemplateRead;
	}
}
