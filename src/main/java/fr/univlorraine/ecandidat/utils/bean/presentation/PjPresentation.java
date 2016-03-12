package fr.univlorraine.ecandidat.utils.bean.presentation;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;

/** 
 * Ojet de PJ formatté
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(of={"pieceJustif"})
public class PjPresentation implements Serializable {
	/**serialVersionUID**/
	private static final long serialVersionUID = 2189408161277446146L;
	
	public static String CHAMPS_CHECK = "check";
	public static String CHAMPS_ID_PJ = "pieceJustif";
	public static String CHAMPS_LIB_PJ = "libPj";
	public static String CHAMPS_FILE_PJ = "filePj";
	public static String CHAMPS_LIB_STATUT = "libStatut";
	public static String CHAMPS_COMMENTAIRE = "commentaire";
	public static String CHAMPS_CONDITIONNEL = "conditionnel";
	
	private PieceJustif pieceJustif;
	private Boolean check;
	private String libPj;
	private Fichier filePj;
	private String codStatut;
	private String libStatut;
	private String commentaire;
	private Boolean pJConditionnel;
	
	public PjPresentation(PieceJustif pieceJustif, String libPj,
			Fichier filePj, String codStatut, String libStatut,
			String commentaire, Boolean pJConditionnel) {
		super();
		this.pieceJustif = pieceJustif;
		this.libPj = libPj;
		this.filePj = filePj;
		this.codStatut = codStatut;
		this.libStatut = libStatut;
		this.commentaire = commentaire;
		this.check = false;
		this.pJConditionnel = pJConditionnel;
	}

	public PjPresentation() {
		super();
	}
	
}
