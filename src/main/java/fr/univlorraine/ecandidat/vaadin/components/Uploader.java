package fr.univlorraine.ecandidat.vaadin.components;

import java.io.OutputStream;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.services.file.FileCustom;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import lombok.Data;

/**
 * Uploader d'un fichier
 * @author Kevin Hergalant
 *
 */
@Data
@Configurable(preConstruction=true)
public class Uploader implements Receiver, SucceededListener {
    
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 189614550786643968L;

	private ByteArrayInOutStream file = new ByteArrayInOutStream();
	private String prefixe;
	private String typeFichier;
	private FileCustom customFile;
	
	@Resource
	private transient FileController fileController;
    
    @Override
    public OutputStream receiveUpload(String filename, String mimeType)
    {
        this.file.reset();
        return file;
    }

    public void uploadSucceeded(SucceededEvent event) {
    	this.customFile = fileController.createFileFromUpload(file,event.getMIMEType(),event.getFilename(),event.getLength(),this.typeFichier, this.prefixe);
    }

	public Uploader(String prefixe, String typeFichier) {
		this.prefixe = prefixe;
		this.typeFichier = typeFichier;
	}

	/*@Override
	public void uploadStarted(StartedEvent event) {
		file = new ByteArrayInOutStream();
	}*/

	public void initFile(){
		file = new ByteArrayInOutStream();
	}
}
