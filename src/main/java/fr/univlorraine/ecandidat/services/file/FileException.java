package fr.univlorraine.ecandidat.services.file;

/**
 * Class d'exception pour les fichiers
 * @author Kevin Hergalant
 *
 */
public class FileException extends Exception{

	/*** serialVersionUID */
	private static final long serialVersionUID = -2174850689897635181L;

	/**
	 * Constructeur
	 */
	public FileException() {
	}

	/** Constructeur avec message
	 * @param message
	 */
	public FileException(String message) {
		super(message);
	}

	/** Constructeur avec cause
	 * @param cause
	 */
	public FileException(Throwable cause) {
		super(cause);
	}

	/** Constructeur avec message et cause
	 * @param message
	 * @param cause
	 */
	public FileException(String message, Throwable cause) {
		super(message, cause);
	}
}
