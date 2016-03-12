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
 * The primary key class for the siScol_com_bdi database table.
 * 
 */
@Data @EqualsAndHashCode(of={"codCom","codBdi"})
@Embeddable
@ToString(of={"codCom","codBdi"})
public class SiScolComBdiPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="cod_com", nullable=false, length=5)
	@Size(max = 5) 
	@NotNull
	private String codCom;

	@Column(name="cod_bdi", nullable=false, length=5)
	@NotNull
	@Size(max = 5) 
	private String codBdi;

	public SiScolComBdiPK() {
	}
	
	public SiScolComBdiPK(String codCom, String codBdi) {
		super();
		this.codCom = codCom;
		this.codBdi = codBdi;
	}
}