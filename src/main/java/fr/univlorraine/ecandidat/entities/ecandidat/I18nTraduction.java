package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the i18n_traduction database table.
 * 
 */
@Entity
@Table(name="i18n_traduction")
@Data @EqualsAndHashCode(of="id")
@ToString(exclude="i18n")
public class I18nTraduction implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private I18nTraductionPK id;

	@Lob
	@Column(name="val_trad", nullable=false, columnDefinition="TEXT")
	@NotNull
	private String valTrad;

	//bi-directional many-to-one association to I18n
	@ManyToOne
	@JoinColumn(name="id_i18n", nullable=false, insertable=false, updatable=false)
	@NotNull
	private I18n i18n;

	//bi-directional many-to-one association to Langue
	@ManyToOne
	@JoinColumn(name="cod_langue", nullable=false, insertable=false, updatable=false)
	@NotNull
	private Langue langue;

	public I18nTraduction() {
		super();
	}

	public I18nTraduction(String valTrad, I18n i18n, Langue langue) {
		super();
		if (i18n!=null){
			this.id = new I18nTraductionPK(i18n.getIdI18n(),langue.getCodLangue());
			this.i18n = i18n;
		}		
		this.valTrad = valTrad;		
		this.langue = langue;
	}	

}