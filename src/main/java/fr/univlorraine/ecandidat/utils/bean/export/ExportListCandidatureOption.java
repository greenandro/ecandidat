package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** 
 * Ojet servant a l'option d'export
 * @author Kevin Hergalant
 *
 */
@Data @EqualsAndHashCode(of={"id"})
public class ExportListCandidatureOption implements Serializable {
	/**serialVersionUID**/
	private static final long serialVersionUID = 7536089795996479334L;
	
	private String id;
	private String caption;
	
	public ExportListCandidatureOption(String id, String caption) {
		super();
		this.id = id;
		this.caption = caption;
	}
	
	public ExportListCandidatureOption() {
		super();
	}

	public ExportListCandidatureOption(String id) {
		super();
		this.id = id;
	}
	
}
