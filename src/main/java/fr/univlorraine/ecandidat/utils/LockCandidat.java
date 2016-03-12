package fr.univlorraine.ecandidat.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;

/** Class de lock d'un candididat
 * @author Kevin Hergalant
 *
 */
@Data @EqualsAndHashCode(of={"cptMin","ressource"})
public class LockCandidat {

	private CompteMinima cptMin;
	private String ressource;
	
	public LockCandidat() {
		super();
	}
	
	public LockCandidat(CompteMinima cptMin, String ressource) {
		super();
		this.cptMin = cptMin;
		this.ressource = ressource;
	}	
}
