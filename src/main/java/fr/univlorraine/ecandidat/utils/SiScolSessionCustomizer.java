package fr.univlorraine.ecandidat.utils;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;

/**
 * Classe Customizer pour les appels apogée
 * @author Kevin Hergalant
 *
 */
public class SiScolSessionCustomizer implements SessionCustomizer {


	@Override
	public void customize(Session session) throws Exception {
		 DatabaseLogin login = (DatabaseLogin)session.getDatasourceLogin();
		 login.setQueryRetryAttemptCount(0);
	}
}
