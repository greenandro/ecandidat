package fr.univlorraine.ecandidat.vaadin.form;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * Converter de date to localDate
 * @author Kevin Hergalant
 *
 */
public class LocalDateToDateConverter implements Converter<Date,LocalDate>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -4277100918426910389L;

	/**
	 * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
	 */
	@Override
	public LocalDate convertToModel(Date value,
			Class<? extends LocalDate> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null){
			return null;
		}		
		Instant instant = Instant.ofEpochMilli(value.getTime());
		LocalDate res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
		return res;
	}

	/**
	 * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
	 */
	@Override
	public Date convertToPresentation(LocalDate value,
			Class<? extends Date> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null){
			return null;
		}
		Instant instant = value.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		Date res = Date.from(instant);
		return res;
	}

	/**
	 * @see com.vaadin.data.util.converter.Converter#getModelType()
	 */
	@Override
	public Class<LocalDate> getModelType() {
		// TODO Auto-generated method stub
		return LocalDate.class;
	}

	/**
	 * @see com.vaadin.data.util.converter.Converter#getPresentationType()
	 */
	@Override
	public Class<Date> getPresentationType() {
		// TODO Auto-generated method stub
		return Date.class;
	}

}
