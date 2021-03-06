package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.siscol.AdresseForm;

/**
 * Fenêtre d'édition d'adresse
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CandidatAdresseWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 8279285838139858898L;

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;

	/* Composants */
	private CustomBeanFieldGroup<Adresse> fieldGroupAdresse;
	private Button btnEnregistrer;
	private Button btnAnnuler;
	private AdresseWindowListener adresseWindowListener;
	/**
	 * Crée une fenêtre d'édition d'adresse
	 * @param adresse l'adresse à éditer
	 */
	public CandidatAdresseWindow(Adresse adresse) {
		/* Style */
		setModal(true);
		setWidth(550,Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("adresse.window", null, UI.getCurrent().getLocale()));

		
		/*Layout adresse*/		
		fieldGroupAdresse = new CustomBeanFieldGroup<Adresse>(Adresse.class,ConstanteUtils.TYP_FORM_ADR);
		fieldGroupAdresse.setItemDataSource(adresse);
		AdresseForm adresseForm = new AdresseForm(fieldGroupAdresse, false);
		layout.addComponent(adresseForm);
		layout.setExpandRatio(adresseForm, 1);
		
		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new Button(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnEnregistrer = new Button(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);		
		btnEnregistrer.addClickListener(e -> {			
			try {			
				/* Valide la saisie de l'adresse*/
				fieldGroupAdresse.commit();
				/* Enregistre la commission saisie */
				adresseWindowListener.btnOkClick(fieldGroupAdresse.getItemDataSource().getBean());
				/* Ferme la fenêtre */
				close();
			} catch (CommitException ce) {			
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}
	
	/**
	 * Défini le 'AdresseWindowListener' utilisé
	 * @param adresseWindowListener
	 */
	public void addAdresseWindowListener(AdresseWindowListener adresseWindowListener) {
		this.adresseWindowListener = adresseWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface AdresseWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param adresse 
		 */
		public void btnOkClick(Adresse adresse);

	}
}
