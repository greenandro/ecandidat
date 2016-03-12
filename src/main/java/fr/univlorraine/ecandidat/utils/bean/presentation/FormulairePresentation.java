package fr.univlorraine.ecandidat.utils.bean.presentation;

import java.io.Serializable;

import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 
 * Ojet de formulaire formatté
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(of={"formulaire"})
public class FormulairePresentation implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 3067467095838475483L;
	
	public static String CHAMPS_ID_FORM = "formulaire";
	public static String CHAMPS_LIB = "libFormulaire";
	public static String CHAMPS_URL = "urlFormulaire";
	public static String CHAMPS_LIB_STATUT = "libStatut";
	public static String CHAMPS_COMMENTAIRE = "commentaire";
	public static String CHAMPS_CONDITIONNEL = "conditionnel";
	public static String CHAMPS_REPONSES = "reponses";
	
	private Formulaire formulaire;
	private String libFormulaire;
	private String urlFormulaire;
	private String codStatut;
	private String libStatut;
	private String commentaire;
	private Boolean conditionnel;
	private String reponses;

	public FormulairePresentation() {
		super();
	}

	public FormulairePresentation(Formulaire formulaire, String libFormulaire, String urlFormulaire, 
			String codStatut, String libStatut, String commentaire, Boolean conditionnel,String reponses) {
		super();
		this.formulaire = formulaire;
		this.libFormulaire = libFormulaire;
		this.urlFormulaire = urlFormulaire;
		this.codStatut = codStatut;
		this.libStatut = libStatut;
		this.commentaire = commentaire;
		this.conditionnel = conditionnel;
		this.reponses = reponses;
	}
	
}
