package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Faq;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Integer> {

	List<Faq> findAllByOrderByOrderFaqAsc();
}
