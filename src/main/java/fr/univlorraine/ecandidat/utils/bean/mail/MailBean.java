package fr.univlorraine.ecandidat.utils.bean.mail;

import java.io.Serializable;

import org.apache.commons.beanutils.BeanUtils;

import lombok.Data;

@Data
public class MailBean implements Serializable{

	/**serialVersionUID**/
	private static final long serialVersionUID = -7146699521220262510L;

	/** Renvoie la valeur de la propriété du bean
	 * @param property
	 * @return la valeur de la propriété 
	 */
	public String getValueProperty(String property){
		try {			
			String valueProperty = BeanUtils.getProperty(this, property);
			if (valueProperty!=null){
				return valueProperty.toString();
			}else{
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}
}
