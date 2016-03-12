package fr.univlorraine.ecandidat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;

@Repository
public interface TypeStatutRepository extends JpaRepository<TypeStatut, String> {

	TypeStatut findByCodTypStatut(String codTypStatut);
}
