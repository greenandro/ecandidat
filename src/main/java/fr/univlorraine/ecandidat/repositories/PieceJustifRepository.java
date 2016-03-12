package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;

@Repository
public interface PieceJustifRepository extends JpaRepository<PieceJustif, Integer> {
	
	public PieceJustif findByCodPj(String codPj);

	public List<PieceJustif> findByCentreCandidatureIdCtrCand(Integer idCtrCand);

	public List<PieceJustif> findByCentreCandidatureIdCtrCandAndTesPjAndTemCommunPj(Integer idCtrCand, Boolean enService, Boolean commun);
	
	public List<PieceJustif> findByTesPjAndTemCommunPj(Boolean enService, Boolean commun);
	
}
