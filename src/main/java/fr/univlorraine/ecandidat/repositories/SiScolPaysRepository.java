package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;

@Repository
public interface SiScolPaysRepository extends JpaRepository<SiScolPays, String> {
	
	List<SiScolPays> findByCodPayNotOrderByLibPay(String codPays);

	SiScolPays findByCodPay(String paysCodFrance);
}
