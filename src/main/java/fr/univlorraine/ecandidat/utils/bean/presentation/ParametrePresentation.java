package fr.univlorraine.ecandidat.utils.bean.presentation;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;

/**
 * Classe de presentation d'un parametre
 * @author Kevin Hergalant
 *
 */
@Data @EqualsAndHashCode(of="codParam")
public class ParametrePresentation implements Serializable {

	/** serialVersionUID **/
	private static final long serialVersionUID = 5079730230151358383L;

	@NotNull
	private String codParam;
	
	@NotNull
	private String libParam;

	@NotNull
	private Integer valParamInteger;
	
	@NotNull
	private String valParamBoolean;

	private String valParamString;
	
	public ParametrePresentation() {
	}

	/** Créé un parametre de presentation pour un parametre
	 * @param parametre
	 */
	public ParametrePresentation(Parametre parametre) {
		this.codParam = parametre.getCodParam();
		this.libParam = parametre.getLibParam();
		if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_BOOLEAN)){
			this.valParamBoolean = parametre.getValParam();
		}else if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_INTEGER)){
			this.valParamInteger = Integer.valueOf(parametre.getValParam());
		}else if (parametre.getTypParam().startsWith(NomenclatureUtils.TYP_PARAM_STRING)){
			this.valParamString = parametre.getValParam();
		}
	}
}