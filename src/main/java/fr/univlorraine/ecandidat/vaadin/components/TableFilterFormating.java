package fr.univlorraine.ecandidat.vaadin.components;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.tepi.filtertable.FilterTable;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty.MethodException;
import com.vaadin.ui.CustomTable;

/** Table filtrée apportant un pattern aux format de date, de double, de boolean
 * @author Kevin
 *
 */
@Configurable(preConstruction=true)
public class TableFilterFormating extends FilterTable{

	/** serialVersionUID **/
	private static final long serialVersionUID = 3460506751703160156L;
	
	@Resource
	private transient DateTimeFormatter formatterDate;
	@Resource
	private transient DateTimeFormatter formatterDateTime;
	private NumberFormat integerFormatter = new DecimalFormat("#");
	
	public TableFilterFormating(String string,Container dataSource) {
		super(string);
		setContainerDataSource(dataSource);
	}
	
	public TableFilterFormating(Container dataSource) {
		super(null);
		setContainerDataSource(dataSource);
	}
	
	public TableFilterFormating() {
		super();
	}

	@Override
	protected String formatPropertyValue(Object rowId, Object colId,Property<?> property) {		
		Object v;
		try{
			v = property.getValue();
		}catch(MethodException e){
			return "";
		}		
        if (v instanceof LocalDate) {        	
        	LocalDate dateValue = (LocalDate) v;        	
    		return formatterDate.format(dateValue);
        }
        else if (v instanceof LocalDateTime) {
        	LocalDateTime dateValue = (LocalDateTime) v;
    		return formatterDateTime.format(dateValue);
        }
        else if (v instanceof Integer) {
        	return integerFormatter.format(v);
        }
        else if (v instanceof Boolean) {
        	Boolean boolValue = (Boolean) v;
            if (boolValue){
            	return "Oui";
            }else{
            	return "Non";
            }
        }
        return super.formatPropertyValue(rowId, colId, property);
	}
	
	public void addBooleanColumn(String property){
		addBooleanColumn(property,true);
	}
	
	/** Ajoute une case a cocher a la place de O et N
	 * @param property
	 */
	public void addBooleanColumn(String property, Boolean alignCenter){
		addGeneratedColumn(property, new CustomTable.ColumnGenerator() {
            /**serialVersionUID**/
			private static final long serialVersionUID = -3483685206189347289L;

			@Override
            public Object generateCell(CustomTable source, Object itemId, Object columnId) {				
				try {
					Object value = PropertyUtils.getProperty(itemId,(String)columnId);
					if (value instanceof Boolean){
						return new IconLabel((Boolean)value,alignCenter);
					}else{
						return value;
					}
				} catch (Exception e) {
					return null;
				}				
            }            
        });
	}

}
