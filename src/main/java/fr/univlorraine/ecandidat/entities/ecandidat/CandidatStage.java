package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the candidat_stage database table.
 * 
 */
@Entity
@Table(name="candidat_stage")
@Data @EqualsAndHashCode(of="idStage")
@ToString(exclude={"candidat"})
public class CandidatStage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_stage", nullable=false)
	private Integer idStage;

	@Column(name="descriptif_stage", nullable=false, length=500)
	@Size(max = 500) 
	@NotNull
	private String descriptifStage;

	@Column(name="duree_stage", nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String dureeStage;

	@Column(name="nb_h_sem_stage")
	private Integer nbHSemStage;

	@Column(name="organisme_stage", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String organismeStage;

	@Column(name="annee_stage", nullable=false)
	@NotNull
	private Integer anneeStage;

	//bi-directional many-to-one association to Candidat
	@ManyToOne
	@JoinColumn(name="id_candidat", nullable=false)
	@NotNull
	private Candidat candidat;
}