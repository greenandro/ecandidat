package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the load_balancing_reload_run database table.
 * 
 */
@Data @EqualsAndHashCode(of={"datLastCheckLbReloadRun","instanceIdLbReloadRun"})
@Embeddable
@ToString(of={"datLastCheckLbReloadRun","instanceIdLbReloadRun"})
public class LoadBalancingReloadRunPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_last_check_lb_reload_run", unique=true, nullable=false)
	@NotNull
	private LocalDateTime datLastCheckLbReloadRun;

	@Column(name="instance_id_lb_reload_run", unique=true, nullable=false, length=20)
	@NotNull
	private String instanceIdLbReloadRun;

	public LoadBalancingReloadRunPK() {
		super();
	}

	public LoadBalancingReloadRunPK(LocalDateTime datLastCheckLbReloadRun, String instanceIdLbReloadRun) {
		super();
		this.datLastCheckLbReloadRun = datLastCheckLbReloadRun;
		this.instanceIdLbReloadRun = instanceIdLbReloadRun;
	}
}