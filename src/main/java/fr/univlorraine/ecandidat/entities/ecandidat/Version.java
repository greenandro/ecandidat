package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;


/**
 * The persistent class for the version database table.
 * 
 */
@Entity @EntityListeners(EntityPushEntityListener.class)
@Table(name="version")
@Data @EqualsAndHashCode(of="codVersion")
public class Version implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_version", nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String codVersion;

	@Column(name="val_version", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String valVersion;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_version", nullable=false)
	@NotNull
	private LocalDateTime datVersion;

	@PrePersist
	private void onPrePersist() {
		this.datVersion = LocalDateTime.now();
	}
	
	@PreUpdate
	private void onPreUpdate() {
		this.datVersion = LocalDateTime.now();
	}
	
	public Version() {
		super();
	}

	public Version(String valVersion) {
		super();
		this.valVersion = valVersion;
	}

	public Version(String codVersion, String valVersion) {
		super();
		this.codVersion = codVersion;
		this.valVersion = valVersion;
	}
}