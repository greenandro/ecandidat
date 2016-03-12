package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;


/**
 * The persistent class for the schema_version database table.
 * 
 */
@Entity
@Table(name="schema_version")
@Data
public class SchemaVersion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	private String version;

	@Column
	private String description;
	
	@Column(name="version_rank")
	private String versionRank;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="installed_on")
	private LocalDateTime installedOn;

	@Column
	private String script;

	@Column
	private Boolean success;

}