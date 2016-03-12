package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;


/**
 * The persistent class for the VOEUX_INS database table.
 * 
 */
@Entity
@Data @Table(name="VOEUX_INS")
public class VoeuxIns implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private VoeuxInsPK id;

}