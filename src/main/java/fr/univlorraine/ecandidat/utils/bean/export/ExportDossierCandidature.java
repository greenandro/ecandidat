package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import lombok.Data;

/**
 * Objet contenant les infos d'une candidature pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierCandidature implements Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = 9118214743245453712L;


	private String campagne;
	private String commission;
	private String adresseCommission;
	private String commentaireRetour;
	private String libelleFormation;
	private String codeFormation;

	public ExportDossierCandidature() {
		super();
	}

	public ExportDossierCandidature(String campagne, String commission, String adresseCommission, Formation formation, String commentaireRetour) {
		super();
		this.campagne = campagne;
		this.commission = commission;
		this.adresseCommission = adresseCommission;
		this.codeFormation = formation.getCodForm();
		this.libelleFormation = formation.getLibForm();
		this.commentaireRetour = commentaireRetour;
	}	
}
