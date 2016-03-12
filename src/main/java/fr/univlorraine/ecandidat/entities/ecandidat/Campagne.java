package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDatePersistenceConverter;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;


/**
 * The persistent class for the campagne database table.
 * 
 */
@Entity
@Table(name="campagne") @EntityListeners(EntityPushEntityListener.class)
@Data @EqualsAndHashCode(of="idCamp")
public class Campagne implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_camp", nullable=false)
	private Integer idCamp;
	
	@Column(name="cod_camp", nullable=false, unique=true, length=20)
	@Size(max = 20) 
	@NotNull
	private String codCamp;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_deb_camp", nullable=false)
	@NotNull
	private LocalDate datDebCamp;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_fin_camp", nullable=false)
	@NotNull
	private LocalDate datFinCamp;

	@Column(name="lib_camp", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String libCamp;

	@Column(name="tes_camp", nullable=false)
	@NotNull
	private Boolean tesCamp;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_activat_prev_camp")
	private LocalDateTime datActivatPrevCamp;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_activat_effec_camp")
	private LocalDateTime datActivatEffecCamp;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_archiv_camp")
	private LocalDateTime datArchivCamp;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_destruct_effec_camp")
	private LocalDateTime datDestructEffecCamp;
	
	//bi-directional many-to-one association to Campagne
	@ManyToOne
	@JoinColumn(name="archiv_id_camp")
	private Campagne campagneArchiv;

	//bi-directional many-to-one association to CompteMinima
	@OneToMany(mappedBy="campagne")
	private List<CompteMinima> compteMinimas;

	public Campagne() {
		super();
		this.tesCamp = false;
	}
	
}