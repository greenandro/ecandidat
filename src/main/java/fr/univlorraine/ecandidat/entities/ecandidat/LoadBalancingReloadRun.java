package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;


/**
 * The persistent class for the load_balancing_reload_run database table.
 * 
 */
@Entity @Data
@Table(name="load_balancing_reload_run")
public class LoadBalancingReloadRun implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LoadBalancingReloadRunPK id;

	public LoadBalancingReloadRun(LoadBalancingReloadRunPK id) {
		super();
		this.id = id;
	}
	
	public LoadBalancingReloadRun() {
		super();
	}
}