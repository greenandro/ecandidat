package fr.univlorraine.ecandidat.services.siscol;

/**
 * Class d'exception pour les appels SiScol
 * @author Kevin Hergalant
 *
 */
public class SiScolException extends Exception {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3475895209155628944L;

	/**
	 * Constructeur
	 */
	public SiScolException() {
	}

	/** Constructeur avec message
	 * @param message
	 */
	public SiScolException(String message) {
		super(message);
	}

	/** Constructeur avec cause
	 * @param cause
	 */
	public SiScolException(Throwable cause) {
		super(cause);
	}

	/** Constructeur avec message et cause
	 * @param message
	 * @param cause
	 */
	public SiScolException(String message, Throwable cause) {
		super(message, cause);
	}
}
