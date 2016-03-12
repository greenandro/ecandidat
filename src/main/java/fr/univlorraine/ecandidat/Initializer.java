package fr.univlorraine.ecandidat;

import java.util.Enumeration;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.atmosphere.cpr.SessionSupport;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import com.vaadin.server.Constants;

import fr.univlorraine.ecandidat.config.SpringConfig;
import fr.univlorraine.tools.logback.UserMdcServletFilter;

/**
 * Initialisation de l'application web
 * 
 * @author Adrien Colson
 */
public class Initializer implements WebApplicationInitializer {

	/**
	 * Profil Spring de debug
	 */
	public final static String DEBUG_PROFILE = "debug";

	/**
	 * Ajoute les paramètres de contexte aux propriétés systèmes, de manière à les rendre accessibles dans logback.xml
	 * @param servletContext
	 */
	private void addContextParametersToSystemProperties(ServletContext servletContext) {
		Enumeration<String> e = servletContext.getInitParameterNames();
		while (e.hasMoreElements()) {
			String parameterName = e.nextElement();
			System.setProperty("context." + parameterName, servletContext.getInitParameter(parameterName));
		}
	}

	/**
	 * @see org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		addContextParametersToSystemProperties(servletContext);

		/* Gestion des sessions dans Atmosphere (Push Vaadin) */
		servletContext.addListener(SessionSupport.class);

		servletContext.addListener(new HttpSessionListener() {
			@Override
			public void sessionCreated(HttpSessionEvent httpSessionEvent) {
				httpSessionEvent.getSession().setMaxInactiveInterval(60);
			}

			@Override
			public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
			}
		});
		
		/* Configure Spring */
		
		String disableDebugModeAvance = servletContext.getInitParameter("disableDebugModeAvance");
		Boolean debugMode = true;
		if (disableDebugModeAvance!=null && Boolean.valueOf(disableDebugModeAvance)){
			debugMode = false;
		}

		AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
		if (!Boolean.valueOf(servletContext.getInitParameter(Constants.SERVLET_PARAMETER_PRODUCTION_MODE)) && debugMode) {
			springContext.getEnvironment().setActiveProfiles(DEBUG_PROFILE);
		}
		springContext.register(SpringConfig.class);
		servletContext.addListener(new ContextLoaderListener(springContext));
		
		String refreshRate = servletContext.getInitParameter("load.balancing.refresh.fixedRate");
		if (refreshRate==null){
			//on place par défaut le refresh à 10min
			servletContext.setInitParameter("load.balancing.refresh.fixedRate", "600000");
		}

		/* Filtre Spring Security */
		FilterRegistration.Dynamic springSecurityFilterChain = servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
		springSecurityFilterChain.addMappingForUrlPatterns(null, false, "/*");

		/* Filtre passant l'utilisateur courant à Logback */
		FilterRegistration.Dynamic userMdcServletFilter = servletContext.addFilter("userMdcServletFilter", UserMdcServletFilter.class);
		userMdcServletFilter.addMappingForUrlPatterns(null, false, "/*");

		/* Servlet REST */
		ServletRegistration.Dynamic restServlet = servletContext.addServlet("rest", new DispatcherServlet(springContext));
		restServlet.setLoadOnStartup(1);
		restServlet.addMapping("/rest", "/rest/*");
	}

}
