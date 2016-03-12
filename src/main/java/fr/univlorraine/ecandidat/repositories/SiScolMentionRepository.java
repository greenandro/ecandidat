package fr.univlorraine.ecandidat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;

@Repository
public interface SiScolMentionRepository extends JpaRepository<SiScolMention, String> {

}
