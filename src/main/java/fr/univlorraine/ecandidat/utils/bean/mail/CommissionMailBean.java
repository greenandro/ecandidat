package fr.univlorraine.ecandidat.utils.bean.mail;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** Class pour l'envoie de mail
 * @author Kevin
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class CommissionMailBean extends MailBean{
	
	/**serialVersionUID**/
	private static final long serialVersionUID = -6316282590469537980L;

	private String libelle;
	private String adresse;
	private String mail;
	private String tel;
	private String fax;
	private String commentaireRetour;
	
	public CommissionMailBean() {
		super();
	}
	
}
