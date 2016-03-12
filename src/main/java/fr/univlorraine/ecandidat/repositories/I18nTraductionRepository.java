package fr.univlorraine.ecandidat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.I18nTraduction;

@Repository
public interface I18nTraductionRepository extends JpaRepository<I18nTraduction, Integer> {
}
