package fr.univlorraine.ecandidat.services.ldap;

/**
 * Class d'exception pour le ldap
 * @author Kevin Hergalant
 *
 */
public class LdapException extends Exception {

	/*** serialVersionUID */
	private static final long serialVersionUID = -8985236868957490755L;

	/**
	 * Constructeur
	 */
	public LdapException() {
	}

	/** Constructeur avec message
	 * @param message
	 */
	public LdapException(String message) {
		super(message);
	}

	/** Constructeur avec cause
	 * @param cause
	 */
	public LdapException(Throwable cause) {
		super(cause);
	}

	/** Constructeur avec message et cause
	 * @param message
	 * @param cause
	 */
	public LdapException(String message, Throwable cause) {
		super(message, cause);
	}
}
