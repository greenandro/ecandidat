package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;


/**
 * The persistent class for the siScol_com_bdi database table.
 * 
 */
@Entity
@Table(name="siscol_com_bdi")
@Data
public class SiScolComBdi implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private SiScolComBdiPK id;

	public SiScolComBdi() {
		super();
	}
	
	public SiScolComBdi(String codCom, String codBdi){
		super();
		this.id = new SiScolComBdiPK(codCom,codBdi);
	}
}