package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;


/**
 * The persistent class for the apo_com_bdi database table.
 * 
 */
@Entity
@Table(name="com_bdi")
@Data
public class ComBdi implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ComBdiPK id;
}