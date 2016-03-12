package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the parametre database table.
 * 
 */
@Entity @EntityListeners(EntityPushEntityListener.class)
@Table(name="parametre")
@Data @EqualsAndHashCode(of="codParam")
public class Parametre implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_param", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String codParam;

	@Column(name="lib_param", nullable=false, length=200)
	@Size(max = 200) 
	@NotNull
	private String libParam;

	@Column(name="val_param", nullable=false, length=100)
	@Size(max = 100)
	@NotNull
	private String valParam;
	
	@Column(name="typ_param", nullable=false, length=20)
	@Size(max = 20)
	@NotNull
	private String typParam;

	public Parametre() {
		super();
	}

	public Parametre(String codParam, String libParam, String valParam,
			String typParam) {
		super();
		this.codParam = codParam;
		this.libParam = libParam;
		this.valParam = valParam;
		this.typParam = typParam;
	}
}