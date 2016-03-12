package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the batch_histo database table.
 * 
 */
@Entity
@Table(name="batch_histo")
@Data @EqualsAndHashCode(of="idBatchHisto")
public class BatchHisto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_batch_histo", nullable=false)
	private Integer idBatchHisto;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="date_deb_batch_histo")
	private LocalDateTime dateDebBatchHisto;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="date_fin_batch_histo")
	private LocalDateTime dateFinBatchHisto;

	@Column(name="state_batch_histo", length=10)
	@Size(max = 10) 
	private String stateBatchHisto;

	//bi-directional many-to-one association to Batch
	@ManyToOne
	@JoinColumn(name="cod_batch")
	@NotNull
	private Batch batch;
}