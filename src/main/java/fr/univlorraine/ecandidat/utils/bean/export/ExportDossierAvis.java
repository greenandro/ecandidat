package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierAvis implements Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = -4794933096190467022L;

	private String libelle;
	private String type;
	private Integer order;
	
	public ExportDossierAvis() {
		super();
	}

	public ExportDossierAvis(String libelle, String type) {
		this.libelle = libelle;
		this.type = type;
		if (type.equals("FA")){
			this.order = 1;
		}else if (type.equals("PR")){
			this.order = 2;
		}else if (type.equals("LA")){
			this.order = 3;
		}else if (type.equals("LC")){
			this.order = 4;
		}else if (type.equals("DE")){
			this.order = 5;
		}else{
			this.order = 6;
		}
	}
}
