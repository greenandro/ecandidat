package fr.univlorraine.ecandidat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCand;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCandPK;

@Repository
public interface FormulaireCandRepository extends JpaRepository<FormulaireCand, FormulaireCandPK> {
	
}
