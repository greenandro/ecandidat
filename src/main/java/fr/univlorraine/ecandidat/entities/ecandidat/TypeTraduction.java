package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the type_traduction database table.
 * 
 */
@Entity
@Table(name="type_traduction")
@Data @EqualsAndHashCode(of="codTypTrad")
public class TypeTraduction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_typ_trad", nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String codTypTrad;

	@Column(name="length_typ_trad", nullable=false)
	@NotNull
	private Integer lengthTypTrad;

	@Column(name="lib_typ_trad", nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String libTypTrad;

	//bi-directional many-to-one association to I18n
	@OneToMany(mappedBy="typeTraduction")
	private List<I18n> i18ns;

	public TypeTraduction() {
		super();
	}
	
	public TypeTraduction(String codTypTrad) {
		this();
		this.codTypTrad = codTypTrad;
	}

	public TypeTraduction(String codTypTrad,
			String libTypTrad, Integer lengthTypTrad) {
		super();
		this.lengthTypTrad = lengthTypTrad;
		this.libTypTrad = libTypTrad;
		this.codTypTrad = codTypTrad;
	}
	
	
}