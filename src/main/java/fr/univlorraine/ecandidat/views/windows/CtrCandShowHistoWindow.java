package fr.univlorraine.ecandidat.views.windows;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.IndividuController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision_;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Fenêtre de visu de l'histo des décisions d'une candidature
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CtrCandShowHistoWindow extends Window{
	

	/** serialVersionUID **/
	private static final long serialVersionUID = -7776558654950981770L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient IndividuController individuController;
	
	public static final String[] FIELDS_ORDER = {
		TypeDecisionCandidature_.datCreTypeDecCand.getName(),
		TypeDecisionCandidature_.userCreTypeDecCand.getName(),
		TypeDecisionCandidature_.typeDecision.getName()+"."+TypeDecision_.libTypDec.getName(),
		TypeDecisionCandidature_.motivationAvis.getName()+"."+MotivationAvis_.libMotiv.getName(),
		TypeDecisionCandidature_.temValidTypeDecCand.getName(),
		TypeDecisionCandidature_.temAppelTypeDecCand.getName(),
		TypeDecisionCandidature_.commentTypeDecCand.getName(),
		TypeDecisionCandidature_.listCompRangTypDecCand.getName(),
		TypeDecisionCandidature_.preselectDateTypeDecCand.getName(),
		TypeDecisionCandidature_.preselectHeureTypeDecCand.getName(),
		TypeDecisionCandidature_.preselectLieuTypeDecCand.getName()};
	
	/* Composants */

	private Button btnClose;
	

	/**
	 * Crée une fenêtre de visu de l'histo des décisions d'une candidature
	 * @param candidature la candidature à éditer
	 */
	public CtrCandShowHistoWindow(Candidature candidature) {
		/* Style */
		setModal(true);
		setWidth(100,Unit.PERCENTAGE);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("candidature.histoavis.window",  new Object[]{candidatController.getLibelleTitle(candidature.getCandidat().getCompteMinima())}, UI.getCurrent().getLocale()));
		Formation f = candidature.getFormation();
		String msg = applicationContext.getMessage("candidature.histoavis.window.detail",  new Object[]{f.getCommission().getLibComm(),f.getLibForm()}, UI.getCurrent().getLocale());
		Label label = new Label(msg);
		label.addStyleName(ValoTheme.LABEL_H3);
		layout.addComponent(label);
		
		BeanItemContainer<TypeDecisionCandidature> container = new BeanItemContainer<TypeDecisionCandidature>(TypeDecisionCandidature.class, candidature.getTypeDecisionCandidatures());
		container.addNestedContainerProperty(TypeDecisionCandidature_.typeDecision.getName()+"."+TypeDecision_.libTypDec.getName());
		container.addNestedContainerProperty(TypeDecisionCandidature_.motivationAvis.getName()+"."+MotivationAvis_.libMotiv.getName());
		TableFormating motivationAvisTable = new TableFormating(null, container);
		motivationAvisTable.addBooleanColumn(TypeDecisionCandidature_.temValidTypeDecCand.getName());
		motivationAvisTable.setSizeFull();
		motivationAvisTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			motivationAvisTable.setColumnHeader(fieldName, applicationContext.getMessage("candidature.histoavis." + fieldName, null, UI.getCurrent().getLocale()));
		}
		motivationAvisTable.setSortContainerPropertyId(TypeDecisionCandidature_.datCreTypeDecCand.getName());
		
		motivationAvisTable.addGeneratedColumn(TypeDecisionCandidature_.userCreTypeDecCand.getName(), new ColumnGenerator() {

			/*** serialVersionUID*/
			private static final long serialVersionUID = 1368300795292841902L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final TypeDecisionCandidature typeDec = (TypeDecisionCandidature) itemId;
				String user = typeDec.getUserCreTypeDecCand();
				if (user==null){
					return "";
				}else{
					Individu ind = individuController.getIndividu(user);
					if (ind!=null && ind.getLibelleInd()!=null){
						return ind.getLibelleInd();
					}
				}
				return user;
			}
		});
		
		
		motivationAvisTable.setSortAscending(false);
		motivationAvisTable.setColumnCollapsingAllowed(true);
		motivationAvisTable.setColumnReorderingAllowed(true);
		motivationAvisTable.setSelectable(false);
		motivationAvisTable.setImmediate(true);
		layout.addComponent(motivationAvisTable);
		layout.setExpandRatio(motivationAvisTable, 1);
		
		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);
		
		btnClose = new Button(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnClose.addClickListener(e -> close());
		buttonsLayout.addComponent(btnClose);
		buttonsLayout.setComponentAlignment(btnClose, Alignment.MIDDLE_CENTER);
		
		/* Centre la fenêtre */
		center();
	}
}
