package fr.univlorraine.ecandidat.utils.bean.odf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** Ojet d'affichage d'offre de formation : le centre de candidature
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(of={"idCtrCand"})
public class OdfCtrCand implements Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = -2310540796434503996L;
	
	private String title;
	private Integer idCtrCand;
	private List<OdfDiplome> listeDiplome;
	private Boolean modeCandidature;
	
	public OdfCtrCand(Integer idCtrCand, String title, Boolean modeCandidature) {
		super();
		this.idCtrCand = idCtrCand;
		this.title = title;
		this.listeDiplome = new ArrayList<OdfDiplome>();
		this.modeCandidature = modeCandidature;
	}
}
