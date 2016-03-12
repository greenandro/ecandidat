package fr.univlorraine.ecandidat.controllers.rest;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Classe d'objet de reponse de LimeSurvey
 * @author Kevin Hergalant
 *
 */

@Data
@EqualsAndHashCode(of="numDossier")
public class SurveyReponse {
	private String id;
	private String submitdate;
	private String lastpage;
	private String startlanguage;
	private String startdate;
	private String datestamp;
    private String numDossier;
    private Map<String,Object> mapReponses;
    
    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
    	mapReponses.put(key, value);
    }

	public SurveyReponse() {
		super();
		mapReponses = new HashMap<String,Object>();
	}
}
