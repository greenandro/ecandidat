package fr.univlorraine.ecandidat.utils.bean.presentation;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Classe de presentation d'un parametre
 * @author Kevin Hergalant
 *
 */
@Data @EqualsAndHashCode(of="code")
public class SimpleBeanPresentation implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String code;
	private String valeur;
	
	public SimpleBeanPresentation() {
		super();
	}
	
	public SimpleBeanPresentation(String code, String valeur) {
		super();
		this.code = code;
		this.valeur = valeur;
	}
	
	public SimpleBeanPresentation(String code) {
		super();
		this.code = code;
	}

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.valeur;
	}
}