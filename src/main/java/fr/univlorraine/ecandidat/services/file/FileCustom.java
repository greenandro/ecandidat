package fr.univlorraine.ecandidat.services.file;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class custom représentant un fichier
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(of="id")
public class FileCustom {
	private String id;
	private String cod;
	private String fileName;
	private String mimeType;
	
	/** Constructeur
	 * @param id
	 * @param cod
	 * @param fileName
	 * @param mimeType
	 */
	public FileCustom(String id, String cod, String fileName, String mimeType) {
		super();
		this.id = id;
		this.cod = cod;
		this.fileName = fileName;
		this.mimeType = mimeType;
	}
	
}
