package fr.univlorraine.ecandidat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;

@Repository
public interface TypeStatutPieceRepository extends JpaRepository<TypeStatutPiece, String> {

	TypeStatutPiece findByCodTypStatutPiece(String codTypStatutPiece);

}
