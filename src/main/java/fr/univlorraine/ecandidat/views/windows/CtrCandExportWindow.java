package fr.univlorraine.ecandidat.views.windows;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatureCtrCandController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.utils.bean.export.ExportListCandidatureOption;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader.OnDemandStreamResource;

/**
 * Fenêtre de choix d'option d'export
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CtrCandExportWindow extends Window {
	

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8977534780022796350L;
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatureCtrCandController ctrCandCandidatureController;
	
	/**
	 * Crée une fenêtre de choix d'option d'export
	 * @param listeCand les candidatures à exporter
	 */
	public CtrCandExportWindow(List<Candidature> listeCand) {
		/* Style */
		setModal(true);
		setWidth(700,Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		//layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("export.window", null, UI.getCurrent().getLocale()));
		
		/*Options*/
		LinkedHashSet<ExportListCandidatureOption> setOptionLeft = new LinkedHashSet<ExportListCandidatureOption>();
		setOptionLeft.add(new ExportListCandidatureOption("numDossierHide",applicationContext.getMessage("export.option.numDossier", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("civiliteHide",applicationContext.getMessage("export.option.civilite", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("nomHide",applicationContext.getMessage("export.option.nom", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("prenomHide",applicationContext.getMessage("export.option.prenom", null, UI.getCurrent().getLocale())));		
		setOptionLeft.add(new ExportListCandidatureOption("nationaliteHide",applicationContext.getMessage("export.option.nationalite", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("langueHide",applicationContext.getMessage("export.option.langue", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("telHide",applicationContext.getMessage("export.option.tel", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("telPortHide",applicationContext.getMessage("export.option.telPort", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("mailHide",applicationContext.getMessage("export.option.mail", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("adresseHide",applicationContext.getMessage("export.option.adresse", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("etablissementHide",applicationContext.getMessage("export.option.etablissement", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("lastDipHide",applicationContext.getMessage("export.option.lastDip", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("codFormHide",applicationContext.getMessage("export.option.codForm", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("libFormHide",applicationContext.getMessage("export.option.libForm", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("dateCandHide",applicationContext.getMessage("export.option.dateCand", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("dateTransHide",applicationContext.getMessage("export.option.dateTrans", null, UI.getCurrent().getLocale())));
		
		
		LinkedHashSet<ExportListCandidatureOption> setOptionRight = new LinkedHashSet<ExportListCandidatureOption>();		
		setOptionRight.add(new ExportListCandidatureOption("statutHide",applicationContext.getMessage("export.option.statut", null, UI.getCurrent().getLocale())));			
		setOptionRight.add(new ExportListCandidatureOption("dateModStatutHide",applicationContext.getMessage("export.option.dateModStatut", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("dateReceptHide",applicationContext.getMessage("export.option.dateRecept", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("dateCompletHide",applicationContext.getMessage("export.option.dateComplet", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("dateIncompletHide",applicationContext.getMessage("export.option.dateIncomplet", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("typeTraitHide",applicationContext.getMessage("export.option.typeTrait", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("typeTraitValidHide",applicationContext.getMessage("export.option.typeTraitValid", null, UI.getCurrent().getLocale())));		
		setOptionRight.add(new ExportListCandidatureOption("dateModPjHide",applicationContext.getMessage("export.option.dateModPj", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("commissionHide",applicationContext.getMessage("export.option.commission", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("avisCandHide",applicationContext.getMessage("export.option.avisCand", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("avisValidHide",applicationContext.getMessage("export.option.avisValid", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("dateValidHide",applicationContext.getMessage("export.option.dateValid", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("motifHide",applicationContext.getMessage("export.option.motif", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("rangHide",applicationContext.getMessage("export.option.rang", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("preselectionHide",applicationContext.getMessage("export.option.preselection", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("commentaireHide",applicationContext.getMessage("export.option.commentaire", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("confirmHide",applicationContext.getMessage("export.option.confirm", null, UI.getCurrent().getLocale())));
		
		LinkedHashSet<ExportListCandidatureOption> allOptions = new LinkedHashSet<ExportListCandidatureOption>();
		allOptions.addAll(setOptionLeft);
		allOptions.addAll(setOptionRight);
		
		
		BeanItemContainer<ExportListCandidatureOption> containerLeft = new BeanItemContainer<ExportListCandidatureOption>(ExportListCandidatureOption.class,setOptionLeft);	
		BeanItemContainer<ExportListCandidatureOption> containerRight = new BeanItemContainer<ExportListCandidatureOption>(ExportListCandidatureOption.class,setOptionRight);
		
		Label label = new Label(applicationContext.getMessage("export.caption", null, UI.getCurrent().getLocale()));
		label.addStyleName(ValoTheme.LABEL_BOLD);
		label.setSizeUndefined();
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		
		HorizontalLayout hlCoche = new HorizontalLayout();
		hlCoche.setWidth(100, Unit.PERCENTAGE);
		hlCoche.setSpacing(true);
		layout.addComponent(hlCoche);
		
		OptionGroup multiOptionGroupLeft = new OptionGroup(null,containerLeft);		
		multiOptionGroupLeft.setMultiSelect(true);
		multiOptionGroupLeft.setImmediate(true);
		multiOptionGroupLeft.setItemCaptionPropertyId("caption");
		multiOptionGroupLeft.setItemCaptionMode(ItemCaptionMode.PROPERTY);		
		hlCoche.addComponent(multiOptionGroupLeft);
		multiOptionGroupLeft.setValue(setOptionLeft);
		
		OptionGroup multiOptionGroupRight = new OptionGroup(null,containerRight);		
		multiOptionGroupRight.setMultiSelect(true);
		multiOptionGroupRight.setImmediate(true);
		multiOptionGroupRight.setItemCaptionPropertyId("caption");
		multiOptionGroupRight.setItemCaptionMode(ItemCaptionMode.PROPERTY);		
		hlCoche.addComponent(multiOptionGroupRight);
		multiOptionGroupRight.setValue(setOptionRight);

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		Button btnAnnuler = new Button(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);
		
		Button btnAllCheck = new Button(applicationContext.getMessage("export.btn.check", null, UI.getCurrent().getLocale()), FontAwesome.CHECK_SQUARE_O);
		btnAllCheck.addClickListener(e->{
			multiOptionGroupLeft.setValue(setOptionLeft);
			multiOptionGroupRight.setValue(setOptionRight);
		});
		buttonsLayout.addComponent(btnAllCheck);
		buttonsLayout.setComponentAlignment(btnAllCheck, Alignment.MIDDLE_CENTER);
		
		Button btnAllDecheck = new Button(applicationContext.getMessage("export.btn.uncheck", null, UI.getCurrent().getLocale()), FontAwesome.SQUARE_O);
		btnAllDecheck.addClickListener(e->{
			multiOptionGroupLeft.setValue(null);
			multiOptionGroupRight.setValue(null);
		});
		buttonsLayout.addComponent(btnAllDecheck);
		buttonsLayout.setComponentAlignment(btnAllDecheck, Alignment.MIDDLE_CENTER);

		Button btnExport = new Button(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()), FontAwesome.FILE_EXCEL_O);
		btnExport.addStyleName(ValoTheme.BUTTON_PRIMARY);
		new OnDemandFileDownloader(new OnDemandStreamResource() {
			private static final long serialVersionUID = -7293680269133650077L;

			@SuppressWarnings("unchecked")
			@Override
			public InputStream getStream() {
				if (listeCand.size()==0){
					btnExport.setEnabled(true);
					return null;
				}
				
				LinkedHashSet<ExportListCandidatureOption> setCoche = new LinkedHashSet<ExportListCandidatureOption>();
				setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupLeft.getValue());
				setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupRight.getValue());
				if (setCoche.size()==0){
					btnExport.setEnabled(true);
					return null;
				}
				
				InputStream is = ctrCandCandidatureController.generateExport(listeCand, allOptions, setCoche);
				if (is != null){
					btnExport.setEnabled(true);
					return is;
				}
				btnExport.setEnabled(true);
				return null;
			}
			
			@Override
			public String getFilename() {
				return "candidatures.xlsx";
			}
		},btnExport);
		
		buttonsLayout.addComponent(btnExport);
		buttonsLayout.setComponentAlignment(btnExport, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}
}
