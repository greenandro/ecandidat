package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/** 
 * Ojet de PJ formatté pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierPj implements Serializable {
	/**serialVersionUID**/
	private static final long serialVersionUID = -4234330768123564863L;

	private String libelle;
	private String statut;
	private String comment;
	private String libFichier;
	
	public ExportDossierPj(String libelle, String statut, String comment) {
		super();
		this.libelle = MethodUtils.formatToExport(libelle);
		this.statut = MethodUtils.formatToExport(statut);
		this.comment = MethodUtils.formatToExport(comment);
		this.libFichier = "";
	}

	public ExportDossierPj() {
		super();
	}
	
}
