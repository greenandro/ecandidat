package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the faq database table.
 * 
 */
@Entity
@Table(name="faq")
@EntityListeners(EntityPushEntityListener.class)
@Data @EqualsAndHashCode(of="idFaq")
public class Faq implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id_faq", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer idFaq;
	
	@Column(name="lib_faq", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String libFaq;
	
	//bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name="id_i18n_reponse_faq", nullable=false)
	@NotNull
	private I18n i18nReponse;
	
	//bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name="id_i18n_question_faq", nullable=false)
	@NotNull
	private I18n i18nQuestion;
	
	@Column(name="order_faq", nullable=false) 
	@NotNull
	private Integer orderFaq;
}