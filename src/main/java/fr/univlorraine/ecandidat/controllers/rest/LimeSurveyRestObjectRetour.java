package fr.univlorraine.ecandidat.controllers.rest;

import lombok.Data;

/** Classe de retour d'un objet LimeSurvey
 * @author Kevin Hergalant
 *
 */
@Data
public class LimeSurveyRestObjectRetour {
	private Integer id;
	private String result;
	private String error;
}
