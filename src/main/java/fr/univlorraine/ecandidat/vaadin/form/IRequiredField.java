package fr.univlorraine.ecandidat.vaadin.form;

/**
 * Interfqce de field Required
 * @author Kevin Hergalant
 *
 */
public interface IRequiredField {

	/**
	 * Affiche le message d'erreur
	 */
	public void preCommit();
	
	/** Initialise le champs
	 * @param immediate
	 */
	public void initField(Boolean immediate);
}
