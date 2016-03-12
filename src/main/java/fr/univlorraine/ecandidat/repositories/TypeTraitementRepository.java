package fr.univlorraine.ecandidat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;

@Repository
public interface TypeTraitementRepository extends JpaRepository<TypeTraitement, String> {

	TypeTraitement findByCodTypTrait(String codTypTrait);
}
