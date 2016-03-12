package fr.univlorraine.ecandidat.config;

import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/** 
 * Configuration Mail
 * 
 * @author Kevin Hergalant
 *
 */
@Configuration 
public class MailConfig {

	@Resource
	private Environment environment;
	
	/**
	 * @return le service mail d'envoi
	 */
	@Bean
	public JavaMailSender javaMailService() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

		javaMailSender.setHost(environment.getProperty("mail.smtpHost"));
		String enable = environment.getProperty("enableTestMode");
		if (enable !=null){
			try{
				Boolean test = Boolean.valueOf(enable);
				if (test){
					javaMailSender.setPort(1025);
					javaMailSender.setHost("smtp-test.sig.univ-lorraine.fr");
				}
				
			}catch (Exception e){
			}
		}
		
		javaMailSender.setJavaMailProperties(getMailProperties());

		return javaMailSender;
	}

	/**
	 * @return les properties d'envoi de mail
	 */
	private Properties getMailProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.auth", "false");
		properties.setProperty("mail.smtp.starttls.enable", "false");
		properties.setProperty("mail.debug", "false");
		return properties;
	}
}
