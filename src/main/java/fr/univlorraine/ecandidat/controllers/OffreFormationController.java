package fr.univlorraine.ecandidat.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfCtrCand;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfDiplome;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfFormation;

/**Gestion de l'offre de formation
 * @author Kevin Hergalant
 */
@Component
public class OffreFormationController {
	/* Injections */
	@Resource
	private transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	
	private List<OdfCtrCand> offreDeFormation;
	
	/** Recupere l'offre de formation
	 * @return la liste d'odf
	 */
	public List<OdfCtrCand> getOdf() {
		if (offreDeFormation==null || offreDeFormation.size()==0){
			offreDeFormation = new ArrayList<OdfCtrCand>();
			centreCandidatureController.getCentreCandidaturesEnService().forEach(ctr->{
				addInternalCtrCand(ctr);				
			});
		}
		return offreDeFormation;
	}
	
	public void reloadOdf(){
		offreDeFormation = null;
		getOdf();
	}
	
	
	/** Renvoi la liste de diplome d'un centre de candidature
	 * @param formations
	 * @return la liste d'odf diplomes
	 */
	private List<OdfDiplome> getDiplomesByCtrCand(Integer idCtr, List<Formation> formations){
		List<OdfDiplome> diplomes = new ArrayList<OdfDiplome>();
		/*Parcourt des formations*/
		formations.forEach(formation->{
			if (formation.getTesForm()){
				SiScolTypDiplome diplome = formation.getSiScolTypDiplome();
				/*Verification que le diplome est deja présent dans la liste des diplomes*/
				Optional<OdfDiplome> dipOpt = diplomes.stream().filter(dip->dip.getCodDip().equals(diplome.getCodTpdEtb())).findAny();
				OdfDiplome leDiplome = null;
				/*Si deja présent-->on le recupere et on ajoute la formation à ce diplome*/
				if (dipOpt.isPresent()){
					leDiplome = dipOpt.get();		
				}
				/*Si pas present on en créé un nouveau*/
				else{
					leDiplome = new OdfDiplome(idCtr+"-"+diplome.getCodTpdEtb(),diplome.getCodTpdEtb(),diplome.getLibTpd());
					diplomes.add(leDiplome);
				}
				leDiplome.getListeFormation().add(new OdfFormation(formation.getLibForm(), formation.getIdForm(), formation.getMotCleForm(), formation.getDatDebDepotForm(),formation.getDatFinDepotForm()));
				leDiplome.getListeFormation().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));	
			}			
		});
		diplomes.sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
		return diplomes;
	}
	
	/** Recupere un objet de type OdfCtrCand suivant son id
	 * @param ctrCand
	 * @return l'odf du ctrCand
	 */
	private OdfCtrCand getCtrCandFromOffre(CentreCandidature ctrCand){
		Optional<OdfCtrCand> centreOpt = offreDeFormation.stream().filter(odfCtr->odfCtr.getIdCtrCand().equals(ctrCand.getIdCtrCand())).findFirst();
		if (centreOpt.isPresent()){
			return centreOpt.get();
		}
		return null;
	}
	

	/** Recupere un objet de type OdfDiplome suivant son code
	 * @param ctrCand
	 * @param siScolTypDiplome
	 * @return l'OdfDiplome
	 */
	private OdfDiplome getDiplomeFromOffre(OdfCtrCand ctrCand, SiScolTypDiplome siScolTypDiplome){
		if (ctrCand.getListeDiplome()==null || ctrCand.getListeDiplome().size()==0){
			return null;
		}
		
		Optional<OdfDiplome> dipOpt = ctrCand.getListeDiplome().stream().filter(dip->dip.getCodDip().equals(siScolTypDiplome.getCodTpdEtb())).findFirst();
		if (dipOpt.isPresent()){
			return dipOpt.get();
		}
		return null;
	}
	
	/** Recupere un objet de type OdfDiplome suivant son code
	 * @param ctrCand
	 * @param siScolTypDiplome
	 * @return l'odfFormation
	 */
	private OdfFormation getFormationFromOffre(OdfDiplome odfDiplome, Formation formation){
		if (odfDiplome.getListeFormation()==null || odfDiplome.getListeFormation().size()==0){
			return null;
		}
		
		Optional<OdfFormation> formOpt = odfDiplome.getListeFormation().stream().filter(form->form.getIdFormation().equals(formation.getIdForm())).findFirst();
		if (formOpt.isPresent()){
			return formOpt.get();
		}
		return null;
	}
	
	/** Supprime un centre de candidature de l'offre
	 * @param ctrCand
	 */
	public void removeCtrCand(CentreCandidature ctrCand){
		getOdf();
		removeInternalCtrCand(ctrCand);
		loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_ODF);
	}
	
	/** Supprime un centre de candidature de l'offre
	 * @param ctrCand
	 */
	private void removeInternalCtrCand(CentreCandidature ctrCand){
		offreDeFormation.remove(new OdfCtrCand(ctrCand.getIdCtrCand(),ctrCand.getLibCtrCand(),ctrCand.getTemDematCtrCand()));		
	}
	
	/** Ajoute un centre de candidature à l'offre
	 * @param ctrCand
	 */
	private void addInternalCtrCand(CentreCandidature ctrCand){		
		if (getCtrCandFromOffre(ctrCand)!=null){
			removeInternalCtrCand(ctrCand);
		}
		if (!ctrCand.getTesCtrCand()){
			removeInternalCtrCand(ctrCand);
			return;
		}
		List<Formation> formations = new ArrayList<Formation>();
		
		ctrCand.getCommissions().forEach(e->formations.addAll(e.getFormations()));
		
		
		if (formations.size()>0){
			OdfCtrCand ctrCandOffre = new OdfCtrCand(ctrCand.getIdCtrCand(),ctrCand.getLibCtrCand(),ctrCand.getTemDematCtrCand());
			ctrCandOffre.setListeDiplome(getDiplomesByCtrCand(ctrCand.getIdCtrCand(),formations));
			offreDeFormation.add(ctrCandOffre);			
		}
		offreDeFormation.sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
	}
	
	/** Ajoute un centre de candidature à l'offre
	 * @param ctrCand
	 */
	public void addCtrCand(CentreCandidature ctrCand){		
		getOdf();
		addInternalCtrCand(ctrCand);
		loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_ODF);
	}
	
	public void removeFormation(Formation formation){
		getOdf();
		removeInternalFormation(formation);
		loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_ODF);
	}
	
	/** Supprime une formation de l'offre
	 * @param formation
	 */
	private void removeInternalFormation(Formation formation){
		OdfCtrCand odfCtrCand = getCtrCandFromOffre(formation.getCommission().getCentreCandidature());
		if (odfCtrCand!=null){
			OdfDiplome odfDiplome = getDiplomeFromOffre(odfCtrCand,formation.getSiScolTypDiplome());
			if (odfDiplome!=null){
				odfDiplome.getListeFormation().remove(new OdfFormation(formation.getLibForm(), formation.getIdForm(), formation.getMotCleForm(), formation.getDatDebDepotForm(),formation.getDatFinDepotForm()));
				if (odfDiplome.getListeFormation().size()==0){
					odfCtrCand.getListeDiplome().remove(odfDiplome);
				}
				if (odfCtrCand.getListeDiplome().size()==0){
					offreDeFormation.remove(odfCtrCand);					
				}
			}
		}
	}
	
	/** Ajoute une formation à l'offre
	 * @param formation
	 */
	public void addFormation(Formation formation){
		getOdf();
		CentreCandidature ctrCand = formation.getCommission().getCentreCandidature();
		if (formation.getTesForm()){
			OdfCtrCand odfCtrCand = getCtrCandFromOffre(ctrCand);
			if (odfCtrCand!=null){
				OdfDiplome odfDiplome = getDiplomeFromOffre(odfCtrCand,formation.getSiScolTypDiplome());
				if (odfDiplome==null){
					odfDiplome = new OdfDiplome(ctrCand.getIdCtrCand()+"-"+formation.getSiScolTypDiplome().getCodTpdEtb(),formation.getSiScolTypDiplome().getCodTpdEtb(),formation.getSiScolTypDiplome().getLibTpd());
					odfCtrCand.getListeDiplome().add(odfDiplome);
					odfCtrCand.getListeDiplome().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
				}
				OdfFormation odfFormation = getFormationFromOffre(odfDiplome, formation);
				if (odfFormation==null){
					odfDiplome.getListeFormation().add(new OdfFormation(formation.getLibForm(), formation.getIdForm(), formation.getMotCleForm(), formation.getDatDebDepotForm(),formation.getDatFinDepotForm()));
					odfDiplome.getListeFormation().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
				}else{
					odfFormation.setTitle(formation.getLibForm());
					odfFormation.setMotCle(formation.getMotCleForm());
					odfFormation.setDateDebut(formation.getDatDebDepotForm());
					odfFormation.setDateFin(formation.getDatFinDepotForm());
					odfDiplome.getListeFormation().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
				}
			}else{
				addInternalCtrCand(ctrCand);
			}
		}else{
			removeInternalFormation(formation);
		}	
		loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_ODF);
	}
	
	/** format la date de candidature
	 * @param la formation
	 * @return les dates formatées
	 */
	/*private String formatDateCandidature(Formation f){
		return applicationContext.getMessage("odf.dates.candidature", new Object[]{formatterDate.format(f.getDatDebDepotForm()),formatterDate.format(f.getDatFinDepotForm())}, locale);
	}*/
}
