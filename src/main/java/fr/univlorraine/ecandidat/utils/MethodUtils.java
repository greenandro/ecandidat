package fr.univlorraine.ecandidat.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;

/** Class de methode utilitaires
 * @author Kevin Hergalant
 *
 */
public class MethodUtils {

	/** Renvoi pour une classe donnée si le champs est nullable ou non
	 * @param classObject
	 * @param property
	 * @return true si l'objet n'est pas null
	 */
	public static Boolean getIsNotNull(Class<?> classObject, String property){
		try {
			NotNull notNull = classObject.getDeclaredField(property).getAnnotation(NotNull.class);
			if (notNull==null){
				return false;
			}
			return true;
		} catch (NoSuchFieldException | SecurityException e) {
			return false;
		}
	}
	
	/** Renvoi un boolean pour un temoin en string (O ou N)
	 * @param temoin
	 * @return le boolean associe
	 */
	public static Boolean getBooleanFromTemoin(String temoin){
		if (temoin == null || temoin.equals(ConstanteUtils.TYP_BOOLEAN_NO)){
			return false;
		}
		return true;
	}
	
	/** Renvoi temoin en string (O ou N) pour un boolean 
	 * @param bool
	 * @return le String associe
	 */
	public static String getTemoinFromBoolean(Boolean bool){
		if (!bool){
			return ConstanteUtils.TYP_BOOLEAN_NO;
		}
		return ConstanteUtils.TYP_BOOLEAN_YES;
	}
	
	/** Ajoute du texte à la suite et place une virgule entre
	 * @param text
	 * @param more
	 * @return le txt complété
	 */
	public static String constructStringEnum(String text, String more){
		if (text == null || text.equals("")){
			return more;
		}else{
			return text+", "+more;
		}
	}

	/** Ajoute un 0 devant le label de temps pour 0, 1, 2, etc..
	 * @param time
	 * @return le label de minute ou d'heure complété
	 */
	public static String getLabelMinuteHeure(Integer time){
		if (time == null){
			return "";
		}else{
			String temps = String.valueOf(time);
			if (temps.length()==1){
				temps = "0"+temps;
			}
			return temps;
		}
	}
	
	/** Nettoie un nom de fichier pour le stockage fs
	 * @param fileName
	 * @return le nom de fichier pour le stockage fs
	 */
	public static String cleanFileName(String fileName){
		if (fileName == null || fileName.equals("")){
			return "_";
		}
		return removeAccents(fileName).replaceAll("[^A-Za-z0-9\\.\\-\\_]", "");
	}
	
	/** Remplace les accents
	 * @param text
	 * @return le text sans accents
	 */
	public static String removeAccents(String text) {
	    return text == null ? "" :
	        Normalizer.normalize(text, Form.NFD)
	            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	/** Valide un bean
	 * @param bean
	 * @throws CustomException
	 */
	public static <T> Boolean validateBean(T bean, Logger logger){
		logger.debug(" ***VALIDATION*** : "+bean);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(bean);
		if (constraintViolations!=null && constraintViolations.size() > 0) {
			for (ConstraintViolation<?> violation : constraintViolations) {
				logger.debug(" *** "+ violation.getPropertyPath().toString() + " : " + violation.getMessage());
			  }
			return false;
		}
		return true;
	}

	/** Formate un texte
	 * @param txt
	 * @return un txt formaté
	 */
	public static String formatToExport(String txt) {
		if (txt == null){
			return "";
		}
		return txt;
	}
	
	/** Verifie que le fichier est un pdf
	 * @param fileName
	 * @return true si le fichier est un pdf
	 */
	public static Boolean isPdfFileName(String fileName){
		return getExtension(fileName.toLowerCase()).equals("pdf");
	}
	
	/** Verifie que le fichier est un jpg
	 * @param fileName
	 * @return true si le fichier est un jpg
	 */
	public static Boolean isJpgFileName(String fileName){
		return getExtension(fileName.toLowerCase()).equals("jpg") || getExtension(fileName.toLowerCase()).equals("jpeg");
	}
	
	/** renvoie l'extension
	 * @param fileName
	 * @return l'extension du fichier
	 */
	private static String getExtension(String fileName){
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i >= 0) {
		    extension = fileName.substring(i+1);
		}
		return extension;
	}
	

	/**
	 * @param liste
	 * @param code
	 * @return le libellé de presentation 
	 */
	public static String getLibByPresentationCode(List<SimpleTablePresentation> liste, String code){
		Optional<SimpleTablePresentation> opt = liste.stream().filter(e->e.getCode().equals(code)).findFirst();
		if (opt.isPresent() && opt.get().getValue()!=null){
			return opt.get().getValue().toString();
		}
		return "";		
	}

	/** Verifie qu'une date est inclue dans un intervalle
	 * @param dateToCompare
	 * @return true si la date est incluse dans un interval
	 */
	public static Boolean isDateIncludeInInterval(LocalDate dateToCompare, LocalDate dateDebut, LocalDate dateFin){
		if (dateToCompare == null){
			/*Si la date est null, c'est ok!*/
			return true;
		}else if ((dateToCompare.equals(dateDebut) || dateToCompare.isAfter(dateDebut)) && (dateToCompare.equals(dateFin) || dateToCompare.isBefore(dateFin))){
			return true;
		}
		return false;
	}
	
	/** Converti un String en entier
	 * @param txt
	 * @return l'entier converti
	 */
	public static Integer convertStringToIntger(String txt){
		if (txt == null){
			return null;
		}
		try{
			return Integer.valueOf(txt);
		}catch(Exception e){
			return null;
		}
	}
	
	/** Converti une date en LocalDate
	 * @param date
	 * @return la localDate convertie
	 */
	public static LocalDate convertDateToLocalDate(Date date){
		if (date == null){
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	/** Converti une LocalDate en date
	 * @param date
	 * @return la date convertie
	 */
	public static Date convertLocalDateToDate(LocalDate date){
		if (date == null){
			return null;
		}
		Instant instant = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}
	
	/** Replace la derniere occurence
	 * @param string
	 * @param from
	 * @param to
	 * @return le string nettoye
	 */
	public static String replaceLast(String string, String from, String to) {
	     int lastIndex = string.lastIndexOf(from);
	     if (lastIndex < 0) return string;
	     String tail = string.substring(lastIndex).replaceFirst(from, to);
	     return string.substring(0, lastIndex) + tail;
	}
	
	/**
	 * @param fileName
	 * @return true si l'extension est jpg ou pdf
	 */
	public static Boolean checkExtension(String fileName){
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
		    extension = fileName.substring(i+1);
		}else{
			return false;
		}
		
		if (extension.equals("")){
			return false;
		}
		extension = extension.toLowerCase();
		if (Arrays.asList(ConstanteUtils.EXTENSION_PDF_JPG_JPEG).contains(extension)){
			return true;
		}
		return false;
	}
	
	/**
	 * @param path
	 * @return la path agrémenté d'un / a la fin
	 */
	public static String formatUrlApplication(String path){
		if (path!=null && !path.equals("")){
			if(!path.substring(path.length() - 1).equals("/")){
				path += "/";
			}
		}
		return path;
	}
	
	/**
	 * @param appPath
	 * @param add
	 * @return l'url formatée pour switch user
	 */
	public static String formatSecurityPath(String appPath, String add){
		if (appPath!=null && !appPath.equals("")){
			if(appPath.substring(appPath.length() - 1).equals("/")){
				appPath = appPath.substring(0,appPath.length() - 1);
			}
		}
		return appPath+add;
	}
	
	/**
	 * @return la version des WS
	 */
	public static String getClassVersion(Class<?> theClass){
		try{
			  
			// Find the path of the compiled class 
			String classPath = theClass.getResource(theClass.getSimpleName() + ".class").toString(); 
			 
			// Find the path of the lib which includes the class 
			String libPath = classPath.substring(0, classPath.lastIndexOf("!")); 
			
			if (libPath!=null){
				Integer lastIndex = libPath.lastIndexOf("/");
				if (lastIndex!=-1){
					libPath = libPath.substring(lastIndex+1,libPath.length());
					libPath = libPath.replaceAll(".jar", "");
					return libPath; 
				}			
			}
			/*if (libPath!=null){
				Integer lastIndex = libPath.lastIndexOf("/");
				if (lastIndex!=-1){
					libPath = libPath.substring(0,lastIndex);
					lastIndex = libPath.lastIndexOf("/");
					if (lastIndex!=-1){
						libPath = libPath.substring(lastIndex+1,libPath.length());
					}
					return libPath; 
				}			
			}*/
		}catch(Exception e){
		}
		return "";
	}
}
