package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the formulaire_cand database table.
 * 
 */
@Data @EqualsAndHashCode(of={"idFormulaire","idCand"})
@Embeddable
@ToString(of={"idFormulaire","idCand"})
public class FormulaireCandPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="id_formulaire", nullable=false)
	@NotNull
	private Integer idFormulaire;

	@Column(name="id_cand", nullable=false)
	@NotNull
	private Integer idCand;

	public FormulaireCandPK() {
	}
	
	public FormulaireCandPK(Integer idFormulaire, Integer idCand) {
		this.idFormulaire = idFormulaire;
		this.idCand = idCand;
	}
}