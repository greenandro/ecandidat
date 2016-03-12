package fr.univlorraine.ecandidat.config;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.aop.Advisor;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.vaadin.spring.annotation.SpringView;

import fr.univlorraine.ecandidat.Initializer;
import fr.univlorraine.ecandidat.controllers.UserController;

/**
 * Configuration mode debug
 * 
 * @author Adrien Colson
 */
@Configuration @Profile(Initializer.DEBUG_PROFILE)
public class DebugConfig {

	/**
	 * @return Interceptor permettant de logger les appels aux méthodes
	 */
	@Bean
	public CustomizableTraceInterceptor customizableTraceInterceptor() {
		CustomizableTraceInterceptor customizableTraceInterceptor = new CustomizableTraceInterceptor();
		customizableTraceInterceptor.setUseDynamicLogger(true);
		customizableTraceInterceptor.setEnterMessage("Entering $[methodName]($[arguments])");
		customizableTraceInterceptor.setExitMessage("Leaving  $[methodName](), returned $[returnValue]");
		return customizableTraceInterceptor;
	}

	/**
	 * @return customizableTraceInterceptor sur les méthodes public des classes du package controllers
	 */
	@Bean
	public Advisor controllersAdvisor() {
		return new StaticMethodMatcherPointcutAdvisor(customizableTraceInterceptor()) {
			private static final long serialVersionUID = 5897279987213542868L;

			@Override
			public boolean matches(Method method, Class<?> clazz) {
				return Modifier.isPublic(method.getModifiers()) && clazz.getPackage() != null && clazz.getPackage().getName().startsWith(UserController.class.getPackage().getName());
			}
		};
	}

	/**
	 * @return customizableTraceInterceptor sur les méthodes enter des vues
	 */
	@Bean
	public Advisor viewsEnterAdvisor() {
		return new StaticMethodMatcherPointcutAdvisor(customizableTraceInterceptor()) {
			private static final long serialVersionUID = -7297125641462899887L;

			@Override
			public boolean matches(Method method, Class<?> clazz) {
				return clazz.isAnnotationPresent(SpringView.class) && "enter".equals(method.getName());
			}
		};
	}

}
