package fr.univlorraine.ecandidat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.univlorraine.ecandidat.entities.ecandidat.Batch;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Faq;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Configuration Entity Push
 * 
 * @author Adrien Colson
 */
@Configuration
public class EntityPushConfig {

	@Bean
	public EntityPusher<Batch> batchEntityPusher() {
		return new EntityPusher<>(Batch.class);
	}
	
	@Bean
	public EntityPusher<Langue> langueEntityPusher() {
		return new EntityPusher<>(Langue.class);
	}
	
	@Bean
	public EntityPusher<Parametre> parametreEntityPusher() {
		return new EntityPusher<>(Parametre.class);
	}
	
	@Bean
	public EntityPusher<Mail> mailEntityPusher() {
		return new EntityPusher<>(Mail.class);
	}
	
	@Bean
	public EntityPusher<DroitProfilInd> droitProfilIndEntityPusher() {
		return new EntityPusher<>(DroitProfilInd.class);
	}
	
	@Bean
	public EntityPusher<DroitProfil> droitProfilEntityPusher() {
		return new EntityPusher<>(DroitProfil.class);
	}
	
	@Bean
	public EntityPusher<TypeDecision> typeDecisionEntityPusher() {
		return new EntityPusher<>(TypeDecision.class);
	}
	
	@Bean
	public EntityPusher<MotivationAvis> motivationAvisEntityPusher() {
		return new EntityPusher<>(MotivationAvis.class);
	}
	
	@Bean
	public EntityPusher<CentreCandidature> centreCandidatureEntityPusher() {
		return new EntityPusher<>(CentreCandidature.class);
	}
	
	@Bean
	public EntityPusher<Campagne> campagneEntityPusher() {
		return new EntityPusher<>(Campagne.class);
	}
	
	@Bean
	public EntityPusher<PieceJustif> pieceJustifEntityPusher() {
		return new EntityPusher<>(PieceJustif.class);
	}
	
	@Bean
	public EntityPusher<Formulaire> formulaireEntityPusher() {
		return new EntityPusher<>(Formulaire.class);
	}
	
	@Bean
	public EntityPusher<Commission> commissionEntityPusher() {
		return new EntityPusher<>(Commission.class);
	}
	
	@Bean
	public EntityPusher<Formation> formationEntityPusher() {
		return new EntityPusher<>(Formation.class);
	}
	
	@Bean
	public EntityPusher<Version> versionEntityPusher() {
		return new EntityPusher<>(Version.class);
	}
	
	@Bean
	public EntityPusher<TypeTraitement> typeTraitementEntityPusher() {
		return new EntityPusher<>(TypeTraitement.class);
	}
	
	@Bean
	public EntityPusher<TypeStatut> typeStatutEntityPusher() {
		return new EntityPusher<>(TypeStatut.class);
	}
	
	@Bean
	public EntityPusher<TypeStatutPiece> typeStatutPieceEntityPusher() {
		return new EntityPusher<>(TypeStatutPiece.class);
	}
	
	@Bean
	public EntityPusher<Faq> faqEntityPusher() {
		return new EntityPusher<>(Faq.class);
	}
	
	@Bean
	public EntityPusher<Candidature> candidatureEntityPusher() {
		return new EntityPusher<>(Candidature.class);
	}
}
