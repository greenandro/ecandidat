package fr.univlorraine.ecandidat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.I18n;

@Repository
public interface I18nRepository extends JpaRepository<I18n, Integer> {
}
