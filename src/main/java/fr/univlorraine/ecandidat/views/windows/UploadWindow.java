package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.services.file.FileCustom;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.Uploader;

/**
 * Fenêtre d'upoload de fichier
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class UploadWindow extends Window {
	private static final long serialVersionUID = 1L;

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	
	@Resource
	private transient FileController fileController;
	
	@Resource
	private transient ParametreController parametreController;
	
	/*Composants*/	
	private Upload uploaderComponent = new Upload();
	private Uploader uploader;
	private Boolean error = false;
	private HorizontalLayout infoLayout = new HorizontalLayout();
	private Label infoLabel = new Label("");

	/** Listeners */
	UploadWindowListener uploadWindowListener;
	
	public void addUploadWindowListener(UploadWindowListener uploadWindowListener){
		this.uploadWindowListener = uploadWindowListener;
	}

	/** Crée une fenêtre d'upoload de fichier
	 * @param prefixe
	 * @param typeFichier
	 */
	public UploadWindow(String prefixe, String typeFichier) {
		
		/* Style */
		setWidth(650, Unit.PIXELS);
		setModal(true);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("window.upload.title", null, Locale.getDefault()));
		
		long UPLOAD_LIMIT = parametreController.getFileMaxSize();

		/* Texte */
		HorizontalLayout hlComponent = new HorizontalLayout();
		hlComponent.setSpacing(true);
		hlComponent.setMargin(true);
		Label textLabel = new Label(applicationContext.getMessage("window.upload.message", new Object[]{UPLOAD_LIMIT}, Locale.getDefault()));
		hlComponent.addComponent(textLabel);
		hlComponent.setComponentAlignment(textLabel, Alignment.MIDDLE_LEFT);
		layout.addComponent(hlComponent);
		
		/*Info*/
		infoLayout = new HorizontalLayout();
		infoLabel = new Label("");
		infoLayout.addComponent(infoLabel);
		infoLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);
		infoLayout.setVisible(false);

		/*Uploader*/
		uploader = new Uploader(prefixe,typeFichier);
		uploaderComponent = new Upload(null, uploader);
		
		hlComponent.addComponent(uploaderComponent);
		hlComponent.setComponentAlignment(uploaderComponent, Alignment.MIDDLE_RIGHT);
		uploaderComponent.setWidth(100,Unit.PERCENTAGE);

		
		uploaderComponent.setImmediate(true);
		uploaderComponent.setButtonCaption(applicationContext.getMessage("window.upload.btn", null, Locale.getDefault()));
		
		/*Ajout du startListener*/
		uploaderComponent.addStartedListener(e->{
			Integer sizeMax = fileController.getSizeMaxFileName();
			String fileName = e.getFilename();			
			
			if (!fileController.isFileNameOk(e.getFilename(),sizeMax)){
				displayError(applicationContext.getMessage("window.upload.toolongfilename", new Object[]{sizeMax}, Locale.getDefault()));
			}
			/*
		 	else if (!Arrays.asList(ConstanteUtils.TYPE_MIME_FILE_PDF).contains(e.getMIMEType()) && !Arrays.asList(ConstanteUtils.TYPE_MIME_FILE_JPG).contains(e.getMIMEType())){
				displayError(applicationContext.getMessage("window.upload.mimetype", null, Locale.getDefault()));
			}*/
			/*Verif de l'extension*/			
			else if (!MethodUtils.checkExtension(fileName)){
				displayError(applicationContext.getMessage("window.upload.mimetype", null, Locale.getDefault()));
			}
			
			else if (e.getContentLength() > UPLOAD_LIMIT*ConstanteUtils.UPLOAD_MO1) {
				displayError(applicationContext.getMessage("window.upload.toobigfile", null, Locale.getDefault()));
	        }
			else{
        		infoLabel.setValue(applicationContext.getMessage("window.upload.start", null, Locale.getDefault()));
	        	uploaderComponent.setEnabled(false);
	        	infoLayout.setVisible(true);
	        	infoLayout.setStyleName(ValoTheme.LABEL_SUCCESS);
	        }
		});
		uploaderComponent.addSucceededListener(uploader);
		uploaderComponent.addFailedListener(e->{
			if (!error){
				error = true;
				infoLabel.setValue(applicationContext.getMessage("window.upload.error", null, Locale.getDefault()));
				infoLayout.setVisible(true);
				infoLabel.setStyleName(ValoTheme.LABEL_FAILURE);
			}			
		});
		
		uploaderComponent.addFinishedListener(e->{
			if (!error){
				if (uploader.getCustomFile()==null){
					error = true;
					infoLabel.setValue(applicationContext.getMessage("window.upload.error", null, Locale.getDefault()));
					infoLayout.setVisible(true);
					infoLabel.setStyleName(ValoTheme.LABEL_FAILURE);
					uploader.initFile();
				}else{
					uploadWindowListener.success(uploader.getCustomFile());
				}
			}
			uploaderComponent.setEnabled(true);
			error = false;
		});
		
			
		layout.addComponent(infoLayout);
		
		
		Button btnClose = new Button(applicationContext.getMessage("btnClose", null, Locale.getDefault()),FontAwesome.TIMES);
		btnClose.addClickListener(e->close());
		layout.addComponent(btnClose);
		layout.setComponentAlignment(btnClose, Alignment.MIDDLE_CENTER);

		/* Centre la fenêtre */
		center();
	}
	
	/** Affiche les erreurs
	 * @param erreur
	 */
	private void displayError(String erreur){
		error = true;
		uploaderComponent.interruptUpload();
		infoLabel.setValue(erreur);
		infoLayout.setVisible(true);
		infoLabel.setStyleName(ValoTheme.LABEL_FAILURE);
	}

	/**
	 * Interface pour les listeners de la confirmation.
	 */
	public interface UploadWindowListener extends Serializable {

		/**
		 * Appelé lorsque le fichier a bien été téléchargé!
		 */
		public void success(FileCustom file);

	}

}
