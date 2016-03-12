package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the opi_attente database table.
 * 
 */
@Entity
@Table(name="opi")
@Data @EqualsAndHashCode(of="idCand")
public class Opi implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id_cand", nullable=false)
	private Integer idCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_cre_opi", nullable=false)
	@NotNull
	private LocalDateTime datCreOpi;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_passage_opi")
	private LocalDateTime datPassageOpi;

	//bi-directional one-to-one association to Candidature
	@OneToOne
	@JoinColumn(name="id_cand", insertable=false, updatable=false)
	private Candidature candidature;
	
	@PrePersist
	private void onPrePersist() {
		this.datCreOpi = LocalDateTime.now();
	}

	public Opi(Candidature candidature) {
		this.candidature = candidature;
		this.idCand = candidature.getIdCand();
	}

	public Opi() {
		super();
	}
}