package fr.univlorraine.ecandidat.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Resource;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.util.MethodInvocationUtils;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CommissionMembre;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.services.ldap.PeopleLdap;
import fr.univlorraine.ecandidat.services.security.SecurityAuthenticationProvider;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCommission;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.services.security.SecurityUser;
import fr.univlorraine.ecandidat.services.security.SecurityUserCandidat;
import fr.univlorraine.ecandidat.services.security.SecurityUserGestionnaire;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.PasswordHashUtils;

/**
 * Gestion de l'utilisateur
 * 
 * @author Kevin Hergalant
 *
 */
@Component
public class UserController {

	private Logger logger = LoggerFactory.getLogger(UserController.class);
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient Environment environment;
	@Resource
	private transient UserDetailsService userDetailsService;
	@Resource
	private transient MethodSecurityInterceptor methodSecurityInterceptor;
	@Resource
	private transient SecurityAuthenticationProvider authenticationManagerCandidat;
	@Resource
	private transient LdapController ldapController;
	@Resource
	private transient UiController uiController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient LoadBalancingController loadBalancingController;

	/**
	 * Récupère le securityContext dans la session.
	 * 
	 * @return securityContext associé à la session
	 */
	public SecurityContext getSecurityContextFromSession() {
		return (SecurityContext) UI.getCurrent().getSession().getSession()
				.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
	}

	/**
	 * @return l'authentification courante
	 */
	public Authentication getCurrentAuthentication() {
		/*if (SecurityContextHolder.getContext()!=null){
			System.out.println("1 : "+SecurityContextHolder.getContext().getAuthentication());
		}else{
			System.out.println("1 : "+SecurityContextHolder.getContext().getAuthentication());
		}*/
		
		/*System.out.println("1 : "+SecurityContextHolder.getContext());
		System.out.println("2: "+UI.getCurrent().getSession().getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));*/
		/*
		 * Iterator<String>
		 * i=UI.getCurrent().getSession().getSession().getAttributeNames().
		 * iterator(); // on crée un Iterator pour parcourir notre HashSet
		 * while(i.hasNext()) // tant qu'on a un suivant { String name =
		 * i.next(); System.out.println(name+" 1 : "+
		 * UI.getCurrent().getSession().getSession().getAttribute(name)); }
		 */
		//return SecurityContextHolder.getContext().getAuthentication();

		SecurityContext securityContext = getSecurityContextFromSession();
		if (securityContext == null) {
			return null;
		} else {
			return securityContext.getAuthentication();
		}

	}

	/**
	 * @param viewClass
	 * @return true si l'utilisateur peut accéder à la vue
	 */
	public boolean canCurrentUserAccessView(Class<? extends View> viewClass) {
		if (getCurrentAuthentication() == null) {
			return false;
		}
		MethodInvocation methodInvocation = MethodInvocationUtils.createFromClass(viewClass, "enter");
		Collection<ConfigAttribute> configAttributes = methodSecurityInterceptor.obtainSecurityMetadataSource()
				.getAttributes(methodInvocation);
		/* Renvoie true si la vue n'est pas sécurisée */
		if (configAttributes.isEmpty()) {
			return true;
		}
		/* Vérifie que l'utilisateur a les droits requis */
		try {
			methodSecurityInterceptor.getAccessDecisionManager().decide(getCurrentAuthentication(), methodInvocation,
					configAttributes);
		} catch (InsufficientAuthenticationException | AccessDeniedException e) {
			return false;
		}
		return true;
	}

	/**
	 * @return user utilisateur courant
	 */
	public UserDetails getCurrentUser() {
		if (isAnonymous()) {
			return null;
		}
		return (UserDetails) getCurrentAuthentication().getPrincipal();
	}

	/**
	 * @return login de l'utilisateur courant
	 */
	public String getCurrentUserLogin() {
		if (isAnonymous()) {
			return null;
		}
		return getCurrentAuthentication().getName();
	}
	
	/**
	 * @return no dossier du candidat
	 */
	public String getCurrentNoDossierCptMinOrLogin() {
		if (getCurrentUser() instanceof SecurityUserCandidat) {
			String noDossier = ((SecurityUserCandidat) getCurrentUser()).getNoDossierOPI();
			if (noDossier!=null && !noDossier.equals("")){
				return noDossier;
			}
		}
		return getCurrentUserLogin();
	}

	/**
	 * @return username de l'utilisateur courant
	 */
	public String getCurrentUserName() {
		if (isAnonymous()) {
			return applicationContext.getMessage("user.notconnected", null, UI.getCurrent().getLocale());
		} else {
			if (getCurrentUser() instanceof SecurityUser) {
				return ((SecurityUser) getCurrentUser()).getDisplayName();
			}
		}
		return getCurrentAuthentication().getName();
	}

	/**
	 * Verifie si le user est anonymous
	 * 
	 * @return true si le user est anonymous
	 */
	public Boolean isAnonymous() {
		if (getCurrentAuthentication() == null) {
			return true;
		}
		return getCurrentAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.filter(Predicate.isEqual(ConstanteUtils.ROLE_ANONYMOUS)).findAny().isPresent();
	}

	/**
	 * Verifie si le user est admin
	 * 
	 * @return true si le user est admin
	 */
	public Boolean isAdmin() {
		if (getCurrentAuthentication() == null) {
			return false;
		}
		return getCurrentAuthentication()
				.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(Predicate
						.isEqual(ConstanteUtils.ROLE_ADMIN).or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN_TECH)))
				.findAny().isPresent();
	}

	/**
	 * Verifie si le user est un candidat
	 * 
	 * @return true si le user est candidat
	 */
	public Boolean isCandidat() {
		if (getCurrentAuthentication() == null) {
			return false;
		}
		return getCurrentAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.filter(Predicate.isEqual(ConstanteUtils.ROLE_CANDIDAT)).findAny().isPresent();
	}

	/**
	 * Verifie si le user est scolCentrale
	 * 
	 * @return true si le user est scolCentrale
	 */
	public Boolean isScolCentrale() {
		if (getCurrentAuthentication() == null) {
			return false;
		}
		return getCurrentAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.filter(Predicate.isEqual(ConstanteUtils.ROLE_SCOL_CENTRALE).or(Predicate
						.isEqual(ConstanteUtils.ROLE_ADMIN).or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN_TECH))))
				.findAny().isPresent();
	}

	/**
	 * Verifie si le user est gestionnaire
	 * 
	 * @return true si le user est gestionnaire
	 */
	public Boolean isGestionnaire() {
		if (getCurrentAuthentication() == null) {
			return false;
		}
		return getCurrentAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.filter(Predicate.isEqual(ConstanteUtils.ROLE_SCOL_CENTRALE)
						.or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN)
								.or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN_TECH))
								.or(Predicate.isEqual(ConstanteUtils.ROLE_CENTRE_CANDIDATURE))))
				.findAny().isPresent();
	}

	/**
	 * Verifie si le user est membre de commission
	 * 
	 * @return true si le user est membre de commission
	 */
	public Boolean isCommissionMember() {
		if (getCurrentAuthentication() == null) {
			return false;
		}
		return getCurrentAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.filter(Predicate.isEqual(ConstanteUtils.ROLE_SCOL_CENTRALE)
						.or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN)
								.or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN_TECH))
								.or(Predicate.isEqual(ConstanteUtils.ROLE_COMMISSION))))
				.findAny().isPresent();
	}
	
	/**
	 * Verifie si le user est un personnel
	 * 
	 * @return true si le user est un personnel
	 */
	public Boolean isPersonnel() {
		if (getCurrentAuthentication() == null) {
			return false;
		}
		return getCurrentAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.filter(Predicate.isEqual(ConstanteUtils.ROLE_SCOL_CENTRALE)
						.or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN)
								.or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN_TECH))
								.or(Predicate.isEqual(ConstanteUtils.ROLE_CENTRE_CANDIDATURE))
								.or(Predicate.isEqual(ConstanteUtils.ROLE_COMMISSION))))
				.findAny().isPresent();
	}

	/**
	 * @return true si l'utilisateur a pris le rôle d'un autre utilisateur
	 */
	public boolean isUserSwitched() {
		if (getCurrentAuthentication() == null) {
			return false;
		}
		return getCurrentAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.filter(Predicate.isEqual(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR)).findAny().isPresent();
	}

	/**
	 * @return true si le candidat est valide, false sinon
	 */
	public Boolean isCandidatValid() {
		SecurityUserCandidat securityUserCand = getSecurityUserCandidat();
		if (securityUserCand != null) {
			return securityUserCand.getCptMinValid() && securityUserCand.getMailValid();
		}
		return false;
	}

	/**
	 * Change le rôle de l'utilisateur courant
	 * 
	 * @param username
	 *            le nom de l'utilisateur a prendre
	 */
	public void switchToUser(String username) {
		Assert.hasText(username);

		/* Vérifie que l'utilisateur existe */
		try {
			UserDetails details = userDetailsService.loadUserByUsername(username);
			if (details==null || details.getAuthorities()==null || details.getAuthorities().size()==0){
				Notification.show(applicationContext.getMessage("admin.switchUser.usernameNotFound",
						new Object[] { username }, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
				return;
			}
		} catch (UsernameNotFoundException unfe) {
			Notification.show(applicationContext.getMessage("admin.switchUser.usernameNotFound",
					new Object[] { username }, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
			return;
		}
		uiController.notifyUIRemoved(UI.getCurrent());
		String switchToUserUrl = MethodUtils.formatSecurityPath(loadBalancingController.getApplicationPath(false),ConstanteUtils.SECURITY_SWITCH_PATH) + "?"
				+ SwitchUserFilter.SPRING_SECURITY_SWITCH_USERNAME_KEY + "=" + username;
		Page.getCurrent().open(switchToUserUrl, null);
	}

	/**
	 * Rétabli le rôle original de l'utilisateur
	 */
	public void switchBackToPreviousUser() {
		uiController.notifyUIRemoved(UI.getCurrent());
		Page.getCurrent().open(MethodUtils.formatSecurityPath(loadBalancingController.getApplicationPath(false),ConstanteUtils.SECURITY_SWITCH_BACK_PATH), null);
	}

	/**
	 * Dirige l'utilisateur vers la page amenant la connexion cas
	 */
	public void connectCAS() {
		String path = MethodUtils.formatSecurityPath(loadBalancingController.getApplicationPath(false),ConstanteUtils.SECURITY_CONNECT_PATH);
		disconnectUser();
		UI.getCurrent().getPage().setLocation(path);

		/*
		 * Iterator<String>
		 * i=UI.getCurrent().getSession().getSession().getAttributeNames().
		 * iterator(); // on crée un Iterator pour parcourir notre HashSet
		 * while(i.hasNext()) // tant qu'on a un suivant { String name =
		 * i.next(); System.out.println(name+" : "+
		 * UI.getCurrent().getSession().getSession().getAttribute(name)); }
		 */
		// Page.getCurrent().open(path,null);
	}

	/**
	 * Deconnect l'utilisateur
	 */
	public void deconnect() {
		String path = MethodUtils.formatSecurityPath(loadBalancingController.getApplicationPath(false),ConstanteUtils.SECURITY_LOGOUT_PATH);
		disconnectUser();
		UI.getCurrent().getPage().setLocation(path);
	}

	/**
	 * Nettoie la session
	 */
	public void disconnectUser() {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		SecurityContextHolder.setContext(context);
		UI.getCurrent().getSession().getSession().setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				context);
		UI.getCurrent().getSession().close();
		// UI.getCurrent().close();
	}
	
	/**
	 * Connexion d'un candidat
	 * 
	 * @param username
	 *            login
	 * @param password
	 *            mot de passe
	 */
	public void connectCandidatInterne(String username, String password) {
		if (loadBalancingController.isLoadBalancingGestionnaireMode()){
			return;
		}		
		CompteMinima cptMin = candidatController.searchCptMinByNumDossier(username);
		if (cptMin != null) {
			if (!validPwdCandidat(password, cptMin.getPwdCptMin())) {
				return;
			}
			if (!cptMin.getTemValidCptMin() || !cptMin.getTemValidMailCptMin()) {
				Notification.show(applicationContext.getMessage("compteMinima.connect.valid.error", null,
						UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}
		} else {
			Notification.show(
					applicationContext.getMessage("compteMinima.connect.user.error", null, UI.getCurrent().getLocale()),
					Type.WARNING_MESSAGE);
			return;
		}

		SecurityUser user = constructSecurityUserCandidat(username, cptMin);
		if (user == null) {
			return;
		}

		// authentication
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user, username,
				user.getAuthorities());
		Authentication authentication = authenticationManagerCandidat.authenticate(authRequest);
		/*
		 * SecurityContextHolder.getContext().setAuthentication(authentication);
		 * UI.getCurrent().getSession().getSession().setAttribute(
		 * HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
		 * SecurityContextHolder.getContext());
		 */
		/* Se désinscrit de la réception de notifications */
		uiController.unregisterUiCandidat((MainUI)UI.getCurrent());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
		UI.getCurrent().getSession().getSession()
				.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
		MainUI current = (MainUI) UI.getCurrent();
		uiController.registerUiCandidat(current);
		i18nController.initLanguageUI(true);
		current.navigateToAccueilView();
		logger.debug("Connexion candidat réussi : "+username);
	}

	/**
	 * Recupere un element de connexion
	 * 
	 * @param username
	 *            le user a charger
	 * @return le user
	 */
	public SecurityUser getSecurityUser(String username) {
		SecurityUser user = connectAdminTech(username);
		if (user == null) {
			user = connectAdmin(username);
			if (user == null) {
				user = connectOther(username);
				if (user == null) {
					return connectCandidatCas(username);
				} else {
					return user;
				}
			} else {
				return user;
			}
		} else {
			return user;
		}
	}

	/**
	 * @param userName
	 * @return le displayName du Ldap, sinon le userName
	 */
	private String getDisplayNameFromLdap(String userName) {
		try {
			PeopleLdap p = ldapController.findByPrimaryKey(userName);
			if (p != null && p.getDisplayName() != null) {
				return p.getDisplayName();
			}
		} catch (Exception e) {
		}
		return userName;
	}

	/**
	 * Connect un admin technique
	 * 
	 * @param username
	 *            le username
	 * @return le user connecte
	 */
	public SecurityUser connectAdminTech(String username) {
		/*if (loadBalancingController.isLoadBalancingCandidatMode()){
			return null;
		}*/
		List<GrantedAuthority> authoritiesListe = new ArrayList<GrantedAuthority>();
		/* Verif si l'utilisateur est l'admin technique initial */
		if (environment.getRequiredProperty("admin.technique").equals(username)) {
			SimpleGrantedAuthority sga = new SimpleGrantedAuthority(ConstanteUtils.ROLE_ADMIN_TECH);
			authoritiesListe.add(sga);
			return new SecurityUserGestionnaire(username, getDisplayNameFromLdap(username), authoritiesListe, getCtrCandForAdmin(), getCommissionForAdmin());
		}
		return null;
	}

	/**
	 * Connect un admin ou adminscolcentrale
	 * 
	 * @param username
	 *            le username
	 * @return le user connecte
	 */
	public SecurityUser connectAdmin(String username) {
		if (loadBalancingController.isLoadBalancingCandidatMode()){
			return null;
		}
		List<GrantedAuthority> authoritiesListe = new ArrayList<GrantedAuthority>();
		/* Verif si l'utilisateur est l'admin technique initial */
		/* Récupération des droits d'admin */
		droitProfilController.searchDroitAdminByLogin(username).forEach(e -> {
			SimpleGrantedAuthority sga = new SimpleGrantedAuthority(
					ConstanteUtils.PREFIXE_ROLE + e.getDroitProfil().getCodProfil());
			authoritiesListe.add(sga);
		});
		/* Si admin on ne va pas plus loin */
		if (authoritiesListe.size() > 0) {
			return new SecurityUserGestionnaire(username, getDisplayNameFromLdap(username), authoritiesListe, getCtrCandForAdmin(), getCommissionForAdmin());
		}
		/* Récupération des droits scolCentral */
		droitProfilController.searchDroitScolCentralByLogin(username).forEach(e -> {
			SimpleGrantedAuthority sga = new SimpleGrantedAuthority(
					ConstanteUtils.PREFIXE_ROLE + e.getDroitProfil().getCodProfil());
			authoritiesListe.add(sga);
		});
		/* Si admin on ne va pas plus loin */
		if (authoritiesListe.size() > 0) {
			return new SecurityUserGestionnaire(username, getDisplayNameFromLdap(username), authoritiesListe, getCtrCandForAdmin(), getCommissionForAdmin());
		}
		return null;
	}

	/**
	 * Connect un membre de commission ou centre cand
	 * 
	 * @param username
	 *            le username
	 * @return le user connecte
	 */
	public SecurityUser connectOther(String username) {
		if (loadBalancingController.isLoadBalancingCandidatMode()){
			return null;
		}
		List<GrantedAuthority> authoritiesListe = new ArrayList<GrantedAuthority>();
		List<DroitProfilInd> listeDroitProfilInd = new ArrayList<DroitProfilInd>();

		Individu ind = individuController.getIndividu(username);

		if (ind != null) {
			// On recherche les profils autorisé (ctrCand ou commission pour
			// l'utilisateur suivant son login --> On ajoute à la liste
			listeDroitProfilInd.addAll(droitProfilController.searchDroitByLogin(username));
			/* Création de la liste d'autorities */
			SecurityCentreCandidature ctrCand = null;
			SecurityCommission commission = null;
			for (DroitProfilInd droitProfilInd : listeDroitProfilInd) {
				String codeRole;
				if (droitProfilInd.getDroitProfil().getTemCtrCandProfil()) {
					codeRole = ConstanteUtils.ROLE_CENTRE_CANDIDATURE;
					Gestionnaire gestionnaire = droitProfilInd.getGestionnaire();
					if (ctrCand == null && gestionnaire != null && gestionnaire.getCentreCandidature() != null
							&& gestionnaire.getCentreCandidature().getTesCtrCand()) {
						
						List<Integer> listComm = new ArrayList<Integer>();
						gestionnaire.getCommissions().forEach(e->listComm.add(e.getIdComm()));
						
						ctrCand = new SecurityCentreCandidature(droitProfilInd.getGestionnaire().getCentreCandidature(),
								new ArrayList<DroitProfilFonc>(droitProfilInd.getDroitProfil().getDroitProfilFoncs()),
								individuController.getCodCgeForGestionnaire(gestionnaire,username),false,gestionnaire.getTemAllCommGest(),listComm);
					}
				} else {
					codeRole = ConstanteUtils.ROLE_COMMISSION;
					CommissionMembre membre = droitProfilInd.getCommissionMembre();
					if (commission == null && membre != null && membre.getCommission() != null
							&& membre.getCommission().getTesComm()) {
						commission = new SecurityCommission(droitProfilInd.getCommissionMembre().getCommission());
					}
				}
				SimpleGrantedAuthority sga = new SimpleGrantedAuthority(codeRole);
				if (!authoritiesListe.contains(sga)) {
					authoritiesListe.add(sga);
				}
			}
			// on verifie qu'il y a bien des droits!
			if (authoritiesListe.size() > 0) {
				return new SecurityUserGestionnaire(username, getDisplayNameFromLdap(username), authoritiesListe, ctrCand, commission);
			}
		}

		return null;
	}

	/**
	 * Valide le mot de passe candidat
	 * 
	 * @param password
	 *            le mot de passe
	 * @param correctHash
	 *            le hash correct
	 * @return true si le mot de passe correspond
	 */
	private Boolean validPwdCandidat(String password, String correctHash) {
		try {
			if (!PasswordHashUtils.validatePassword(password, correctHash)) {
				Notification.show(applicationContext.getMessage("compteMinima.connect.pwd.error", null,
						UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return false;
			} else {
				return true;
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			Notification.show(
					applicationContext.getMessage("compteMinima.connect.pwd.error", null, UI.getCurrent().getLocale()),
					Type.WARNING_MESSAGE);
			return false;
		}
	}

	/**
	 * Connect un candidat
	 * 
	 * @param username
	 *            le username
	 * @return le user connecte
	 */
	public SecurityUser connectCandidatCas(String username) {
		if (loadBalancingController.isLoadBalancingGestionnaireMode()){
			return new SecurityUser(username, username, new ArrayList<GrantedAuthority>());
		}
		CompteMinima cptMin = candidatController.searchCptMinByLogin(username);
		return constructSecurityUserCandidat(username, cptMin);
	}

	/**
	 * Créer un user Candidat
	 * 
	 * @param cptMin
	 *            le compte a minima cree
	 * @param username
	 *            le username
	 * @return le user connecte
	 */
	private SecurityUser constructSecurityUserCandidat(String username, CompteMinima cptMin) {
		Integer idCptMin = null;
		String noDossierOPI = null;
		Boolean cptMinValid = false;
		Boolean mailValid = false;

		if (cptMin != null) {
			idCptMin = cptMin.getIdCptMin();
			noDossierOPI = cptMin.getNumDossierOpiCptMin();
			cptMinValid = cptMin.getTemValidCptMin();
			mailValid = cptMin.getTemValidMailCptMin();
			List<GrantedAuthority> authoritiesListe = new ArrayList<GrantedAuthority>();
			SimpleGrantedAuthority sga = new SimpleGrantedAuthority(ConstanteUtils.ROLE_CANDIDAT);
			authoritiesListe.add(sga);
			Candidat candidat = cptMin.getCandidat();
			String codLangue = null;
			if (candidat != null) {
				codLangue = candidat.getLangue().getCodLangue();				
			}
			return new SecurityUserCandidat(username, getDisplayNameCandidat(cptMin), authoritiesListe, idCptMin,
					noDossierOPI, cptMinValid, mailValid, codLangue);
		} else {
			return new SecurityUser(username, username, new ArrayList<GrantedAuthority>());
		}
	}

	/**
	 * @return le user Candidat
	 */
	public SecurityUserCandidat getSecurityUserCandidat() {
		if (getCurrentUser() instanceof SecurityUserCandidat) {
			return ((SecurityUserCandidat) getCurrentUser());
		}
		return null;
	}

	/**
	 * Alimente la session pour un compte local
	 * 
	 * @param cptMin
	 *            le compte a minima a connecter
	 */
	public void alimenteSecurityUserCptMin(CompteMinima cptMin) {
		SecurityUser user = (SecurityUser) getCurrentUser();
		if (user != null) {
			List<GrantedAuthority> authoritiesListe = new ArrayList<GrantedAuthority>();
			SimpleGrantedAuthority sga = new SimpleGrantedAuthority(ConstanteUtils.ROLE_CANDIDAT);
			authoritiesListe.add(sga);

			SecurityUserCandidat securityUserCandidat = new SecurityUserCandidat(user.getUsername(),
					user.getDisplayName(), authoritiesListe, cptMin.getIdCptMin(), cptMin.getNumDossierOpiCptMin(),
					cptMin.getTemValidCptMin(), cptMin.getTemValidMailCptMin(), null);
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
					securityUserCandidat, securityUserCandidat.getUsername(), securityUserCandidat.getAuthorities());
			Authentication authentication = authenticationManagerCandidat.authenticate(authRequest);
			/*
			 * SecurityContextHolder.getContext().setAuthentication(
			 * authentication);
			 * UI.getCurrent().getSession().getSession().setAttribute(
			 * HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
			 * SecurityContextHolder.getContext());
			 */
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);
			UI.getCurrent().getSession().getSession()
					.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
		}
	}

	/**
	 * Valide les flags d'un compteminima connecte
	 */
	public void validSecurityUserCptMin() {
		SecurityUserCandidat securityUserCandidat = getSecurityUserCandidat();		
		CompteMinima cptMin = candidatController.getCompteMinima();
		if (securityUserCandidat!=null && securityUserCandidat!=null){
			securityUserCandidat.setCptMinValid(cptMin.getTemValidCptMin());
			securityUserCandidat.setMailValid(cptMin.getTemValidMailCptMin());
		}
	}

	/**
	 * Renvoi le centre de canidature à rattaché à l'utilisateur
	 * 
	 * @param id
	 *            l'id du ctrCand
	 * @param username
	 *            le user
	 * @return L'element de connexion ctrCand
	 */
	public SecurityCentreCandidature getSecurityCentreCandidature(Integer id, String username) {
		for (DroitProfilInd droitProfilInd : droitProfilController.searchDroitByLoginAndIdCtrCand(id, username)) {
			if (droitProfilInd.getDroitProfil().getTemCtrCandProfil()) {
				Gestionnaire gestionnaire = droitProfilInd.getGestionnaire();
				if (gestionnaire != null && gestionnaire.getCentreCandidature() != null) {
					List<Integer> listComm = new ArrayList<Integer>();
					gestionnaire.getCommissions().forEach(e->listComm.add(e.getIdComm()));
					
					return new SecurityCentreCandidature(droitProfilInd.getGestionnaire().getCentreCandidature(),
							new ArrayList<DroitProfilFonc>(droitProfilInd.getDroitProfil().getDroitProfilFoncs()),
							individuController.getCodCgeForGestionnaire(gestionnaire,username), false, gestionnaire.getTemAllCommGest(),listComm);
				}
			}
		}
		return null;
	}

	/**
	 * Recupere le premier centre de candidature pour un admin
	 * 
	 * @return le premier centre de candidature
	 */
	public SecurityCentreCandidature getCtrCandForAdmin() {
		CentreCandidature ctrCand = droitProfilController.getCtrCandForAdmin();
		if (ctrCand != null) {
			return new SecurityCentreCandidature(ctrCand, null, null, true,true,new ArrayList<Integer>());
		}
		return null;
	}

	/**
	 * Recupere la premiere commission pour un admin
	 * 
	 * @return la premiere commission
	 */
	public SecurityCommission getCommissionForAdmin() {
		Commission commission = droitProfilController.getCommissionForAdmin();
		if (commission != null) {
			return new SecurityCommission(commission);
		}
		return null;
	}

	/**
	 * Renvoie le centre de candidature préféré
	 * 
	 * @return le centre de candidature de la session
	 */
	public SecurityCentreCandidature getCentreCandidature() {
		if (getCurrentUser() instanceof SecurityUserGestionnaire) {
			return ((SecurityUserGestionnaire) getCurrentUser()).getCentreCandidature();
		}
		return null;
	}

	/**
	 * Renvoie la commission préférée
	 * 
	 * @return la commission de la session
	 */
	public SecurityCommission getCommission() {
		if (getCurrentUser() instanceof SecurityUserGestionnaire) {
			return ((SecurityUserGestionnaire) getCurrentUser()).getCommission();
		}
		return null;
	}

	/**
	 * Renvoie le numero dossier candidat en cours d'édition
	 * 
	 * @return le numero dossier candidat en cours d'édition
	 */
	public String getNoDossierCandidat() {
		if (getCurrentUser() instanceof SecurityUserGestionnaire) {
			return ((SecurityUserGestionnaire) getCurrentUser()).getNoDossierCandidat();
		}
		return null;
	}

	/**
	 * Renvoie le numero dossier candidat en cours d'édition
	 * 
	 * @return le numero dossier candidat en cours d'édition
	 */
	public String getDisplayNameCandidat() {
		if (getCurrentUser() instanceof SecurityUserGestionnaire) {
			return ((SecurityUserGestionnaire) getCurrentUser()).getDisplayNameCandidat();
		}
		return null;
	}

	/**
	 * Renvoie la fonctionnalité et le centre de candidature en cours
	 * 
	 * @param codFonc
	 *            le code de la fonctionnalite
	 * @return l'element de session de fonctionnalite
	 */
	public SecurityCtrCandFonc getCtrCandFonctionnalite(String codFonc) {
		SecurityCentreCandidature scc = getCentreCandidature();
		if (scc != null) {
			CentreCandidature ctrCand = centreCandidatureController.getCentreCandidature(scc.getIdCtrCand());
			if (ctrCand != null) {
				SecurityCtrCandFonc sf = new SecurityCtrCandFonc(ctrCand.getIdCtrCand(), null, scc.getIsGestAllCommission(), scc.getListeIdCommission());
				if (isScolCentrale()) {
					sf.setReadOnly(false);
				} else if (scc.getListFonctionnalite() != null && scc.getListFonctionnalite().size() != 0) {
					Optional<DroitProfilFonc> fonc = scc.getListFonctionnalite().stream()
							.filter(e -> e.getId().getCodFonc().equals(codFonc)).findFirst();
					if (fonc.isPresent()) {
						sf.setReadOnly(fonc.get().getTemReadOnly());
					}
				}

				return sf;
			}
		}
		return null;
	}

	/**
	 * @return les fonctionnalites dont l'utilisateur a le droit pour les
	 *         candidatures
	 */
	public List<DroitFonctionnalite> getCandidatureFonctionnalite() {
		List<DroitFonctionnalite> listeFonc = tableRefController.getListeDroitFonctionnaliteCandidature();
		if (isScolCentrale()) {
			return listeFonc;
		} else {
			SecurityCentreCandidature scc = getCentreCandidature();
			if (scc.getListFonctionnalite() != null && scc.getListFonctionnalite().size() != 0) {
				List<DroitFonctionnalite> listeFoncToRet = new ArrayList<DroitFonctionnalite>();
				listeFonc.forEach(f -> {
					Optional<DroitProfilFonc> fonc = scc.getListFonctionnalite().stream()
							.filter(e -> e.getId().getCodFonc().equals(f.getCodFonc()) && !e.getTemReadOnly())
							.findFirst();
					if (fonc.isPresent()) {
						listeFoncToRet.add(fonc.get().getDroitFonctionnalite());
					}
				});

				return listeFoncToRet;
			}
		}
		return new ArrayList<DroitFonctionnalite>();
	}

	/**
	 * change le centre de candidature préféré
	 * 
	 * @param centreCand
	 *            le centre de candidature
	 */
	public void setCentreCandidature(CentreCandidature centreCand) {
		if (getCurrentUser() instanceof SecurityUserGestionnaire) {
			if (isScolCentrale()) {
				((SecurityUserGestionnaire) getCurrentUser())
						.setCentreCandidature(new SecurityCentreCandidature(centreCand, null, null, true,true,new ArrayList<Integer>()));
			} else {
				SecurityCentreCandidature centre = getSecurityCentreCandidature(centreCand.getIdCtrCand(),
						getCurrentUserLogin());
				((SecurityUserGestionnaire) getCurrentUser()).setCentreCandidature(centre);
			}
		}
		return;
	}

	/**
	 * change la commission preferee
	 * 
	 * @param commission
	 *            la commission
	 */
	public void setCommission(Commission commission) {
		if (getCurrentUser() instanceof SecurityUserGestionnaire) {
			((SecurityUserGestionnaire) getCurrentUser()).setCommission(new SecurityCommission(commission));
		}
		return;
	}

	/**
	 * Change le numero de dossier en cours d'edition
	 * 
	 * @param cptMin le compte a minima
	 */
	public void setNoDossierNomCandidat(CompteMinima cptMin) {
		if (getCurrentUser() instanceof SecurityUserGestionnaire) {
			if (cptMin == null) {
				((SecurityUserGestionnaire) getCurrentUser()).setNoDossierCandidat(null);
				((SecurityUserGestionnaire) getCurrentUser()).setDisplayNameCandidat(null);
			} else {
				((SecurityUserGestionnaire) getCurrentUser()).setNoDossierCandidat(cptMin.getNumDossierOpiCptMin());
				((SecurityUserGestionnaire) getCurrentUser()).setDisplayNameCandidat(getDisplayNameCandidat(cptMin));
			}

		}
	}

	/**
	 * @param cpt
	 * @return le displayName du candidat
	 */
	private String getDisplayNameCandidat(CompteMinima cpt) {
		if (cpt == null) {
			return null;
		} else {
			return cpt.getPrenomCptMin()+" "+cpt.getNomCptMin();
		}
	}

	/**
	 * @return le numéro de dossier d'un candidat ou gestionnaire
	 */
	public String getNoDossierOPI() {
		if (getCurrentUser() instanceof SecurityUserCandidat) {
			return ((SecurityUserCandidat) getCurrentUser()).getNoDossierOPI();
		} else if (getCurrentUser() instanceof SecurityUserGestionnaire) {
			return ((SecurityUserGestionnaire) getCurrentUser()).getNoDossierCandidat();
		}
		return null;
	}

	/**
	 * @param candidature
	 * @return true si le gestionnaire a le droit de modifier une candidature
	 */
	public Boolean isAutorizedToLookCandidature(Candidature candidature) {
		if (isScolCentrale()) {
			return true;
		} else {
			List<DroitProfilInd> listeProfil = droitProfilController.searchDroitByLoginAndIdCtrCand(
					candidature.getFormation().getCommission().getCentreCandidature().getIdCtrCand(),
					getCurrentUserLogin());
			for (DroitProfilInd profils : listeProfil) {
				for (DroitProfilFonc fonc : profils.getDroitProfil().getDroitProfilFoncs()) {
					if (!fonc.getTemReadOnly() && fonc.getDroitFonctionnalite().getCodFonc()
							.equals(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
