package fr.univlorraine.ecandidat.utils;

/**
 * Class d'exception générique
 * @author Kevin Hergalant
 *
 */
public class CustomException extends Exception {

	/*** serialVersionUID */
	private static final long serialVersionUID = -2446652512370277694L;

	/**
	 * Constructeur
	 */
	public CustomException() {
	}

	/** Constructeur avec message
	 * @param message
	 */
	public CustomException(String message) {
		super(message);
	}

	/** Constructeur avec cause
	 * @param cause
	 */
	public CustomException(Throwable cause) {
		super(cause);
	}

	/** Constructeur avec message et cause
	 * @param message
	 * @param cause
	 */
	public CustomException(String message, Throwable cause) {
		super(message, cause);
	}
}
