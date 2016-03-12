package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;

@Repository
public interface SiScolEtablissementRepository extends JpaRepository<SiScolEtablissement, String> {

	@Query("select distinct etab from SiScolEtablissement etab where etab.siScolCommune.codCom = :codCom order by etab.libEtb")
	List<SiScolEtablissement> getEtablissementByCommune(@Param("codCom") String codCom);

}
