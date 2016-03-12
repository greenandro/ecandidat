package fr.univlorraine.ecandidat.controllers.rest;

import java.util.LinkedHashMap;
import java.util.List;

import lombok.Data;

/**
 * Classe d'objet de retour de LimeSurvey
 * @author Kevin Hergalant
 *
 */
@Data
public class SurveyReponseRoot {
	private List<LinkedHashMap<String,SurveyReponse>> responses;
}
