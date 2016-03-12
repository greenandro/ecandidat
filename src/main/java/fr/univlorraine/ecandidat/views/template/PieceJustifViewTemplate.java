package fr.univlorraine.ecandidat.views.template;

import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.PieceJustifController;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader.OnDemandStreamResource;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/** Template de la vue des PieceJustif, utilisé par la scol et ctrCand
 * @author Kevin Hergalant
 *
 */
public class PieceJustifViewTemplate extends VerticalLayout{

	/** serialVersionUID **/
	private static final long serialVersionUID = 8432471097989849796L;

	public static final String NAME = "scolPieceJustifView";

	String[] FIELDS_ORDER;
	String[] FIELDS_ORDER_FILE = {PieceJustif_.codPj.getName(),PieceJustif_.libPj.getName(),PieceJustif_.tesPj.getName(),PieceJustif_.temCommunPj.getName(),PieceJustif_.temConditionnelPj.getName(),PieceJustif_.fichier.getName()};
	String[] FIELDS_ORDER_NO_FILE = {PieceJustif_.codPj.getName(),PieceJustif_.libPj.getName(),PieceJustif_.tesPj.getName(),PieceJustif_.temCommunPj.getName(),PieceJustif_.temConditionnelPj.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient PieceJustifController pieceJustifController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient I18nController i18nController;
	
	//protected Boolean dematCtrCand0 = true;
	protected Boolean isVisuPjCommunMode = true;
	protected Boolean isReadOnly = false;

	/* Composants */	
	protected Label titleParam = new Label();
	protected Button btnNew = new Button(FontAwesome.PLUS);
	protected Button btnEdit = new Button(FontAwesome.PENCIL);
	protected HorizontalLayout buttonsLayout = new HorizontalLayout();
	protected BeanItemContainer<PieceJustif> container = new BeanItemContainer<PieceJustif>(PieceJustif.class);
	protected TableFormating pieceJustifTable = new TableFormating(null, container);

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);
		
		/* Titre */
		
		titleParam.addStyleName(ValoTheme.LABEL_H2);
		addComponent(titleParam);
		
		/* Boutons */
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);


		btnNew.setCaption(applicationContext.getMessage("pieceJustif.btnNouveau", null, UI.getCurrent().getLocale()));
		btnNew.setEnabled(true);
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);


		btnEdit.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (pieceJustifTable.getValue() instanceof PieceJustif) {
				pieceJustifController.editPieceJustif((PieceJustif) pieceJustifTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);
		
		Button btnDelete = new Button(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (pieceJustifTable.getValue() instanceof PieceJustif) {
				pieceJustifController.deletePieceJustif((PieceJustif) pieceJustifTable.getValue());
			}			
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);


		/* Table des pieceJustifs */
		pieceJustifTable.addBooleanColumn(PieceJustif_.tesPj.getName());
		pieceJustifTable.addBooleanColumn(PieceJustif_.temCommunPj.getName());
		pieceJustifTable.addBooleanColumn(PieceJustif_.temConditionnelPj.getName());
		if (!fileController.getModeDematBackoffice().equals(ConstanteUtils.TYPE_FICHIER_STOCK_NONE)){
			pieceJustifTable.addGeneratedColumn(PieceJustif_.fichier.getName(), new ColumnGenerator() {
				/*** serialVersionUID*/
				private static final long serialVersionUID = -1750183076315269277L;

				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					final PieceJustif pieceJustif = (PieceJustif) itemId;
					if (pieceJustif.getFichier()==null){
						if (isVisuPjCommunMode && !isReadOnly){
							Button btnAdd = new Button(FontAwesome.PLUS);
							btnAdd.setDescription(applicationContext.getMessage("file.btnAdd", null, UI.getCurrent().getLocale()));
							btnAdd.addClickListener(e->pieceJustifController.addFileToPieceJustificative(pieceJustif));
							return btnAdd;
						}
						return null;
					}else{
						HorizontalLayout hl = new HorizontalLayout();
						hl.setSpacing(true);
						hl.setWidth(100, Unit.PERCENTAGE);
						
						if (isVisuPjCommunMode && !isReadOnly){
							Button btnDel = new Button(FontAwesome.MINUS);
							btnDel.setDescription(applicationContext.getMessage("file.btnDel", null, UI.getCurrent().getLocale()));
							btnDel.addClickListener(e->pieceJustifController.deleteFileToPieceJustificative(pieceJustif));
							hl.addComponent(btnDel);
							hl.setComponentAlignment(btnDel, Alignment.MIDDLE_CENTER);
						}
												
						Button btnDownload = new Button(FontAwesome.DOWNLOAD);
						btnDownload.setDescription(applicationContext.getMessage("file.btnDownload", null, UI.getCurrent().getLocale()));
						hl.addComponent(btnDownload);
						hl.setComponentAlignment(btnDownload, Alignment.MIDDLE_CENTER);
						new OnDemandFileDownloader(new OnDemandStreamResource() {
							/*** serialVersionUID*/
							private static final long serialVersionUID = -7293680269133650077L;

							@Override
							public InputStream getStream() {
								InputStream is = fileController.getInputStreamFromFichier(pieceJustif.getFichier(),true);
								if (is != null){
									btnDownload.setEnabled(true);
									return is;
								}
								btnDownload.setEnabled(true);
								return null;
							}
							
							@Override
							public String getFilename() {
								return pieceJustif.getFichier().getNomFichier();
							}
						},btnDownload);
						Label label = new Label(pieceJustif.getFichier().getNomFichier());
						hl.addComponent(label);
						hl.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
						hl.setExpandRatio(label, 1.0f);
						
						return hl;
					}				
				}
			});
			FIELDS_ORDER = FIELDS_ORDER_FILE;
		}else{
			FIELDS_ORDER = FIELDS_ORDER_NO_FILE;
		}
		
		pieceJustifTable.setSizeFull();
		pieceJustifTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			pieceJustifTable.setColumnHeader(fieldName, applicationContext.getMessage("pieceJustif.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		pieceJustifTable.setSortContainerPropertyId(PieceJustif_.codPj.getName());
		pieceJustifTable.setColumnCollapsingAllowed(true);
		pieceJustifTable.setColumnReorderingAllowed(true);
		pieceJustifTable.setSelectable(true);
		pieceJustifTable.setImmediate(true);
		pieceJustifTable.addItemSetChangeListener(e -> pieceJustifTable.sanitizeSelection());
		pieceJustifTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de pieceJustif sont actifs seulement si une pieceJustif est sélectionnée. */
			boolean pieceJustifIsSelected = pieceJustifTable.getValue() instanceof PieceJustif;
			btnEdit.setEnabled(pieceJustifIsSelected);
			btnDelete.setEnabled(pieceJustifIsSelected);
		});
		addComponent(pieceJustifTable);
		setExpandRatio(pieceJustifTable, 1);
	}
	
}
