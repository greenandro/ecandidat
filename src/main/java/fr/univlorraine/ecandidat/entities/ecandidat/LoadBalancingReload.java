package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;


/**
 * The persistent class for the load_balancing_reload database table.
 * 
 */
@Entity @Data
@Table(name="load_balancing_reload")
public class LoadBalancingReload implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_data_lb_reload", unique=true, nullable=false, length=20)
	private String codDataLbReload;

	@Column(name="dat_cre_lb_reload", nullable=false)
	@NotNull
	private LocalDateTime datCreLbReload;

	public LoadBalancingReload() {
		super();
	}

	public LoadBalancingReload(String codDataLbReload, LocalDateTime datCreLbReload) {
		super();
		this.codDataLbReload = codDataLbReload;
		this.datCreLbReload = datCreLbReload;
	}
}