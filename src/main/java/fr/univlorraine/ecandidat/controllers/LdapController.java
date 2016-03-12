package fr.univlorraine.ecandidat.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;

import fr.univlorraine.ecandidat.services.ldap.LdapException;
import fr.univlorraine.ecandidat.services.ldap.LdapGenericService;
import fr.univlorraine.ecandidat.services.ldap.PeopleLdap;

/**
 * Controller gérant les appels Ldap
 * @author Kevin Hergalant
 *
 */
@Component
public class LdapController {
	
	/*applicationContext pour les messages*/
	@Resource
	private transient ApplicationContext applicationContext;
	
	/*Les services Ldap*/
	@Resource(name="ldapPeopleServiceImpl")
	private LdapGenericService<PeopleLdap> ldapPeopleService;
	
	@Value("${ldap.champs.cn}")
	private String champsCn;
	
	@Value("${ldap.champs.uid}")
	private String champsUid;
	
	@Value("${ldap.filtre.personnel}")
	private String filtrePersonnel;
	
	
	
	/**Rafraichi le container de recherche de people Ldap
	 * @param txt le filtre a appliquer
	 */
	public List<PeopleLdap> getPeopleByFilter(String txt) {
		String filtreTotal = "("+champsCn+"=*"+txt+"*)";
		if (filtrePersonnel!=null && !filtrePersonnel.equals("")){
			filtreTotal = "(&"+filtreTotal+filtrePersonnel+")";
		}
		
		List<PeopleLdap> l = new ArrayList<PeopleLdap>();
		try{
			l = ldapPeopleService.findEntitiesByFilter(filtreTotal);
			if (l==null || l.size()==0){
				Notification.show(applicationContext.getMessage("ldap.search.noresult", null, Locale.getDefault()), Notification.Type.TRAY_NOTIFICATION);
			}else{
				return l;
			}
		}catch (LdapException e) {
			Notification.show(applicationContext.getMessage(e.getMessage(), null, Locale.getDefault()), Notification.Type.TRAY_NOTIFICATION);
		}
		return l;		
	}
	
	/** 
	 * @param uid
	 * @return Retourne un people par son uid
	 */
	public PeopleLdap findByPrimaryKey(String uid) {
		try{
			PeopleLdap l = ldapPeopleService.findByPrimaryKey(uid);
			if (l!=null){
				return l;
			}
		}catch (LdapException e) {
			Notification.show(applicationContext.getMessage(e.getMessage(), null, Locale.getDefault()), Notification.Type.TRAY_NOTIFICATION);
		}
		return null;		
	}

	
}
