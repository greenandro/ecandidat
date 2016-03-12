package fr.univlorraine.ecandidat.config;

import java.util.UUID;

import javax.annotation.Resource;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

import fr.univlorraine.ecandidat.services.security.SecurityAuthenticationProvider;
import fr.univlorraine.ecandidat.services.security.SecurityUserDetailMapper;
import fr.univlorraine.ecandidat.services.security.SecurityUserDetailsService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;

/**
 * Configuration Spring Security
 * 
 * @author Adrien Colson
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true, jsr250Enabled=true, prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Resource
	private Environment environment;

	/**
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#authenticationManagerBean()
	 */
	@Bean(name="authenticationManager")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	/**
	 * @return authenticationManager candidat
	 * @throws Exception
	 */
	@Bean(name="authenticationManagerCandidat")
	public SecurityAuthenticationProvider authenticationManagerCandidatBean() throws Exception {
		return new SecurityAuthenticationProvider();
	}

	/**
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling()
				.authenticationEntryPoint(casEntryPoint())
				.and()
			.authorizeRequests()
				.antMatchers(ConstanteUtils.SECURITY_CONNECT_PATH+"/**").authenticated()
				.antMatchers("/**").permitAll()
				.antMatchers(ConstanteUtils.SECURITY_SWITCH_PATH).hasAuthority(NomenclatureUtils.DROIT_PROFIL_ADMIN)
				.antMatchers(ConstanteUtils.SECURITY_SWITCH_BACK_PATH).hasAuthority(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR)
				.anyRequest().authenticated()
				.and()
			.addFilterBefore(singleSignOutFilter(), LogoutFilter.class)
			//.addFilter(new LogoutFilter(environment.getRequiredProperty("cas.url") + "/logout?service=http://kevin-hergalant.univ-lorraine.fr:8080/", new SecurityContextLogoutHandler()))
			.addFilter(new LogoutFilter(environment.getRequiredProperty("cas.url") + ConstanteUtils.SECURITY_LOGOUT_PATH, new SecurityContextLogoutHandler()))
			.addFilter(casAuthenticationFilter())
			//.addFilterAfter(anonymousFilter(), CasAuthenticationFilter.class)
			.addFilterAfter(switchUserFilter(), FilterSecurityInterceptor.class)
			/* La protection Spring Security contre le Cross Scripting Request Forgery est désactivée, Vaadin implémente sa propre protection */
			.csrf().disable()
			.headers()
				/* Autorise l'affichage en iFrame */
				.frameOptions().disable()
				/* Supprime la gestion du cache du navigateur, pour corriger le bug IE de chargement des polices cf. http://stackoverflow.com/questions/7748140/font-face-eot-not-loading-over-https */
				.cacheControl().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(casAuthenticationProvider());
	}

	/* Configuration CAS */
	@Bean
	public SingleSignOutFilter singleSignOutFilter() {
		SingleSignOutFilter filter = new SingleSignOutFilter();
		filter.setCasServerUrlPrefix(environment.getRequiredProperty("app.url"));
		return filter;
	}

	@Bean
	public ServiceProperties casServiceProperties() {
		ServiceProperties casServiceProperties = new ServiceProperties();
		casServiceProperties.setService(environment.getRequiredProperty("app.url") + "/login/cas");
		return casServiceProperties;
	}

	@Bean
	public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
		CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
		casAuthenticationFilter.setAuthenticationManager(authenticationManager());
		return casAuthenticationFilter;
	}

	@Bean
	public CasAuthenticationEntryPoint casEntryPoint() {
		CasAuthenticationEntryPoint casEntryPoint = new CasAuthenticationEntryPoint();
		casEntryPoint.setLoginUrl(environment.getRequiredProperty("cas.url") + "/login");
		casEntryPoint.setServiceProperties(casServiceProperties());
		return casEntryPoint;
	}

	@Bean
	public CasAuthenticationProvider casAuthenticationProvider() throws Exception {
		CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
		casAuthenticationProvider.setKey(UUID.randomUUID().toString());
		casAuthenticationProvider.setAuthenticationUserDetailsService(new UserDetailsByNameServiceWrapper<CasAssertionAuthenticationToken>(userDetailsServiceBean()));
		casAuthenticationProvider.setServiceProperties(casServiceProperties());
		casAuthenticationProvider.setTicketValidator(new Cas20ServiceTicketValidator(environment.getRequiredProperty("cas.url")));
		return casAuthenticationProvider;
	}

	/*Config du userDetailsMapper*/
	@Bean
	public SecurityUserDetailMapper securityUserDetailsMapper() throws Exception {
		return new SecurityUserDetailMapper();
	}
	
	@Bean(name="userDetailsService")
	@Override
	public UserDetailsService userDetailsServiceBean() throws Exception {
		SecurityUserDetailsService userDetailsService = new SecurityUserDetailsService();
		userDetailsService.setUserDetailsMapper(securityUserDetailsMapper());
		return userDetailsService;
	}


	/* Filtre permettant de prendre le rôle d'un autre utilisateur */
	@Bean
	public SwitchUserFilter switchUserFilter() throws Exception {
		SwitchUserFilter switchUserFilter = new SwitchUserFilter();
		switchUserFilter.setUserDetailsService(userDetailsServiceBean());
		switchUserFilter.setSwitchUserUrl(ConstanteUtils.SECURITY_SWITCH_PATH);
		switchUserFilter.setExitUserUrl(ConstanteUtils.SECURITY_SWITCH_BACK_PATH);
		switchUserFilter.setTargetUrl("/");
		return switchUserFilter;
	}

}
