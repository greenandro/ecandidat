package fr.univlorraine.ecandidat.utils;

import java.io.Serializable;
import java.util.List;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPro;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatStage;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.utils.bean.presentation.FormulairePresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation;

/**
 * Class des listeners d'un candidat
 * @author Kevin Hergalant
 *
 */
public class ListenerUtils {

	/** Listener pour la mise a jour du candidat
	 * @author Kevin
	 *
	 */
	public interface InfoPersoListener {
		/** L'info perso a été modifié
		 * @param candidat
		 * @param langueChanged
		 */
		void infoPersoModified(Candidat candidat, Boolean langueChanged);
	}
	
	/** Listener pour la mise a jour de l'adresse
	 * @author Kevin
	 *
	 */
	public interface AdresseListener {
		/** L'adresse a été modifiée
		 * @param candidat
		 */
		void adresseModified(Candidat candidat);
	}
	
	/** Listener pour la mise a jour du cursus post bac
	 * @author Kevin
	 *
	 */
	public interface CandidatCursusExterneListener {
		/** Les cursus ont été modifié
		 * @param list
		 */
		void cursusModified(List<CandidatCursusPostBac> list);
	}

	/** Listener pour la mise a jour du parcours pro
	 * @author Kevin
	 *
	 */
	public interface CandidatProListener {
		/** Les cursus pro ont été modifié
		 * @param candidatCursusPros
		 */
		void cursusProModified(List<CandidatCursusPro> candidatCursusPros);
	}
	
	/** Listener pour la mise a jour du stage
	 * @author Kevin
	 *
	 */
	public interface CandidatStageListener {
		/** Les stages ont été modifié
		 * @param candidatStage
		 */
		void stageModified(List<CandidatStage> candidatStage);
	}
	
	/** Listener pour la mise a jour du bac
	 * @author Kevin
	 *
	 */
	public interface CandidatBacListener {
		/**Le bac a été modifié
		 * @param bac
		 */
		void bacModified(CandidatBacOuEqu bac);
	}
	
	/** Listener pour la mise a jour d'une formation pro
	 * @author Kevin
	 *
	 */
	public interface CandidatFormationProListener {
		/** Les formations pro ont été modifié
		 * @param candidatCursusPros
		 */
		void formationProModified(List<CandidatCursusPro> candidatCursusPros);
	}
	
	/**Listener pour la mise a jour d'une candidature
	 * @author Kevin Hergalant
	 *
	 */
	public interface CandidatureListener {
		/** Une pj a été modifié
		 * @param pieceJustif
		 * @param candidature
		 */
		void pjModified(PjPresentation pieceJustif, Candidature candidature);
		/** Un formulaire  a été modifié
		 * @param formulaire
		 * @param candidature
		 */
		void formulaireModified(FormulairePresentation formulaire, Candidature candidature);
		/** Candidature supprimée
		 * @param candidature
		 */
		void candidatureDeleted(Candidature candidature);
		/** Candidature annulée
		 * @param candidature
		 */
		void candidatureCanceled(Candidature candidature);
		/** Les pjs ont été modifiées
		 * @param listePj
		 * @param candidatureSave
		 */
		void pjsModified(List<PjPresentation> listePj, Candidature candidatureSave);
		/** Le statut a été modifié
		 * @param candidatureSave
		 */
		void infosCandidatureModified(Candidature candidatureSave);
		/**
		 * Le dossier candidat doit être ouvert
		 */
		void openCandidat();
		/** L'annulation a été annulée ;)
		 * @param candidatureSave
		 */
		void candidatureAnnulCanceled(Candidature candidatureSave);
		
		/** La candidature a été transmise
		 * @param candidatureSave
		 */
		void transmissionDossier(Candidature candidatureSave);
	}
	
	/**Listener pour la mise a jour d'une candidature
	 * @author Kevin Hergalant
	 *
	 */
	public interface CandidatureCandidatViewListener {
		/** Candidature annulée
		 * @param candidature
		 */
		void candidatureCanceled(Candidature candidature);
		
		/** Le statut du dossier a été modifié
		 * @param candidatureSave
		 */
		void statutDossierModified(Candidature candidatureSave);
	}
	
	/** Listener pour la mise a jour d'un candidat
	 * @author Kevin
	 *
	 */
	public interface CandidatAdminListener {
		/** Le compte a minima a été modifié
		 * @param cptMin
		 */
		void cptMinModified(CompteMinima cptMin);
	}
	
	/**
	 * Listener pour l'offre de formation
	 * @author Kevin Hergalant
	 *
	 */
	public interface OdfListener {
		/**
		 * L'offre de formation a été mdoifiée
		 */
		void updateOdf();
	}
	
	/**
	 * Interface pour le listener de changement de mode de maintenance
	 */
	public interface MaintenanceListener extends Serializable {

		/**
		 * Appelé lorsque le mode de maintenance est modifié
		 */
		public void changeModeMaintenance();

	}
}
