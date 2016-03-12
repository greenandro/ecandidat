package fr.univlorraine.ecandidat.views.windows;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

import fr.univlorraine.ecandidat.controllers.BatchController;
import fr.univlorraine.ecandidat.entities.ecandidat.Batch;
import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto_;
import fr.univlorraine.ecandidat.entities.ecandidat.Batch_;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;


/** 
 * Fenêtre de visu de l'histo des batchs
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class AdminBatchHistoWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 2289862475384177512L;

	public static final String[] BATCH_HISTO_FIELDS_ORDER = {BatchHisto_.stateBatchHisto.getName(),BatchHisto_.dateDebBatchHisto.getName(),BatchHisto_.dateFinBatchHisto.getName(),"duree"};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient BatchController batchController;

	/* Composants */
	private TableFormating batchHistoTable;
	private BeanItemContainer<BatchHisto> container;
	private Button btnFermer;
	private Button btnRefresh;

	/**
	 * Crée une fenêtre de visu de l'histo d'un batch
	 * @param batch le batch à visualiser
	 */
	public AdminBatchHistoWindow(Batch batch) {
		/* Style */
		setModal(true);
		setWidth(700,Unit.PIXELS);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("batchHisto.window", new Object[]{batch.getCodBatch()}, UI.getCurrent().getLocale()));

		/* Table */
		container = new BeanItemContainer<BatchHisto>(BatchHisto.class, batchController.getBatchHisto(batch));
		batchHistoTable = new TableFormating(null,container);
		batchHistoTable.addGeneratedColumn("duree", new ColumnGenerator() {
			
			/*** serialVersionUID*/
			private static final long serialVersionUID = 7461290324017459118L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final BatchHisto batchHisto = (BatchHisto) itemId;
				if (batchHisto.getDateFinBatchHisto()!=null)
				{
					LocalDateTime dateDeb = LocalDateTime.from(batchHisto.getDateDebBatchHisto());
					Long minutes = dateDeb.until(batchHisto.getDateFinBatchHisto(), ChronoUnit.MINUTES);
					dateDeb = dateDeb.plusMinutes(minutes);
					Long secondes = dateDeb.until(batchHisto.getDateFinBatchHisto(), ChronoUnit.SECONDS);
					return new Label(applicationContext.getMessage("batch.histo.duree", new Object[]{minutes,secondes}, UI.getCurrent().getLocale()));
				}
				return null;
			}
		});
		batchHistoTable.setSizeFull();
		batchHistoTable.setVisibleColumns((Object[]) BATCH_HISTO_FIELDS_ORDER);
		for (String fieldName : BATCH_HISTO_FIELDS_ORDER) {
			batchHistoTable.setColumnHeader(fieldName, applicationContext.getMessage("batchHisto.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		batchHistoTable.setSortContainerPropertyId(Batch_.codBatch.getName());
		batchHistoTable.setColumnCollapsingAllowed(true);
		batchHistoTable.setColumnReorderingAllowed(true);
		batchHistoTable.setSelectable(true);

		layout.addComponent(batchHistoTable);
		
		
		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnFermer = new Button(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnFermer.addClickListener(e -> close());
		buttonsLayout.addComponent(btnFermer);
		buttonsLayout.setComponentAlignment(btnFermer, Alignment.MIDDLE_LEFT);
		
		btnRefresh = new Button(applicationContext.getMessage("btnRefresh", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
		btnRefresh.addClickListener(e -> {
			container.removeAllItems();
			container.addAll(batchController.getBatchHisto(batch));
		});
		buttonsLayout.addComponent(btnRefresh);
		buttonsLayout.setComponentAlignment(btnRefresh, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

}
