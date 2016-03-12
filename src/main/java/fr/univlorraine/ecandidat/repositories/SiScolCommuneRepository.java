package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;

@Repository
public interface SiScolCommuneRepository extends JpaRepository<SiScolCommune, String> {
	
	@Query("select distinct c from SiScolCommune c, SiScolComBdi b where c.codCom = b.id.codCom and b.id.codBdi=:codePostal")
	public List<SiScolCommune> getCommuneByCodePostal(@Param("codePostal") String codePostal);

	@Query("select distinct c from SiScolCommune c, SiScolEtablissement etab where c.siScolDepartement.codDep = :codDep and etab.siScolDepartement.codDep = :codDep and etab.siScolCommune.codCom = c.codCom ")
	public List<SiScolCommune> getCommuneByDepartement(@Param("codDep") String codDep);
}
