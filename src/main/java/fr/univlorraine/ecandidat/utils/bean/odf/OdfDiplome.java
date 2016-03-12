package fr.univlorraine.ecandidat.utils.bean.odf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** Ojet d'affichage d'offre de formation : le diplome
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(of={"id"})
public class OdfDiplome implements Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = 2245122946073015182L;
	
	private String id;
	private String title;
	private String codDip;
	private List<OdfFormation> listeFormation;
	
	public OdfDiplome(String id,String codDip, String title) {
		super();
		this.id = id;
		this.codDip = codDip;
		this.title = title;
		this.listeFormation = new ArrayList<OdfFormation>();
	}
}
