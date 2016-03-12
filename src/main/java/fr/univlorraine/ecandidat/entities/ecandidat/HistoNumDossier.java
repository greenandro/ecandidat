package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.domain.Persistable;

import lombok.Data;


/**
 * The persistent class for the histo_num_dossier database table.
 * 
 */
@Entity @Data
@Table(name="histo_num_dossier")
public class HistoNumDossier implements Serializable, Persistable<String> {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="num_dossier", unique=true, nullable=false, updatable=true ,length=8)
	@Size(max = 8) 
	@NotNull
	private String numDossier;
	
	@Column(name="cod_camp", nullable=false, updatable=true, length=20)
	@Size(max = 20) 
	@NotNull
	private String codCamp;

	public HistoNumDossier() {
		super();
	}

	public HistoNumDossier(String numDossier, String codCamp) {
		super();
		this.numDossier = numDossier;
		this.codCamp = codCamp;
	}

	@Override
	public String getId() {
		return numDossier;
	}

	@Override
	public boolean isNew() {
		return true;
	}
}