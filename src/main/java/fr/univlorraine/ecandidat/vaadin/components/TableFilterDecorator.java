package fr.univlorraine.ecandidat.vaadin.components;

import java.util.Locale;

import org.tepi.filtertable.FilterDecorator;
import org.tepi.filtertable.numberfilter.NumberFilterPopupConfig;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.datefield.Resolution;

/**
 * Decorateur de tables filtrées
 * @author Kevin Hergalant
 *
 */
public class TableFilterDecorator implements FilterDecorator {

	/**serialVersionUID**/
	private static final long serialVersionUID = 1L;

	@Override
	public String getEnumFilterDisplayName(Object propertyId, Object value) {		
		return null;
	}

	@Override
	public Resource getEnumFilterIcon(Object propertyId, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
		return value ? "Oui": "Non";
	}

	@Override
	public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
		return value ? FontAwesome.CHECK_SQUARE_O: FontAwesome.SQUARE_O;
	}

	@Override
	public boolean isTextFilterImmediate(Object propertyId) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getTextChangeTimeout(Object propertyId) {
		// TODO Auto-generated method stub
		return 500;
	}

	@Override
	public String getFromCaption() {
		return "Date début :";
	}

	@Override
	public String getToCaption() {
		return "Date fin :";
	}

	@Override
	public String getSetCaption() {
		return "Appliquer filtre";
	}

	@Override
	public String getClearCaption() {
		return "Réinitialiser";
	}

	@Override
	public Resolution getDateFieldResolution(Object propertyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDateFormatPattern(Object propertyId) {
		return "dd/MM/yyyy";
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAllItemsVisibleString() {
		// TODO Auto-generated method stub
		return "Tous";
	}

	@Override
	public NumberFilterPopupConfig getNumberFilterPopupConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean usePopupForNumericProperty(Object propertyId) {
		// TODO Auto-generated method stub
		return false;
	}

}
