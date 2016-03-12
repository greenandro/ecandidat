package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the batch_run database table.
 * 
 */
@Entity
@Table(name="batch_run")
@Data @EqualsAndHashCode(of="datLastCheckRun")
public class BatchRun implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_last_check_run", nullable=false)
	@NotNull
	private LocalDateTime datLastCheckRun;

	public BatchRun() {
		super();
	}

	public BatchRun(LocalDateTime datLastCheckRun) {
		super();
		this.datLastCheckRun = datLastCheckRun;
	}
}