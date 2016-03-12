package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.vaadin.server.ThemeResource;

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the langue database table.
 * 
 */
@Entity @EntityListeners(EntityPushEntityListener.class)
@Table(name="langue")
@Data @EqualsAndHashCode(of="codLangue")
public class Langue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_langue", nullable=false, length=5)
	@Size(max = 5) 
	@NotNull
	private String codLangue;

	@Column(name="lib_langue", nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String libLangue;

	@Column(name="tem_defaut_langue", nullable=false)
	@NotNull
	private Boolean temDefautLangue;

	@Column(name="tes_langue", nullable=false)
	@NotNull
	private Boolean tesLangue;
	
	@Transient
	private ThemeResource flag;

	//bi-directional many-to-one association to Candidat
	@OneToMany(mappedBy="langue")
	private List<Candidat> candidats;

	//bi-directional many-to-one association to I18nTraduction
	@OneToMany(mappedBy="langue")
	private List<I18nTraduction> i18nTraductions;

	public Langue() {
		super();
	}	

	public Langue(String codLangue, String libLangue, Boolean temDefautLangue,
			Boolean tesLangue) {
		super();
		this.codLangue = codLangue;
		this.libLangue = libLangue;
		this.temDefautLangue = temDefautLangue;
		this.tesLangue = tesLangue;
	}



	public Langue(String codLangue) {
		super();
		this.codLangue = codLangue;
	}
}