package fr.univlorraine.ecandidat.utils.bean.mail;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** Class pour l'envoie de mail pôur les compteMinima
 * @author Kevin
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class CptMinMailBean extends MailBean{

	/**serialVersionUID**/
	private static final long serialVersionUID = -1505279031302585086L;
	
	private String prenom;
	private String nom;
	private String numDossierOpi;
	private String password;
	private String lienValidation;
	private String libelleCampagne;
	private String jourDestructionCptMin;
	
	public CptMinMailBean(String prenom, String nom,
			String numDossierOpi, String password, String lienValidation,
			String libelleCampagne, String jourDestructionCptMin) {
		super();
		this.prenom = prenom;
		this.nom = nom;
		this.numDossierOpi = numDossierOpi;
		this.password = password;
		this.lienValidation = lienValidation;
		this.libelleCampagne = libelleCampagne;
		this.jourDestructionCptMin = jourDestructionCptMin;
	}	
}
