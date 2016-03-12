package fr.univlorraine.ecandidat.config;

import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.vaadin.spring.annotation.EnableVaadin;

import fr.univlorraine.ecandidat.Initializer;

/**
 * Configuration Spring
 * 
 * @author Adrien Colson
 */
@Configuration
@EnableSpringConfigured
@ComponentScan(basePackageClasses=Initializer.class)
@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableVaadin
@EnableScheduling
@PropertySource("classpath:/app.properties")
public class SpringConfig {

	/**
	 * @return PropertySourcesPlaceholderConfigurer qui ajoute les paramètres de contexte aux propriétés Spring
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	/**
	 * @return ResourceBundleMessageSource pour les messages de l'application
	 */
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
		resourceBundleMessageSource.setBasenames(
				"i18n/messages",
				"i18n/backoffice/backoffice-messages",
				"i18n/backoffice/nomenclature-messages",
				"i18n/candidat/candidat-messages");
		resourceBundleMessageSource.setFallbackToSystemLocale(false);
		return resourceBundleMessageSource;
	}
	
	/**
	 * @return un formatter de date
	 */
	@Bean
	public static DateTimeFormatter formatterDate() {
		return DateTimeFormatter.ofPattern("dd/MM/yyyy");
	}
	
	/**
	 * @return un formatter de dateTime
	 */
	@Bean
	public static DateTimeFormatter formatterDateTime() {
		return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	}
	
	/**
	 * @return un formatter de dateTime pour les WS
	 */
	@Bean
	public static DateTimeFormatter formatterDateTimeWS() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * @return un formatter de dateTime pour apogee
	 */
	@Bean
	public static DateTimeFormatter formatterDateTimeApo() {
		return DateTimeFormatter.ofPattern("ddMMyyyy");
	}
}
