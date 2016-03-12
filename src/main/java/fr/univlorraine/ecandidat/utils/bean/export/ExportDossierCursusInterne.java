package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusInterne;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierCursusInterne implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 6111120936286501453L;

	private String annee;
	private String code;
	private String formation;
	private String resultat;
	private String mention;
	
	public ExportDossierCursusInterne() {
		super();
	}

	public ExportDossierCursusInterne(CandidatCursusInterne cursus) {
		if (cursus != null){
			if (cursus.getAnneeUnivCursusInterne()!=null){
				this.annee = String.valueOf(cursus.getAnneeUnivCursusInterne());
			}else{
				this.annee = "";
			}
			this.code = MethodUtils.formatToExport(cursus.getCodVetCursusInterne());
			this.formation = MethodUtils.formatToExport(cursus.getLibCursusInterne());
			if (cursus.getSiScolTypResultat()!=null){
				this.resultat = cursus.getSiScolTypResultat().getLibTre();
			}else{
				this.resultat = "";
			}
			if (cursus.getSiScolMention()!=null){
				this.mention = cursus.getSiScolMention().getLibMen();
			}else{
				this.mention = "";
			}
		}else{
			this.annee = "";
			this.code = "";
			this.formation = "";
			this.resultat = "";
			this.mention = "";
		}
	}
}
