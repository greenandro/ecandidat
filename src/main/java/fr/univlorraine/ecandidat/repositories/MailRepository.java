package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Mail;

@Repository
public interface MailRepository extends JpaRepository<Mail, Integer> {
	Mail findByCodMail(String codMail);

	List<Mail> findByTemIsModeleMail(Boolean isModel);

	List<Mail> findByTypeAvisNotNullAndTesMail(Boolean enService);
}
