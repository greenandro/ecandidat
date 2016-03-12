package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import fr.univlorraine.ecandidat.entities.ecandidat.CandidatStage;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierStage implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 6371858823552443506L;

	private String annee;
	private String duree;
	private String organisme;
	private String descriptif;
	private String quotite;
	
	public ExportDossierStage() {
		super();
	}

	public ExportDossierStage(CandidatStage stage) {
		this.annee = String.valueOf(stage.getAnneeStage());
		this.duree = MethodUtils.formatToExport(stage.getDureeStage());
		this.organisme = MethodUtils.formatToExport(stage.getOrganismeStage());
		this.descriptif = MethodUtils.formatToExport(stage.getDescriptifStage());
		this.quotite = stage.getNbHSemStage()!=null?String.valueOf(stage.getNbHSemStage()):"";
	}
}
