package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPro;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierCursusPro implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 6111120936286501453L;

	private String annee;
	private String duree;
	private String organisme;
	private String intitule;
	private String objectif;
	
	public ExportDossierCursusPro() {
		super();
	}

	public ExportDossierCursusPro(CandidatCursusPro cursus) {
		this.annee = String.valueOf(cursus.getAnneeCursusPro());
		this.duree = MethodUtils.formatToExport(cursus.getDureeCursusPro());
		this.organisme = MethodUtils.formatToExport(cursus.getOrganismeCursusPro());
		this.intitule = MethodUtils.formatToExport(cursus.getIntituleCursusPro());
		this.objectif = MethodUtils.formatToExport(cursus.getObjectifCursusPro());
	}
}
