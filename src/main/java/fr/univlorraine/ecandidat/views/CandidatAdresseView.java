package fr.univlorraine.ecandidat.views;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.utils.ListenerUtils.AdresseListener;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;
 


/** Vue pour l'adresse du candidat
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CandidatAdresseView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatAdresseView extends CandidatViewTemplate implements View, AdresseListener{	

	/** serialVersionUID **/
	private static final long serialVersionUID = 5842232696061936906L;

	public static final String NAME = "candidatAdresseView";

	public static final String[] FIELDS_ORDER = {SimpleTablePresentation.champsTitle,SimpleTablePresentation.champsValue};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;

	/*Composants*/	
	private BeanItemContainer<SimpleTablePresentation> container = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	private TableFormating table = new TableFormating(null, container);
	private Label noInfoLabel = new Label();
	

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		super.init();
		setNavigationButton(CandidatInfoPersoView.NAME, CandidatBacView.NAME);
			
		/*Edition des donneés d'adresse*/	
		Button btnEdit = new Button(FontAwesome.PENCIL);
		btnEdit.setCaption(applicationContext.getMessage("adresse.edit.btn", null, UI.getCurrent().getLocale()));
		btnEdit.addClickListener(e -> {
			candidatController.editAdresse(cptMin, this);
		});
		addGenericButton(btnEdit, Alignment.MIDDLE_LEFT);
		
		noInfoLabel.setValue(applicationContext.getMessage("adresse.noinfo", null, UI.getCurrent().getLocale()));
		addGenericComponent(noInfoLabel);
		
		/*L'adresse*/		
		table.setSizeFull();
		table.setVisibleColumns((Object[]) FIELDS_ORDER);
		table.setColumnCollapsingAllowed(false);
		table.setColumnReorderingAllowed(false);
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table.setSelectable(false);
		table.setImmediate(true);		
		table.setColumnWidth(SimpleTablePresentation.champsTitle, 250);
		table.setCellStyleGenerator((components, itemId, columnId)->{
			if (columnId!=null && columnId.equals(SimpleTablePresentation.champsTitle)){
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		addGenericComponent(table);		
		setGenericExpandRatio(table);
	}

	
	/**
	 * Met a jour les composants adresse
	 */
	private void majComponentsAdresse(Candidat candidat){
		if (candidat==null){
			setButtonVisible(false);
			table.setVisible(false);
			noInfoLabel.setVisible(false);
			return;
		}else{
			Adresse adresse = candidat.getAdresse();
			if (adresse == null){
				noInfoLabel.setVisible(true);
				table.setVisible(false);
				setGenericLayoutSizeFull(false);
			}else{
				noInfoLabel.setVisible(false);
				table.setVisible(true);
				List<SimpleTablePresentation> liste = candidatController.getInformationsAdresse(adresse);
				container.removeAllItems();
				container.addAll(liste);
				table.setPageLength(liste.size());
				setGenericLayoutSizeFull(true);
			}			
		}	
	}
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		if (majView(applicationContext.getMessage("adresse.title", null, UI.getCurrent().getLocale()), true,  ConstanteUtils.LOCK_ADRESSE)){
			majComponentsAdresse(candidat);
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		candidatController.unlockCandidatRessource(cptMin, ConstanteUtils.LOCK_ADRESSE);
		super.detach();		
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.AdresseListener#adresseModified(fr.univlorraine.ecandidat.entities.ecandidat.Candidat)
	 */
	@Override
	public void adresseModified(Candidat candidat) {
		cptMin.setCandidat(candidat);
		majComponentsAdresse(candidat);
	}

}
