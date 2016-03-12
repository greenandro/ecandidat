package fr.univlorraine.ecandidat.vaadin.components;

import com.vaadin.server.StreamResource;

public class CustomStreamResource extends StreamResource{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2097637267098980047L;

	public CustomStreamResource(StreamSource streamSource, String filename) {
		super(streamSource, filename);
		setMIMEType("application/force-download;charset=UTF-8");
		setCacheTime(0);		
	}

}
