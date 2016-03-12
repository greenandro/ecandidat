package fr.univlorraine.ecandidat.utils.bean.odf;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** Ojet d'affichage d'offre de formation : la formation
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(of={"idFormation"})
public class OdfFormation implements Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = -4207497335767866029L;
	
	private String title;
	private Integer idFormation;
	private String motCle;
	private String dates;
	private LocalDate dateDebut;
	private LocalDate dateFin;
	
	public OdfFormation(String title, Integer idFormation, String motCle, LocalDate dateDebut, LocalDate dateFin) {
		super();
		this.title = title;
		this.idFormation = idFormation;
		this.motCle = motCle;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
	}
}
