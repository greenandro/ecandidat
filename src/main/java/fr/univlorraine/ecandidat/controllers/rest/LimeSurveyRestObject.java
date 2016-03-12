package fr.univlorraine.ecandidat.controllers.rest;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

/**
 * Ojbet Rest de retour LimeSurvey
 * @author Kevin Hergalant
 *
 */
@Data
public class LimeSurveyRestObject {
	
	private String method;
	private Map<String,Object> params;
	private Integer id;
	
	public LimeSurveyRestObject(String method) {
		super();
		this.method = method;
		this.id = 1;
		params = new LinkedHashMap<String,Object>();
	}
	
	public void addParameter(String key, Object value){
		params.put(key,value);
	}
}
