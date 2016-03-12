package fr.univlorraine.ecandidat.utils.bean.mail;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** Class pour l'envoie de mail pôur les compteMinima
 * @author Kevin
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class CandidatureMailBean extends MailBean{
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 2313911982990891477L;

	private String libelleCampagne;
	private CandidatMailBean candidat;
	private FormationMailBean formation;
	private CommissionMailBean commission;
	
	public CandidatureMailBean(String libelleCampagne, CandidatMailBean candidat,
			FormationMailBean formation,CommissionMailBean commission) {
		super();
		this.libelleCampagne = libelleCampagne;
		this.candidat = candidat;
		this.formation = formation;
		this.commission = commission;
	}
	
}
