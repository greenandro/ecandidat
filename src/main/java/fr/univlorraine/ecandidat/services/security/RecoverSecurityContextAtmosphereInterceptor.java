package fr.univlorraine.ecandidat.services.security;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import fr.univlorraine.tools.logback.UserMdcServletFilter;

/**
 * Interceptor Atmosphere permettant de restaurer le SecurityContext dans le
 * SecurityContextHolder.
 * 
 * @see <a href=
 *      "https://groups.google.com/forum/#!msg/atmosphere-framework/8yyOQALZEP8/ZCf4BHRgh_EJ">
 *      https://groups.google.com/forum/#!msg/atmosphere-framework/8yyOQALZEP8/
 *      ZCf4BHRgh_EJ</a>
 * @author Adrien Colson
 */
public class RecoverSecurityContextAtmosphereInterceptor implements AtmosphereInterceptor {

	/** Logger de classe. */
	private transient Logger logger = LoggerFactory.getLogger(RecoverSecurityContextAtmosphereInterceptor.class);

	/**
	 * Initialise les champs transient.
	 * 
	 * @see java.io.ObjectInputStream#defaultReadObject()
	 * @param inputStream
	 *            deserializes primitive data and objects previously written
	 *            using an ObjectOutputStream.
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws ClassNotFoundException
	 *             if the class of a serialized object could not be found.
	 */
	private void readObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		inputStream.defaultReadObject();
		logger = LoggerFactory.getLogger(RecoverSecurityContextAtmosphereInterceptor.class);
	}

	/**
	 * @see org.atmosphere.cpr.AtmosphereInterceptor#configure(org.atmosphere.cpr.AtmosphereConfig)
	 */
	@Override
	public void configure(final AtmosphereConfig atmosphereConfig) {
	}

	/**
	 * @see org.atmosphere.cpr.AtmosphereInterceptor#inspect(org.atmosphere.cpr.AtmosphereResource)
	 */
	@Override
	public Action inspect(final AtmosphereResource atmosphereResource) {
		logger.trace("Recover SecurityContext in SecurityContextHolder");
		final SecurityContext context = (SecurityContext) atmosphereResource.getRequest().getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		/*System.out.println("1 Old COntext : "+SecurityContextHolder.getContext());
		System.out.println("1 New COntext : "+context);*/
		if (context == null) {
			SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
		} else {
			SecurityContextHolder.setContext(context);
		}

		final Authentication auth = context.getAuthentication();
		if (auth instanceof Authentication) {
			MDC.put(UserMdcServletFilter.USER_KEY, auth.getName());
			logger.trace("Username set in MDC");
		}

		return Action.CONTINUE;
	}

	/**
	 * @see org.atmosphere.cpr.AtmosphereInterceptor#postInspect(org.atmosphere.cpr.AtmosphereResource)
	 */
	@Override
	public void postInspect(final AtmosphereResource atmosphereResource) {
		MDC.remove(UserMdcServletFilter.USER_KEY);		
		/*SecurityContext context = SecurityContextHolder.getContext();
	    atmosphereResource.getRequest().getSession().setAttribute(
	            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
	    SecurityContextHolder.clearContext();
	    System.out.println("2 Old COntext : "+SecurityContextHolder.getContext());
		System.out.println("2 New COntext : "+context);*/
		logger.trace("Username removed from MDC");
	}

}
