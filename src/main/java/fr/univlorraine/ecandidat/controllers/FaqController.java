package fr.univlorraine.ecandidat.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Faq;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.repositories.FaqRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.ScolFaqWindow;

/**
 * Gestion de l'entité faq
 * @author Kevin Hergalant
 */
@Component
public class FaqController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient FaqRepository faqRepository;
	
	private List<Faq> listeFaq;
	
	/**
	 * @return liste des faq
	 */
	public List<Faq> getFaq() {
		if (listeFaq==null){
			listeFaq = faqRepository.findAllByOrderByOrderFaqAsc();
		}
		return listeFaq;
	}
	
	/**
	 * Recharge la faq
	 */
	public void reloadFaq() {
		listeFaq = null;
		getFaq();
		loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_FAQ);
	}
	
	/**
	 * Ouvre une fenêtre d'édition d'un nouveau faq.
	 */
	public void editNewFaq() {
		Faq faq = new Faq();
		faq.setI18nQuestion(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_FAQ_QUESTION)));
		faq.setI18nReponse(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_FAQ_REPONSE)));
		UI.getCurrent().addWindow(new ScolFaqWindow(faq));
	}
	
	/**
	 * Ouvre une fenêtre d'édition de faq.
	 * @param faq
	 */
	public void editFaq(Faq faq) {
		Assert.notNull(faq);

		/* Verrou */
		if (!lockController.getLockOrNotify(faq, null)) {
			return;
		}
		ScolFaqWindow window = new ScolFaqWindow(faq);
		window.addCloseListener(e->lockController.releaseLock(faq));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un faq
	 * @param faq
	 */
	public void saveFaq(Faq faq) {
		Assert.notNull(faq);
		

		/* Verrou */
		if (faq.getIdFaq()!=null && !lockController.getLockOrNotify(faq, null)) {
			return;
		}
		faq.setI18nQuestion(i18nController.saveI18n(faq.getI18nQuestion()));
		faq.setI18nReponse(i18nController.saveI18n(faq.getI18nReponse()));
		faq = faqRepository.saveAndFlush(faq);
		reloadFaq();
		lockController.releaseLock(faq);
	}
	
	/**
	 * Supprime une faq
	 * @param faq
	 */
	public void deleteFaq(Faq faq) {
		Assert.notNull(faq);
		
		/* Verrou */
		if (!lockController.getLockOrNotify(faq, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("faqAvis.window.confirmDelete", new Object[]{faq.getLibFaq()}, UI.getCurrent().getLocale()), applicationContext.getMessage("faqAvis.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(faq, null)) {
				faqRepository.delete(faq);
				/* Suppression du lock */
				lockController.releaseLock(faq);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(faq);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
}
