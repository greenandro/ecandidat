package fr.univlorraine.ecandidat.services.file;

import java.io.InputStream;
import java.io.Serializable;

import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;

public interface FileManager extends Serializable{
	
	/**
	 * @return le type de dematerialisation
	 */
	public String getType();
	
	/**
	 * test le mode de dematerialisation
	 */
	public Boolean testSession();

	/** Creéé un fichier provenant d'une fenetre d'upload
	 * @param file
	 * @param mimeType
	 * @param filename
	 * @param length
	 * @param typeFichier
	 * @param prefixe
	 * @return le fichier
	 * @throws FileException
	 */
	public FileCustom createFileFromUpload(ByteArrayInOutStream file, String mimeType, String filename, long length, String typeFichier, String prefixe) throws FileException;

	/** Supprime un fichier
	 * @param fichier
	 * @throws FileException
	 */
	public void deleteFile(Fichier fichier) throws FileException;

	/** Recupere un flux permettant de telecharger un fichier
	 * @param file
	 * @return l'InputStream du fichier
	 * @throws FileException
	 */
	public InputStream getInputStreamFromFile(Fichier file) throws FileException;
	
}
