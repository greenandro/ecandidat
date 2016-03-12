package fr.univlorraine.ecandidat.entities.siscol;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class WSCursusInterne {
	@Id
	private String codVet;
	private String libVet;
	private String codAnu;
	private String codMen;
	private String codTre;
	
	public WSCursusInterne() {
		super();
	}

	public WSCursusInterne(String codVet, String libVet, String codAnu,
			String codMen, String codTre) {
		super();
		this.codVet = codVet;
		this.libVet = libVet;
		this.codAnu = codAnu;
		this.codMen = codMen;
		this.codTre = codTre;
	}
}
