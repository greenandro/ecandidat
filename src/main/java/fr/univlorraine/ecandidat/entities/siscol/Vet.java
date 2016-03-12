package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.Data;


/**
 * The persistent class for the Vet database table.
 * 
 */
@Entity
@Data
public class Vet implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private VetPK id;
	
	private String libVet;
	private String libTypDip;
}