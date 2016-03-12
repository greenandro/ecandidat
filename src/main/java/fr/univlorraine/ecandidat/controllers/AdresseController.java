package fr.univlorraine.ecandidat.controllers;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 *
 */
@Component
public class AdresseController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TableRefController tableRefController;

	/**
	 * @param adresse
	 * @param delimiter
	 * @return le libelle d'une adresse
	 */
	public String getLibelleAdresse(Adresse adresse,String delimiter){
		String libAdr = "";
		if (adresse != null){
			if (adresse.getAdr1Adr()!=null){
				libAdr = libAdr + adresse.getAdr1Adr()+delimiter;
			}
			if (adresse.getAdr2Adr()!=null){
				libAdr = libAdr + adresse.getAdr2Adr()+delimiter;
			}
			if (adresse.getAdr3Adr()!=null){
				libAdr = libAdr + adresse.getAdr3Adr()+delimiter;
			}
			if (adresse.getCodBdiAdr()!=null && adresse.getCedexAdr()!=null && adresse.getSiScolCommune()!=null && adresse.getSiScolCommune().getLibCom()!=null){
				libAdr = libAdr + adresse.getCodBdiAdr()+" "+adresse.getSiScolCommune().getLibCom()+" "+ adresse.getCedexAdr()+delimiter;
			}else if (adresse.getCodBdiAdr()!=null && adresse.getSiScolCommune()!=null && adresse.getSiScolCommune().getLibCom()!=null){
				libAdr = libAdr + adresse.getCodBdiAdr()+" "+adresse.getSiScolCommune().getLibCom()+delimiter;
			}else{
				if (adresse.getCodBdiAdr()!=null){
					libAdr = libAdr + adresse.getCodBdiAdr()+delimiter;
				}
				if (adresse.getSiScolCommune()!=null && adresse.getSiScolCommune().getLibCom()!=null){
					libAdr = libAdr + adresse.getSiScolCommune().getLibCom()+delimiter;
				}
			}
			if (adresse.getLibComEtrAdr()!=null){
				libAdr = libAdr + adresse.getLibComEtrAdr()+delimiter;
			}
			if (adresse.getSiScolPays()!=null && !adresse.getSiScolPays().equals(tableRefController.getPaysFrance())){
				libAdr = libAdr + adresse.getSiScolPays().getLibPay()+delimiter;
			}
		}
		return libAdr;
	}
	
	/**
	 * @param commission
	 * @param delimiter
	 * @return le libelle de l'adresse de la commission
	 */
	public String getLibelleAdresseCommission(Commission commission, String delimiter){
		String libAdr = getLibelleAdresse(commission.getAdresse(),delimiter);
		if (commission.getTelComm()!=null){
			libAdr = libAdr + applicationContext.getMessage("candidature.adresse.tel", new Object[]{commission.getTelComm()}, UI.getCurrent().getLocale())+delimiter;
		}
		if (commission.getMailComm()!=null){
			libAdr = libAdr + applicationContext.getMessage("candidature.adresse.mail", new Object[]{commission.getMailComm()}, UI.getCurrent().getLocale())+delimiter;
		}
		if (commission.getFaxComm()!=null){
			libAdr = libAdr + applicationContext.getMessage("candidature.adresse.fax", new Object[]{commission.getFaxComm()}, UI.getCurrent().getLocale())+delimiter;
		}
		if (commission.getCommentRetourComm()!=null){
			libAdr = libAdr +delimiter+ commission.getCommentRetourComm();
		}
		
		return libAdr;
	}
}
