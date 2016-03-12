package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the civilite database table.
 * 
 */
@Entity
@Table(name="civilite")
@Data @EqualsAndHashCode(of="codCiv")
public class Civilite implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_civ")
	private String codCiv;

	@Column(name="cod_apo")
	private String codApo;

	@Column(name="lib_civ")
	private String libCiv;

	//bi-directional many-to-one association to Candidat
	@OneToMany(mappedBy="civilite")
	private List<Candidat> candidats;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.codCiv;
	}
	
	public Civilite() {
		super();
	}
	
	public Civilite(String codCiv, String libCiv, String codApo) {
		super();
		this.codCiv = codCiv;
		this.codApo = codApo;
		this.libCiv = libCiv;
	}	
}