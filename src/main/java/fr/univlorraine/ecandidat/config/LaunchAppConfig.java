package fr.univlorraine.ecandidat.config;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.univlorraine.ecandidat.controllers.LoadBalancingController;
import fr.univlorraine.ecandidat.controllers.LockCandidatController;
import fr.univlorraine.ecandidat.controllers.NomenclatureController;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;

/**
 * Configuration du lancement de l'appli
 * 
 * @author Kevin Hergalant
 */
@Component
public class LaunchAppConfig  implements ApplicationListener<ContextRefreshedEvent> {

	private Logger logger = LoggerFactory.getLogger(LaunchAppConfig.class);

	@Resource
	private transient NomenclatureController nomenclatureController;
	@Resource
	private transient LockCandidatController lockCandidatController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	
	@Value("${enablePreProcessTemplate:}")
	private transient String enablePreProcessTemplate;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("Preprocess en cours...");
		preprocessCleanLock();
		preprocessNomenclature();
		if (isPreProcessTemplateEnable()){
			preprocessTemplate();
		}			
		loadBalancingController.reloadAllData();
		logger.info("Fin preprocess");
	}
	
	/**
	 * Au démarrage de l'appli, on supprime tout les locks
	 */
	private void preprocessCleanLock() {
		lockCandidatController.cleanAllLockCandidatForInstance();
	}

	/**
	 * @return true si le WebSocket est actif
	 */
	private Boolean isPreProcessTemplateEnable(){
		Boolean enablePreProcess = true;
		if (enablePreProcessTemplate instanceof String && enablePreProcessTemplate!=null && !enablePreProcessTemplate.equals("")){
			enablePreProcess = Boolean.valueOf(enablePreProcessTemplate);
			if (enablePreProcess == null){
				enablePreProcess = false;
			}			
		}		
		return enablePreProcess;
	}

	/**
	 * Charge les nomenclatures si pas a jour
	 */
	public void preprocessNomenclature() {
		if (!loadBalancingController.isLoadBalancingCandidatMode() && nomenclatureController.isNomenclatureToReload()){
			logger.info("Mise à jour nomenclature...");
			nomenclatureController.cleanNomenclature();
			nomenclatureController.majNomenclature(true);
			logger.info("Fin mise à jour nomenclature");
		}else{
			logger.info("Nomenclature a jour");
		}
	}
	
	/**
	 * Charge les templates	
	}*/
	
	public void preprocessTemplate() {		
		try {
			logger.info("Generation du report...");
			InputStream in = getClass().getResourceAsStream("/template/dossier_export_template.docx");
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Velocity);
			ByteArrayInOutStream out = new ByteArrayInOutStream();
			Options options = Options.getTo(ConverterTypeTo.PDF).via(
					ConverterTypeVia.XWPF);
			IContext context = report.createContext();
			report.convert(context, options, out);
			out.close();							
			in.close();
			logger.info("Fin generation du report");
		} catch (IOException | XDocReportException e) {
			logger.info("Erreur a la generation du report");
		}
	}
}
