package fr.univlorraine.ecandidat.services.ldap;

/**
 * Class d'exception pour les services ldap
 * @author Kevin Hergalant
 *
 */
public class LdapServiceException extends Exception {
	/***serialVersionUID*/
	private static final long serialVersionUID = -550702354641987484L;

	/**
	 * Constructeur
	 */
	public LdapServiceException() {
	}

	/** Constructeur avec message
	 * @param message
	 */
	public LdapServiceException(String message) {
		super(message);
	}

	/** Constructeur avec cause
	 * @param cause
	 */
	public LdapServiceException(Throwable cause) {
		super(cause);
	}

	/** Constructeur avec message et cause
	 * @param message
	 * @param cause
	 */
	public LdapServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
