package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierMotivationAvis implements Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = 7124571589830336719L;

	private String libelle;
	
	public ExportDossierMotivationAvis() {
		super();
	}

	public ExportDossierMotivationAvis(String libelle) {
		this.libelle = libelle;
	}
}
