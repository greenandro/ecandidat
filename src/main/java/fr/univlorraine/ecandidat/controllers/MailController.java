package fr.univlorraine.ecandidat.controllers;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.I18nTraduction;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.repositories.MailRepository;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.mail.CandidatMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.CandidatureMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.CommissionMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.FormationMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.MailBean;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.ScolMailWindow;

/**
 * Gestion de l'entité mail
 * @author Kevin Hergalant
 */
@Component
public class MailController {
	
	private Logger logger = LoggerFactory.getLogger(MailController.class);
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient AdresseController adresseController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient MailRepository mailRepository;
	@Resource
	private transient Environment environment;
	@Resource
	private JavaMailSender javaMailService;
	@Resource
	private transient DateTimeFormatter formatterDate;
	
	/**
	 * @return liste des mails modele
	 */
	public List<Mail> getMailsModels() {
		return mailRepository.findByTemIsModeleMail(true);
	}
	
	/**
	 * @return retourne un mail par son code
	 */
	public Mail getMailByCod(String code) {
		return mailRepository.findByCodMail(code);
	}
	
	/**
	 * @return liste des mails nouveau
	 */
	public List<Mail> getMailsTypeDecScol() {
		return mailRepository.findByTemIsModeleMail(false);
	}
	
	/**
	 * @return liste des mails avec un type de decision
	 */
	public List<Mail> getMailsTypeAvis() {
		return mailRepository.findByTypeAvisNotNullAndTesMail(true);
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau mail.
	 */
	public void editNewMail() {
		Mail mail = new Mail(userController.getCurrentUserLogin());
		mail.setTypeAvis(tableRefController.getTypeAvisFavorable());
		mail.setI18nCorpsMail(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_MAIL_CORPS)));
		mail.setI18nSujetMail(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_MAIL_SUJET)));
		UI.getCurrent().addWindow(new ScolMailWindow(mail));
	}
	
	/**
	 * Ouvre une fenêtre d'édition de mail.
	 * @param mail
	 */
	public void editMail(Mail mail) {
		Assert.notNull(mail);

		/* Verrou */
		if (!lockController.getLockOrNotify(mail, null)) {
			return;
		}
		
		ScolMailWindow window = new ScolMailWindow(mail);
		window.addCloseListener(e->lockController.releaseLock(mail));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un mail
	 * @param mail
	 */
	public void saveMail(Mail mail) {
		Assert.notNull(mail);
		

		/* Verrou */
		if (mail.getIdMail()!=null && !lockController.getLockOrNotify(mail, null)) {
			return;
		}
		mail.setUserModMail(userController.getCurrentUserLogin());
		mail.setI18nSujetMail(i18nController.saveI18n(mail.getI18nSujetMail()));
		mail.setI18nCorpsMail(i18nController.saveI18n(mail.getI18nCorpsMail()));
		mail = mailRepository.saveAndFlush(mail);
		
		lockController.releaseLock(mail);
	}

	/**
	 * Supprime une mail
	 * @param mail
	 */
	public void deleteMail(Mail mail) {
		Assert.notNull(mail);
		
		if (mail.getTypeDecisions().size()>0){
			Notification.show(applicationContext.getMessage("mail.error.delete", new Object[]{TypeDecision.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(mail, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("mail.window.confirmDelete", new Object[]{mail.getCodMail()}, UI.getCurrent().getLocale()), applicationContext.getMessage("mail.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(mail, null)) {
				mailRepository.delete(mail);
				/* Suppression du lock */
				lockController.releaseLock(mail);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(mail);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	/** Verifie l'unicité du code
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodMailUnique(String cod, Integer id) {
		Mail mail = getMailByCod(cod);
		if (mail==null){
			return true;
		}else{
			if (mail.getIdMail().equals(id)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return un bean mail pour la candidature-->commun a la plupart des mails
	 */
	public CandidatureMailBean getCandidatureMailBean(Candidature candidature){
		Candidat candidat = candidature.getCandidat();
		Formation formation = candidature.getFormation();
		Commission commission = candidature.getFormation().getCommission();
		
		/*Bean du candidat*/
		CandidatMailBean  candidatMailBean = new CandidatMailBean();
		candidatMailBean.setNumDossierOpi(candidat.getCompteMinima().getNumDossierOpiCptMin());
		candidatMailBean.setNomPat(candidat.getNomPatCandidat());
		candidatMailBean.setNomUsu(candidat.getNomUsuCandidat());
		candidatMailBean.setPrenom(candidat.getPrenomCandidat());
		candidatMailBean.setAutrePrenom(candidat.getAutrePrenCandidat());
		candidatMailBean.setIne(candidat.getIneCandidat());
		candidatMailBean.setCleIne(candidat.getCleIneCandidat());
		candidatMailBean.setDatNaiss(formatterDate.format(candidat.getDatNaissCandidat()));
		candidatMailBean.setLibVilleNaiss(candidat.getLibVilleNaissCandidat());
		candidatMailBean.setLibLangue(candidat.getLangue().getLibLangue());
		candidatMailBean.setTel(candidat.getTelCandidat());
		candidatMailBean.setTelPort(candidat.getTelPortCandidat());
		
		/*Bean de la formation*/
		FormationMailBean formationMailBean = new FormationMailBean();
		formationMailBean.setCode(formation.getCodForm());
		formationMailBean.setLibelle(formation.getLibForm());
		formationMailBean.setCodEtpVetApo(formation.getCodEtpVetApoForm());
		formationMailBean.setCodVrsVetApo(formation.getCodVrsVetApoForm());
		formationMailBean.setLibApo(formation.getLibApoForm());
		formationMailBean.setMotCle(formation.getMotCleForm());
		if (formation.getDatPubliForm()!=null){
			formationMailBean.setDatPubli(formatterDate.format(formation.getDatPubliForm()));
		}
		if (formation.getDatRetourForm()!=null){
			formationMailBean.setDatRetour(formatterDate.format(formation.getDatRetourForm()));
		}
		if (formation.getDatJuryForm()!=null){
			formationMailBean.setDatJury(formatterDate.format(formation.getDatJuryForm()));
		}
		if (formation.getDatConfirmForm()!=null){
			formationMailBean.setDatConfirm(formatterDate.format(formation.getDatConfirmForm()));			
		}
		if (formation.getDatDebDepotForm()!=null){
			formationMailBean.setDatDebDepot(formatterDate.format(formation.getDatDebDepotForm()));
		}
		if (formation.getDatFinDepotForm()!=null){
			formationMailBean.setDatFinDepot(formatterDate.format(formation.getDatFinDepotForm()));
		}
		if (formation.getDatAnalyseForm()!=null){
			formationMailBean.setDatPreAnalyse(formatterDate.format(formation.getDatAnalyseForm()));
		}
		
		/*Bean de la commission*/
		CommissionMailBean commissionMailBean = new CommissionMailBean();
		commissionMailBean.setLibelle(commission.getLibComm());
		commissionMailBean.setTel(commission.getTelComm());
		commissionMailBean.setMail(commission.getMailComm());		
		commissionMailBean.setFax(commission.getFaxComm());
		commissionMailBean.setAdresse(adresseController.getLibelleAdresse(commission.getAdresse(), "<br>"));
		commissionMailBean.setCommentaireRetour(commission.getCommentRetourComm());
		return new CandidatureMailBean(campagneController.getCampagneEnService().getLibCamp(),candidatMailBean, formationMailBean, commissionMailBean);
	}
	

	
	/** Envoie un email
	 * @param mailTo
	 * @param title
	 * @param text
	 */
	public void sendMail(String mailTo, String title, String text) {
		try {
			MimeMessage message = javaMailService.createMimeMessage();

			message.setFrom(new InternetAddress(environment.getProperty("mail.from.noreply")));

			InternetAddress[] internetAddresses = new InternetAddress[1];
			internetAddresses[0] = new InternetAddress(mailTo);
			message.setRecipients(Message.RecipientType.TO, internetAddresses);
			message.setSubject(title);
			text = text
					+ applicationContext.getMessage("mail.footer", null,
							Locale.getDefault());

			message.setContent(text, "text/html; charset=utf-8");
			message.setHeader("X-Mailer", "Java");
			message.setSentDate(new Date());

			javaMailService.send(message);

		} catch (AddressException e) {
			logger.error("Erreur lors de l'envoie du mail : " + e.getMessage());
		} catch (MessagingException e) {
			logger.error("Erreur lors de l'envoie du mail : " + e.getMessage());
		}
	}
	
	/** Envoie un mail grace a son code
	 * @param cod
	 * @param bean
	 */
	public void sendMailByCod(String mailAdr, String cod, MailBean bean, CandidatureMailBean candidatureMailBean) {
		Mail mail = getMailByCod(cod);
		sendMail(mailAdr, mail, bean, candidatureMailBean);
	}

	/**
	 * Envoie un mail
	 */
	/*public void sendMail(String mailAdr, Mail mail, MailBean bean){
		String contentMail = getTraduction(mail.getI18nCorpsMail());
		String sujetMail = getTraduction(mail.getI18nSujetMail());
		String varMail = getVarMail(mail.getCodMail(), mail.getTemIsModeleMail());
		if (varMail!=null && !varMail.equals("")){
			String[] tabSplit = varMail.split(";");
			for (String property : tabSplit){
				String propRegEx = "\\$\\{"+property+"\\}";
				contentMail = contentMail.replaceAll(propRegEx, bean.getValueProperty(property, formatterDate));				
			}
		}
		sendMail(mailAdr,sujetMail,contentMail);
	}*/
	
	/**
	 * Envoie un mail
	 */
	public void sendMail(String mailAdr, Mail mail, MailBean bean, CandidatureMailBean candidatureMailBean){		
		String contentMail = getTraduction(mail.getI18nCorpsMail());
		String sujetMail = getTraduction(mail.getI18nSujetMail());
		String varMail = getVarMail(mail);
		String varCandidature = getVarMailCandidature(mail.getCodMail());
		
		/*Suppression des if*/
		String contentWithoutIf = deleteIfFromMail(contentMail,bean,candidatureMailBean);
		while (contentWithoutIf!=null){
			contentMail = contentWithoutIf;
			contentWithoutIf = deleteIfFromMail(contentMail,bean,candidatureMailBean);
		}			
		/*Fin supression if*/
		
		contentMail = parseVar(contentMail, varMail, bean);
		contentMail = parseVar(contentMail, varCandidature, candidatureMailBean);
		sendMail(mailAdr,sujetMail,contentMail);
	}
	
	/** Parse les variables du mail
	 * @param contentMail
	 * @param var
	 * @param bean
	 * @return le contenu parsé
	 */
	public String parseVar(String contentMail, String var, MailBean bean){
		if (bean!=null && var!=null && !var.equals("")){
			String[] tabSplit = var.split(";");
			
			for (String property : tabSplit){
				String propRegEx = "\\$\\{"+property+"\\}";
				contentMail = contentMail.replaceAll(propRegEx, bean.getValueProperty(property));				
			}
		}
		return contentMail;
	}
	
	/** Parse les if d'un mail
	 * @param contentMail
	 * @param beanSpecifique
	 * @param candidatureMailBean
	 * @return le contenu parsé
	 */
	private String deleteIfFromMail(String contentMail, MailBean beanSpecifique, CandidatureMailBean candidatureMailBean){
		/*Les balsies IF*/
		String baliseDebutIf = "{if($";
		String baliseFinIf = ")}";
		
		/*On recherche la position de la balise if*/
		int indexDebutIf = contentMail.indexOf(baliseDebutIf, 0);
		if (indexDebutIf!=-1){
			/*elle existe donc on recherche la premiere position de la balise de fin du if*/
			int indexFinIf = contentMail.indexOf(baliseFinIf, indexDebutIf+baliseDebutIf.length());
			if (indexFinIf!=-1){
				/*Elle existe, on calcul la propriété*/
				String property = contentMail.substring(indexDebutIf+baliseDebutIf.length(), indexFinIf);
				String endIf = "{endif($"+property+")}";
				
				if (property!=null && !property.equals("")){
					String valueProperty = (beanSpecifique==null)?null:beanSpecifique.getValueProperty(property);
					String valuePropertyCandidature = (candidatureMailBean==null)?null:candidatureMailBean.getValueProperty(property);
					if (valueProperty!=null && !valueProperty.equals("")){
						contentMail = contentMail.replace(baliseDebutIf+property+baliseFinIf, "");
						contentMail = contentMail.replace(endIf, "");
						return contentMail;						
					}else if (valuePropertyCandidature!=null && !valuePropertyCandidature.equals("")){
						contentMail = contentMail.replace(baliseDebutIf+property+baliseFinIf, "");
						contentMail = contentMail.replace(endIf, "");
						return contentMail;	
					}
					else{
						int indexDebutEndIf = contentMail.indexOf(endIf, indexFinIf+baliseFinIf.length());
						if (indexDebutEndIf!=-1){
							String strToReplace = contentMail.substring(indexDebutIf, indexDebutEndIf+endIf.length());
							return contentMail.replace(strToReplace, "");
						}
					}

				}
				
			}				
		}
		return null;
	}
	
	/** Renvoie la variable de mail associée au cod mail
	 * @param isModel 
	 * @param codMail
	 * @return les variables d'un mail
	 */
	/*public String getVarMail(Mail mail){
		String codMail = mail.getCodMail();
		Boolean isModel = mail.getTemIsModeleMail();
		if (isModel && codMail!=null && codMail.startsWith(NomenclatureUtils.MAIL_STATUT_PREFIX)){
			return null;
		}else if (isModel && codMail!=null && codMail.equals(NomenclatureUtils.MAIL_CPT_MIN)){
			return NomenclatureUtils.MAIL_CPT_MIN_VAR;
		}else if (isModel && codMail!=null && codMail.equals(NomenclatureUtils.MAIL_ID_OUBLIE)){
			return NomenclatureUtils.MAIL_ID_OUBLIE_VAR;
		}else if (isModel && codMail!=null && codMail.equals(NomenclatureUtils.MAIL_MOD_MAIL_CPT_MIN)){
			return NomenclatureUtils.MAIL_MOD_MAIL_CPT_MIN_VAR;
		}else if (isModel && codMail!=null && codMail.equals(NomenclatureUtils.MAIL_CANDIDATURE)){
			return null;
		}else if (isModel && codMail!=null && codMail.equals(NomenclatureUtils.MAIL_ANNUL_CANDIDATURE)){
			return null;
		}else if (isModel && codMail!=null && codMail.equals(NomenclatureUtils.MAIL_CONFIRM_CANDIDATURE)){
			return null;
		}else if (isModel && codMail!=null && codMail.equals(NomenclatureUtils.MAIL_DESIST_CANDIDATURE)){
			return null;
		}else if (isModel && codMail!=null && codMail.equals(NomenclatureUtils.MAIL_TYPE_TRAIT_AD)){
			return null;
		}else if (isModel && codMail!=null && codMail.equals(NomenclatureUtils.MAIL_TYPE_TRAIT_AC)){
			return null;
		}else if (isModel && codMail!=null && codMail.equals(NomenclatureUtils.MAIL_COMMISSION_ANNUL)){
			return null;
		}else if (isModel && codMail!=null && codMail.equals(NomenclatureUtils.MAIL_COMMISSION_PROP)){
			return null;
		}
		return NomenclatureUtils.MAIL_DEC_VAR;
	}*/
	
	/**
	 * @param mail
	 * @return les variables de mail
	 */
	public String getVarMail(Mail mail){
		String codMail = mail.getCodMail();
		/*Mail de compte a minima*/
		if (codMail!=null && codMail.equals(NomenclatureUtils.MAIL_CPT_MIN)){
			return NomenclatureUtils.MAIL_GEN_VAR+";"+NomenclatureUtils.MAIL_CPT_MIN_VAR;
		}else if (codMail!=null && codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_ID_OUBLIE)){
			/*Mail de d'identifiants oubliés*/
			return NomenclatureUtils.MAIL_GEN_VAR+";"+NomenclatureUtils.MAIL_CPT_MIN_ID_OUBLIE_VAR;
		}else if (codMail!=null && codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL)){
			/*Mail de modification de mail*/
			return NomenclatureUtils.MAIL_GEN_VAR+";"+NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL_VAR;
		}else if (mail.getTypeAvis()!=null){
			/*Mail de type de decisoion*/
			TypeAvis type = mail.getTypeAvis();
			String var = NomenclatureUtils.MAIL_DEC_VAR;
			if (type.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_DEF)){
				var = var + ";" + NomenclatureUtils.MAIL_DEC_VAR_DEFAVORABLE;
			}else if (type.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_PRESELECTION)){
				var = var + ";" + NomenclatureUtils.MAIL_DEC_VAR_PRESELECTION;
			}else if (type.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_LISTE_COMP)){
				var = var + ";" + NomenclatureUtils.MAIL_DEC_VAR_LISTE_COMP;
			}
			return var;
		}
		return null;
	}
	
	
	/**
	 * @param codMail
	 * @return les variables de mail génériques
	 */
	public String getVarMailCandidature(String codMail){
		if (codMail!=null && (codMail.equals(NomenclatureUtils.MAIL_CPT_MIN) || 
				codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_ID_OUBLIE) || 
				codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL))){
			return null;
		}else{
			return NomenclatureUtils.MAIL_GEN_VAR+";"+NomenclatureUtils.MAIL_CANDIDAT_GEN_VAR+";"+NomenclatureUtils.MAIL_FORMATION_GEN_VAR+";"+NomenclatureUtils.MAIL_COMMISSION_GEN_VAR;
		}
	}
	
	/** Renvoie la traduction suivant la local désirée
	 * @param i18n
	 * @param codLangue
	 * @return la traduction
	 */
	private String getTraductionByLocal(I18n i18n, String codLangue){
		Optional<I18nTraduction> i18nOpt = i18n.getI18nTraductions().stream().filter(i18nLg->i18nLg.getLangue().getCodLangue().equals(codLangue)).findAny();
		if (i18nOpt.isPresent()){
			return i18nOpt.get().getValTrad();
		}
		return null;
	}
	
	/**
	 * @param i18n
	 * @return la traduction si elle existe, sinon renvoi la langue par défaut
	 */
	private String getTraduction(I18n i18n){
		String valTrad = getTraductionByLocal(i18n, UI.getCurrent().getLocale().getLanguage());
		if (valTrad!=null){
			return valTrad;
		}else{
			return getTraductionByLocal(i18n, tableRefController.getLangueDefault().getCodLangue());
		}
	}
}
