package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the i18n_traduction database table.
 * 
 */
@Data @EqualsAndHashCode(of={"idI18n","codLangue"})
@Embeddable
@ToString(of={"idI18n","codLangue"})
public class I18nTraductionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="id_i18n", nullable=false)
	@NotNull
	private Integer idI18n;

	@Column(name="cod_langue", nullable=false, length=5)
	@Size(max = 5) 
	@NotNull
	private String codLangue;

	public I18nTraductionPK() {
	}
	
	public I18nTraductionPK(Integer idI18n, String codLangue) {
		super();
		this.idI18n = idI18n;
		this.codLangue = codLangue;
	}
}