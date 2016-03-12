package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les dates pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierDate implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 6786025518369323993L;

	private String dateRetour;
	private String dateConfirmation;
	private String dateJury;
	private String datePublication;
	
	public ExportDossierDate(String datRetour, String datConfirmation,
			String datJury, String datPubli) {
		super();
		this.dateRetour = MethodUtils.formatToExport(datRetour);
		this.dateConfirmation = MethodUtils.formatToExport(datConfirmation);
		this.dateJury = MethodUtils.formatToExport(datJury);
		this.datePublication = MethodUtils.formatToExport(datPubli);
	}

	public ExportDossierDate() {
		super();
	}	
	
}
