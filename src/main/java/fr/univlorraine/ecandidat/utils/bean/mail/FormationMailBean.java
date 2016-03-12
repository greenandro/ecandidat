package fr.univlorraine.ecandidat.utils.bean.mail;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** Class pour l'envoie de mail pôur les compteMinima
 * @author Kevin
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class FormationMailBean extends MailBean{
	
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 8852717651034015914L;

	private String code;
	private String libelle;
	private String codEtpVetApo;
	private String codVrsVetApo;
	private String libApo;
	private String motCle;
	private String datPubli;
	private String datPreAnalyse;
	private String datRetour;
	private String datJury;
	private String datConfirm;
	private String datDebDepot;
	private String datFinDepot;
	
	public FormationMailBean() {
		super();
	}
	
}
