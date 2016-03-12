package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierCursusExterne implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 6111120936286501453L;

	private String annee;
	private String code;
	private String etablissement;
	private String formation;
	private String resultat;
	private String mention;
	
	public ExportDossierCursusExterne() {
		super();
	}

	public ExportDossierCursusExterne(CandidatCursusPostBac cursus, String libCursus) {
		if (cursus != null){
			if (cursus.getAnneeUnivCursus()!=null){
				this.annee = String.valueOf(cursus.getAnneeUnivCursus());
			}else{
				this.annee = "";
			}
			if (cursus.getSiScolDipAutCur()!=null){
				this.code = cursus.getSiScolDipAutCur().getLibDac();
			}else{
				this.code = "";
			}
			if (cursus.getSiScolEtablissement()!=null){
				this.etablissement = cursus.getSiScolEtablissement().getLibEtb();
			}else{
				this.etablissement = "";
			}
			this.formation = MethodUtils.formatToExport(cursus.getLibCursus());
			this.resultat = libCursus;
			/*if (cursus.getTemObtenuCursus()==null || !cursus.getTemObtenuCursus()){
				this.resultat = libNon;
			}else{
				this.resultat = libOui;
			}*/
			if (cursus.getSiScolMention()!=null){
				this.mention = cursus.getSiScolMention().getLibMen();
			}else{
				this.mention = "";
			}
		}else{
			this.annee = "";
			this.code = "";
			this.etablissement = "";
			this.formation = "";
			this.resultat = "";
			this.mention = "";
		}
	}
}
