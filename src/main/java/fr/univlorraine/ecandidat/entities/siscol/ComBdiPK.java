package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the apo_com_bdi database table.
 * 
 */
@Data @EqualsAndHashCode(of={"codCom","codBdi"})
@Embeddable
@ToString(of={"codCom","codBdi"})
public class ComBdiPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="COD_COM")
	private String codCom;

	@Column(name="COD_BDI")
	private String codBdi;
}