package fr.univlorraine.ecandidat.utils.bean.presentation;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Objet servant à la présentation basique de donnée
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(of={"code"})
public class SimpleTablePresentation implements Serializable {
	/**serialVersionUID**/
	private static final long serialVersionUID = -1362401047200086315L;
	
	private Integer order;
	private String code;
	private String title;
	private Object value;
	private String shortValue;
	private LocalDateTime date;
	
	public final static String champsOrder = "order";
	public final static String champsCode = "code";
	public final static String champsTitle = "title";
	public final static String champsValue = "value";
	public final static String champsDate = "date";
	public final static String champsAction = "action";
	
	public SimpleTablePresentation(Integer order,String code,String title, Object value) {
		super();
		this.order = order;
		this.code = code;
		this.title = title;
		this.value = value;
	}
	
	public SimpleTablePresentation(String code,String title, Object value) {
		super();
		this.code = code;
		this.title = title;
		this.value = value;
	}

	public SimpleTablePresentation(Integer order,String code, 
			String title, String value,	LocalDateTime date) {
		this(order,code,title,value);
		this.date = date;
	}

	public SimpleTablePresentation(String title, Object value) {
		this.title = title;
		this.value = value;
	}
	
}
