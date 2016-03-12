package fr.univlorraine.ecandidat.utils.bean.mail;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** Class pour l'envoie de mail pôur les compteMinima
 * @author Kevin
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AvisMailBean extends MailBean{
	
	/**serialVersionUID**/
	private static final long serialVersionUID = -85939988354207498L;
	
	private String motif;
	private String commentaire;
	private String complementPreselect;
	private String complementAppel;
	private String rang;
	
	
	public AvisMailBean(String motif, String commentaire, String complementPreselect, String complementAppel, String rang) {
		super();
		this.motif = motif;		
		this.commentaire = commentaire;
		this.complementPreselect = complementPreselect;
		this.complementAppel = complementAppel;
		this.rang = rang;
	}	
}
