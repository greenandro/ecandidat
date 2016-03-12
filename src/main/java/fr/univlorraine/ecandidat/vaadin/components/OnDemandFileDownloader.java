package fr.univlorraine.ecandidat.vaadin.components;

import java.io.IOException;
import java.io.InputStream;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.UI;

import fr.univlorraine.tools.vaadin.BusyIndicatorWindow;

/**
 * This specializes {@link FileDownloader} in a way, such that both the file
 * name and content can be determined on-demand, i.e. when the user has clicked
 * the component.
 */
public class OnDemandFileDownloader extends FileDownloader {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8800423122387573100L;

	/**
	 * Provide both the {@link StreamSource} and the filename in an on-demand
	 * way.
	 */
	public interface OnDemandStreamResource extends StreamSource {		
		String getFilename();
		InputStream getStream();
	}

	private final OnDemandStreamResource onDemandStreamResource;

	public OnDemandFileDownloader(OnDemandStreamResource onDemandStreamResource) {
		super(new CustomStreamResource(onDemandStreamResource, ""));
		this.onDemandStreamResource = onDemandStreamResource;
	}
	
	public OnDemandFileDownloader(OnDemandStreamResource onDemandStreamResource, AbstractComponent target) {
		super(new CustomStreamResource(onDemandStreamResource, ""));
		this.onDemandStreamResource = onDemandStreamResource;
		setOverrideContentType(false);
		extend(target);
	}
	
	@Override
	public boolean handleConnectorRequest(VaadinRequest request,
			VaadinResponse response, String path) throws IOException {
		getResource().setFilename(onDemandStreamResource.getFilename());
		final BusyIndicatorWindow busyIndicatorWindow = new BusyIndicatorWindow();
		final UI ui = UI.getCurrent();
		ui.access(() -> ui.addWindow(busyIndicatorWindow));
		try {
			return super.handleConnectorRequest(request, response, path);
		} finally {
			busyIndicatorWindow.close();
		}		
	}

	private StreamResource getResource() {
		return (StreamResource) this.getResource("dl");
	}

}