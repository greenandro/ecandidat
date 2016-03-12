package fr.univlorraine.ecandidat.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Civilite;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.repositories.CiviliteRepository;
import fr.univlorraine.ecandidat.repositories.DroitFonctionnaliteRepository;
import fr.univlorraine.ecandidat.repositories.LangueRepository;
import fr.univlorraine.ecandidat.repositories.ParametreRepository;
import fr.univlorraine.ecandidat.repositories.SiScolBacOuxEquRepository;
import fr.univlorraine.ecandidat.repositories.SiScolCentreGestionRepository;
import fr.univlorraine.ecandidat.repositories.SiScolCommuneRepository;
import fr.univlorraine.ecandidat.repositories.SiScolDepartementRepository;
import fr.univlorraine.ecandidat.repositories.SiScolDipAutCurRepository;
import fr.univlorraine.ecandidat.repositories.SiScolEtablissementRepository;
import fr.univlorraine.ecandidat.repositories.SiScolMentionNivBacRepository;
import fr.univlorraine.ecandidat.repositories.SiScolMentionRepository;
import fr.univlorraine.ecandidat.repositories.SiScolPaysRepository;
import fr.univlorraine.ecandidat.repositories.SiScolTypDiplomeRepository;
import fr.univlorraine.ecandidat.repositories.SiScolTypResultatRepository;
import fr.univlorraine.ecandidat.repositories.TypeAvisRepository;
import fr.univlorraine.ecandidat.repositories.TypeStatutPieceRepository;
import fr.univlorraine.ecandidat.repositories.TypeStatutRepository;
import fr.univlorraine.ecandidat.repositories.TypeTraitementRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleBeanPresentation;


/**Gestion des tables ref
 * @author Kevin Hergalant
 */
@Component
public class TableRefController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient TypeAvisRepository typeAvisRepository;
	@Resource
	private transient TypeStatutPieceRepository typeStatutPieceRepository;
	@Resource
	private transient TypeStatutRepository typeStatutRepository;	
	@Resource
	private transient TypeTraitementRepository typeTraitementRepository;
	@Resource
	private transient LangueRepository langueRepository;
	@Resource
	private transient CiviliteRepository civiliteRepository;
	@Resource
	private transient ParametreRepository parametreRepository;
	@Resource
	private transient DroitFonctionnaliteRepository droitFonctionnaliteRepository;
	@Resource
	private transient SiScolPaysRepository siScolPaysRepository;
	@Resource
	private transient SiScolDepartementRepository siScolDepartementRepository;
	@Resource
	private transient SiScolEtablissementRepository siScolEtablissementRepository;
	@Resource
	private transient SiScolCommuneRepository siScolCommuneRepository;
	@Resource
	private transient SiScolTypDiplomeRepository siScolTypDiplomeRepository;
	@Resource
	private transient SiScolCentreGestionRepository siScolCentreGestionRepository;
	
	@Resource
	private transient SiScolBacOuxEquRepository siScolBacOuxEquRepository;
	@Resource
	private transient SiScolDipAutCurRepository siScolDipAutCurRepository;
	@Resource
	private transient SiScolMentionNivBacRepository siScolMentionNivBacRepository;
	@Resource
	private transient SiScolMentionRepository siScolMentionRepository;
	@Resource
	private transient SiScolTypResultatRepository siScolTypResultatRepository;


	/*Les listes de tables ref*/
	private List<TypeAvis> listeTypeAvis;
	private List<TypeStatutPiece> listeTypeStatutPiece;
	private List<TypeStatut> listeTypeStatut;
	private List<TypeTraitement> listeTypeTraitement;
	private Langue langueDefaut;
	private List<Langue> langueActives;
	private Map<String,Parametre> mapParametre;
	private List<Civilite> listeCivilite;
	private List<DroitFonctionnalite> listeDroitFonctionnalite;
	
	/*Les tables ref apo*/
	private List<SiScolPays> listeSiScolPays;
	private List<SiScolTypDiplome> listeSiScolTypDiplome;
	private List<SiScolCentreGestion> listeSiScolCentreGestion;
	private List<SiScolDepartement> listeSiScolDepartement;
	private SiScolPays paysFrance;
	private List<SiScolBacOuxEqu> listeSiScolBacOuxEqu;
	private List<SiScolDipAutCur> listeSiScolDipAutCur;
	private List<SiScolMentionNivBac> listeSiScolMentionNivBac;
	private List<SiScolMention> listeSiScolMention;
	private List<SiScolTypResultat> listeSiScolTypResultat;
	
	/**
	 * @return la liste des parametres
	 */
	public Map<String,Parametre> getMapParametre() {
		if (mapParametre==null || mapParametre.size()==0){
			mapParametre = new HashMap<String,Parametre>();
			parametreRepository.findAll().forEach( e-> mapParametre.put(e.getCodParam(), e));
		}
		return mapParametre;
	}
	
	/**
	 * Clear la map de parametre 
	 */
	public void reloadMapParametre(){
		mapParametre = null;
		getMapParametre();
		loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_PARAM);
	}
	
	/**
	 * Remet à 0 les langues
	 */
	public void reloadLangues() {
		langueDefaut = null;
		langueActives = null;
		getLangueDefault();
		getLangueEnService();
		loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_LANGUE);
	}
	
	/**
	 * @return la langue par defaut
	 */
	public Langue getLangueDefault() {
		if (langueDefaut==null){
			langueDefaut = langueRepository.findByTemDefautLangue(true);
		}
		return langueDefaut;
	}
	
	/**
	 * @return langues en service
	 */
	public List<Langue> getLangueEnService() {
		if (langueActives==null || langueActives.size()==0){
			langueActives = langueRepository.findByTemDefautLangueAndTesLangue(false,true);
		}
		return langueActives;
	}
	
	/**
	 * @return la liste de types d'avis
	 */
	public List<TypeAvis> getListeTypeAvis() {
		if (listeTypeAvis==null || listeTypeAvis.size()==0){
			listeTypeAvis = new ArrayList<TypeAvis>();
			listeTypeAvis.addAll(typeAvisRepository.findAll());
		}
		return listeTypeAvis;
	}
	
	/** 
	 * @return Le type avis favorable
	 */
	public TypeAvis getTypeAvisFavorable(){
		return getListeTypeAvis().stream().filter(e->e.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_FAV)).findFirst().get();
	}
	
	/** 
	 * @return Le type avis liste completmentairee
	 */
	public TypeAvis getTypeAvisListComp(){
		return getListeTypeAvis().stream().filter(e->e.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_LISTE_COMP)).findFirst().get();
	}
	
	/** 
	 * @return Le type avis defavorable
	 */ 
	public TypeAvis getTypeAvisDefavorable(){
		return getListeTypeAvis().stream().filter(e->e.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_DEF)).findFirst().get();
	}
	
	/** 
	 * @return Le type avis preselect
	 */
	public TypeAvis getTypeAvisPreselect(){
		return getListeTypeAvis().stream().filter(e->e.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_PRESELECTION)).findFirst().get();
	}
	
	/**
	 * @return la liste de types de statut de pièce
	 */
	public List<TypeStatutPiece> getListeTypeStatutPiece() {
		if (listeTypeStatutPiece==null || listeTypeStatutPiece.size()==0){
			listeTypeStatutPiece = new ArrayList<TypeStatutPiece>();
			listeTypeStatutPiece.addAll(typeStatutPieceRepository.findAll());
		}
		return listeTypeStatutPiece;
	}
	
	/**
	 * @return la liste de types de statut de pièce
	 */
	public List<TypeStatutPiece> getListeTypeStatutPieceActif() {
		return getListeTypeStatutPiece().stream().filter(e->!e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE) && !e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)).collect(Collectors.toList());
	}
	
	/**
	 * @return la liste de types de statut
	 */
	public List<TypeStatut> getListeTypeStatut() {
		if (listeTypeStatut==null || listeTypeStatut.size()==0){
			listeTypeStatut = new ArrayList<TypeStatut>();
			listeTypeStatut.addAll(typeStatutRepository.findAll());
		}
		return listeTypeStatut;
	}
	
	/**
	 * @return la liste de DroitFonctionnalite 
	 */
	public List<DroitFonctionnalite> getListeDroitFonctionnaliteCandidature() {
		if (listeDroitFonctionnalite==null || listeDroitFonctionnalite.size()==0){
			listeDroitFonctionnalite = new ArrayList<DroitFonctionnalite>();
			droitFonctionnaliteRepository.findAll().forEach(e->{				
				if (e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE) || 
						e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_EDIT_TYPTRAIT)|| 
						e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_VALID_TYPTRAIT)|| 
						e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_EDIT_AVIS)|| 
						e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_VALID_AVIS)){
					listeDroitFonctionnalite.add(e);
				}
			});
		}
		return listeDroitFonctionnalite;
	}
	
	
	/**
	 * @return le type de statut en attente
	 */
	public TypeStatut getTypeStatutEnAttente() {
		return getListeTypeStatut().stream().filter(e->e.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_ATT)).findFirst().get();
	}
	
	/**
	 * @return le type de statut complet
	 */
	public TypeStatut getTypeStatutComplet() {
		return getListeTypeStatut().stream().filter(e->e.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_COM)).findFirst().get();
	}
	
	/**
	 * @return le type de statut complet
	 */
	public TypeStatut getTypeStatutIncomplet() {
		return getListeTypeStatut().stream().filter(e->e.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_INC)).findFirst().get();
	}
	
	/**
	 * @return le type de statut complet
	 */
	public TypeStatut getTypeStatutReceptionne() {
		return getListeTypeStatut().stream().filter(e->e.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_REC)).findFirst().get();
	}
	
	/**
	 * @return le type de statut piece transmis
	 */
	public TypeStatutPiece getTypeStatutPieceTransmis() {
		return getListeTypeStatutPiece().stream().filter(e->e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_TRANSMIS)).findFirst().get();
	}
	
	/**
	 * @return le type de statut piece validé
	 */
	public TypeStatutPiece getTypeStatutPieceValide() {
		return getListeTypeStatutPiece().stream().filter(e->e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_VALIDE)).findFirst().get();
	}
	
	/**
	 * @return le type de statut piece validé
	 */
	public TypeStatutPiece getTypeStatutPieceRefuse() {
		return getListeTypeStatutPiece().stream().filter(e->e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_REFUSE)).findFirst().get();
	}
	
	/**
	 * @return le type de statut piece validé
	 */
	public TypeStatutPiece getTypeStatutPieceAttente() {
		return getListeTypeStatutPiece().stream().filter(e->e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE)).findFirst().get();
	}
	
	/**
	 * @return le type de statut piece validé
	 */
	public TypeStatutPiece getTypeStatutPieceNonConcerne() {
		return getListeTypeStatutPiece().stream().filter(e->e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)).findFirst().get();
	}
	
	/**
	 * @return la liste de types de statut de pièce
	 */
	public List<TypeTraitement> getListeTypeTraitement() {
		if (listeTypeTraitement==null || listeTypeTraitement.size()==0){
			listeTypeTraitement = new ArrayList<TypeTraitement>();
			listeTypeTraitement.addAll(typeTraitementRepository.findAll());
		}
		return listeTypeTraitement;
	}
	
	/**
	 * @return type de traitement en attente
	 */
	public TypeTraitement getTypeTraitementEnAttente() {
		return getListeTypeTraitement().stream().filter(e->e.getCodTypTrait().equals(NomenclatureUtils.TYP_TRAIT_AT)).findFirst().get();
	}
	
	/**
	 * @return type de traitement en acces direct
	 */
	public TypeTraitement getTypeTraitementAccesDirect() {
		return getListeTypeTraitement().stream().filter(e->e.getCodTypTrait().equals(NomenclatureUtils.TYP_TRAIT_AD)).findFirst().get();
	}
	
	/**
	 * @return type de traitement en acces controle
	 */
	public TypeTraitement getTypeTraitementAccesControle() {
		return getListeTypeTraitement().stream().filter(e->e.getCodTypTrait().equals(NomenclatureUtils.TYP_TRAIT_AC)).findFirst().get();
	}
	
	/**
	 * @return la liste de civilité
	 */
	public List<Civilite> getListeCivilte() {
		if (listeCivilite==null || listeCivilite.size()==0){
			listeCivilite = new ArrayList<Civilite>();
			listeCivilite.addAll(civiliteRepository.findAll());
		}
		return listeCivilite;
	}
	
	
	
	/**
	 * @return la liste des pays apogée
	 */
	public List<SiScolPays> getListPaysEnService(){
		if (listeSiScolPays==null || listeSiScolPays.size()==0){
			listeSiScolPays = new ArrayList<SiScolPays>();
			SiScolPays paysFrance = siScolPaysRepository.findByCodPay(ConstanteUtils.PAYS_CODE_FRANCE);
			if (paysFrance!=null){
				listeSiScolPays.add(paysFrance);
			}			
			listeSiScolPays.addAll(siScolPaysRepository.findByCodPayNotOrderByLibPay(ConstanteUtils.PAYS_CODE_FRANCE));
		}
		return listeSiScolPays;
	}
	
	/**
	 * @return la liste des departements apogée
	 */
	public List<SiScolDepartement> getListDepartementEnService(){
		if (listeSiScolDepartement==null || listeSiScolDepartement.size()==0){
			listeSiScolDepartement = new ArrayList<SiScolDepartement>();
			listeSiScolDepartement.addAll(siScolDepartementRepository.findAll());
		}
		return listeSiScolDepartement;
	}
	
	/**
	 * @return la liste de types de diplome
	 */
	public List<SiScolTypDiplome> getListeTypDiplome() {
		if (listeSiScolTypDiplome==null || listeSiScolTypDiplome.size()==0){
			listeSiScolTypDiplome = new ArrayList<SiScolTypDiplome>();
			listeSiScolTypDiplome.addAll(siScolTypDiplomeRepository.findAll());
		}
		return listeSiScolTypDiplome;
	}
	
	/**
	 * @return la liste des centres de gestion
	 */
	public List<SiScolCentreGestion> getListeCentreGestion() {
		if (listeSiScolCentreGestion==null || listeSiScolCentreGestion.size()==0){
			listeSiScolCentreGestion = new ArrayList<SiScolCentreGestion>();
			listeSiScolCentreGestion.addAll(siScolCentreGestionRepository.findAll());
		}
		return listeSiScolCentreGestion;
	}
	
	/**
	 * @return la liste des bac ou equ
	 */
	public List<SiScolBacOuxEqu> getListeBacOuxEqu() {
		if (listeSiScolBacOuxEqu==null || listeSiScolBacOuxEqu.size()==0){
			listeSiScolBacOuxEqu = new ArrayList<SiScolBacOuxEqu>();
			listeSiScolBacOuxEqu.addAll(siScolBacOuxEquRepository.findAll());
		}
		return listeSiScolBacOuxEqu;
	}
	
	/**
	 * @return la liste des dip aut cur
	 */
	public List<SiScolDipAutCur> getListeDipAutCur() {
		if (listeSiScolDipAutCur==null || listeSiScolDipAutCur.size()==0){
			listeSiScolDipAutCur = new ArrayList<SiScolDipAutCur>();
			listeSiScolDipAutCur.addAll(siScolDipAutCurRepository.findAll());
		}
		return listeSiScolDipAutCur;
	}
	
	/**
	 * @return la liste des Mention Niv Bac
	 */
	public List<SiScolMentionNivBac> getListeMentionNivBac() {
		if (listeSiScolMentionNivBac==null || listeSiScolMentionNivBac.size()==0){
			listeSiScolMentionNivBac = new ArrayList<SiScolMentionNivBac>();
			listeSiScolMentionNivBac.addAll(siScolMentionNivBacRepository.findAll());
		}
		return listeSiScolMentionNivBac;
	}
	
	/**
	 * @return la liste des Mention
	 */
	public List<SiScolMention> getListeMention() {
		if (listeSiScolMention==null || listeSiScolMention.size()==0){
			listeSiScolMention = new ArrayList<SiScolMention>();
			listeSiScolMention.addAll(siScolMentionRepository.findAll());
		}
		return listeSiScolMention;
	}
	
	/**
	 * @return la liste des Types de resultats
	 */
	public List<SiScolTypResultat> getListeTypeResultat() {
		if (listeSiScolTypResultat==null || listeSiScolTypResultat.size()==0){
			listeSiScolTypResultat = new ArrayList<SiScolTypResultat>();
			listeSiScolTypResultat.addAll(siScolTypResultatRepository.findAll());
		}
		return listeSiScolTypResultat;
	}
	
	/** Met à jour les tables ref apogée réutilisables (pays et departement)
	 * @param cod
	 */
	public void clearSiScolTableRef(String cod){
		if (cod.equals(ConstanteUtils.TYP_REF_PAYS)){
			paysFrance = null;
			listeSiScolPays = null;
			getListPaysEnService();
			getPaysFrance();
			loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_TABLE_REF_PAYS);
		}else if (cod.equals(ConstanteUtils.TYP_REF_DEPARTEMENT)){
			listeSiScolDepartement = null;
			getListDepartementEnService();
			loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_TABLE_REF_DPT);
		}else if (cod.equals(ConstanteUtils.TYP_REF_TYPDIPLOME)){
			listeSiScolTypDiplome = null;
			getListeTypDiplome();
			loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_TABLE_REF_TYPDIP);
		}else if (cod.equals(ConstanteUtils.TYP_REF_CENTREGESTION)){
			listeSiScolCentreGestion = null;
			getListeCentreGestion();
			loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_TABLE_REF_CGE);
		}else if (cod.equals(ConstanteUtils.TYP_REF_BAC_OU_EQU)){
			listeSiScolBacOuxEqu = null;
			getListeBacOuxEqu();
			loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_TABLE_REF_BAC);
		}else if (cod.equals(ConstanteUtils.TYP_REF_DIP_AUT_CUR)){
			listeSiScolDipAutCur = null;
			getListeDipAutCur();
			loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_TABLE_REF_DIP);
		}else if (cod.equals(ConstanteUtils.TYP_REF_MENTION)){
			listeSiScolMention = null;
			getListeMention();
			loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_TABLE_REF_MENTION);
		}else if (cod.equals(ConstanteUtils.TYP_REF_MENTION_BAC)){
			listeSiScolMentionNivBac = null;
			getListeMentionNivBac();
			loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_TABLE_REF_MENTBAC);
		}else if (cod.equals(ConstanteUtils.TYP_REF_TYPRESULTAT)){
			listeSiScolTypResultat = null;
			getListeTypeResultat();
			loadBalancingController.askToReloadData(ConstanteUtils.LB_RELOAD_TABLE_REF_TYPRES);
		}
	}
	
	/** 
	 * @param codPostal
	 * @return la Liste les communes par leur code postal
	 */
	public List<SiScolCommune> listeCommuneByCodePostal(String codPostal){
		return siScolCommuneRepository.getCommuneByCodePostal(codPostal);	
	}
	
	/** 
	 * @param siScolDepartement
	 * @return Liste les commune par le code commune et là ou il y a des etablissments
	 */
	public List<SiScolCommune> listeCommuneByDepartement(SiScolDepartement siScolDepartement) {
		return siScolCommuneRepository.getCommuneByDepartement(siScolDepartement.getCodDep());
	}
	
	/**
	 * @param commune
	 * @return  Liste les etablissment par code commune
	 */
	public List<SiScolEtablissement> listeEtablissementByCommune(SiScolCommune commune) {
		return siScolEtablissementRepository.getEtablissementByCommune(commune.getCodCom());
	}

	/**
	 * @return la france
	 */
	public SiScolPays getPaysFrance() {
		if (paysFrance==null || paysFrance.getLibPay()==null){
			List<SiScolPays> liste = getListPaysEnService();
			if (liste!=null && liste.size()>0){
				paysFrance = getPaysByCode(ConstanteUtils.PAYS_CODE_FRANCE);
			}else{
				paysFrance = null;
			}
		}
		return paysFrance;
	}
	
	/**
	 * @param code
	 * @return le pays equivalent au code
	 */
	public SiScolPays getPaysByCode(String code){
		if (code == null){
			return null;
		}
		List<SiScolPays> liste = getListPaysEnService();
		if (liste==null || liste.size()==0){
			return null;
		}
		Optional<SiScolPays> fr = liste.stream().filter(e->e.getCodPay().equals(code)).findFirst();
		if (fr.isPresent()){
			return fr.get();
		}else{
			return null;
		}
	}

	/** Renvoie le departement par son code
	 * @param cod
	 * @return le departement par son code
	 */
	public SiScolDepartement getDepartementByCode(String cod) {
		if (cod==null){
			return null;
		}
		List<SiScolDepartement> liste = getListDepartementEnService();
		Optional<SiScolDepartement> dep = liste.stream().filter(e->e.getCodDep().equals(cod)).findFirst();
		if (dep.isPresent()){
			return dep.get();
		}else{
			return null;
		}
	}
	
	/**
	 * @param codApo
	 * @return la civilite par son code apogee
	 */
	public Civilite getCiviliteByCodeApo(String codApo){
		if (codApo==null){
			return null;
		}
		List<Civilite> liste = getListeCivilte();
		Optional<Civilite> civ = liste.stream().filter(e->e.getCodApo().equals(codApo)).findFirst();
		if (civ.isPresent()){
			return civ.get();
		}else{
			return null;
		}
	}

	/** CHerche la commune par son code postal et son code commune
	 * @param codBdi
	 * @param codCom
	 * @return la commune
	 */
	public SiScolCommune getCommuneByCodePostalAndCodeCom(String codBdi,
			String codCom) {
		if (codBdi==null || codCom==null){
			return null;
		}
		List<SiScolCommune> listeCommuneByCodePostal = listeCommuneByCodePostal(codBdi);
		if (listeCommuneByCodePostal!=null && listeCommuneByCodePostal.size()>0){
			Optional<SiScolCommune> com = listeCommuneByCodePostal.stream().filter(e->e.getCodCom().equals(codCom)).findFirst();
			if (com.isPresent()){
				return com.get();
			}else{
				return null;
			}
		}
		return null;
	}

	/** Cherche l'etablissement par son code
	 * @param codEtb
	 * @return l'etablissement
	 */
	public SiScolEtablissement getEtablissementByCode(String codEtb) {
		if (codEtb==null){
			return null;
		}
		return siScolEtablissementRepository.findOne(codEtb);
	}

	/** Cherche le bac Ou Equ par son code
	 * @param codBac
	 * @return le bac ou equ
	 */
	public SiScolBacOuxEqu getBacOuEquByCode(String codBac) {
		if (codBac==null){
			return null;
		}
		List<SiScolBacOuxEqu> liste = getListeBacOuxEqu();
		Optional<SiScolBacOuxEqu> bac = liste.stream().filter(e->e.getCodBac().equals(codBac)).findFirst();
		if (bac.isPresent()){
			return bac.get();
		}else{
			return null;
		}
	}

	/** Cherche la mention niveau bac par son code
	 * @param codMnb
	 * @return la mention niveau bac
	 */
	public SiScolMentionNivBac getMentionNivBacByCode(String codMnb) {
		if (codMnb==null){
			return null;
		}
		List<SiScolMentionNivBac> liste = getListeMentionNivBac();
		Optional<SiScolMentionNivBac> mention = liste.stream().filter(e->e.getCodMnb().equals(codMnb)).findFirst();
		if (mention.isPresent()){
			return mention.get();
		}else{
			return null;
		}
	}

	/** Cherche la mention par son code
	 * @param codMen
	 * @return la mention
	 */
	public SiScolMention getMentionByCode(String codMen) {
		if (codMen==null){
			return null;
		}
		List<SiScolMention> liste = getListeMention();
		Optional<SiScolMention> mention = liste.stream().filter(e->e.getCodMen().equals(codMen)).findFirst();
		if (mention.isPresent()){
			return mention.get();
		}else{
			return null;
		}
	}	
	
	/** Cherche le type de resultat par son code
	 * @param codTre
	 * @return le type de resultat
	 */
	public SiScolTypResultat getTypeResultatByCode(String codTre) {
		if (codTre==null){
			return null;
		}
		List<SiScolTypResultat> liste = getListeTypeResultat();
		Optional<SiScolTypResultat> result = liste.stream().filter(e->e.getCodTre().equals(codTre)).findFirst();
		if (result.isPresent()){
			return result.get();
		}else{
			return null;
		}
	}
	
	/**
	 * @param codCGE
	 * @return le SiScolCentreGestion lie au code CGE
	 */
	public SiScolCentreGestion getSiScolCentreGestionByCode(String codCGE){
		if (codCGE==null){
			return null;
		}
		List<SiScolCentreGestion> liste = getListeCentreGestion();
		Optional<SiScolCentreGestion> result = liste.stream().filter(e->e.getCodCge().equals(codCGE)).findFirst();
		if (result.isPresent()){
			return result.get();
		}else{
			return null;
		}
	}
	
	/**
	 * @param codTpd
	 * @return le SiScolTypDiplome lie au code tpd
	 */
	public SiScolTypDiplome getSiScolTypDiplomeByCode(String codTpd){
		if (codTpd==null){
			return null;
		}
		List<SiScolTypDiplome> liste = getListeTypDiplome();
		Optional<SiScolTypDiplome> result = liste.stream().filter(e->e.getCodTpdEtb().equals(codTpd)).findFirst();
		if (result.isPresent()){
			return result.get();
		}else{
			return null;
		}
	}
	
	/**
	 * @return la liste de type "obtenu" du cursus externe
	 */
	public List<SimpleBeanPresentation> getListeObtenuCursus(){
		List<SimpleBeanPresentation> liste = new ArrayList<SimpleBeanPresentation>();
		liste.add(new SimpleBeanPresentation(ConstanteUtils.CURSUS_EXTERNE_OBTENU, applicationContext.getMessage("cursusexterne.obtenu.choix.obtenu", null,  UI.getCurrent().getLocale())));
		liste.add(new SimpleBeanPresentation(ConstanteUtils.CURSUS_EXTERNE_NON_OBTENU, applicationContext.getMessage("cursusexterne.obtenu.choix.nonobtenu", null,  UI.getCurrent().getLocale())));
		liste.add(new SimpleBeanPresentation(ConstanteUtils.CURSUS_EXTERNE_EN_COURS, applicationContext.getMessage("cursusexterne.obtenu.choix.encours", null,  UI.getCurrent().getLocale())));
		return liste;
	}
	
	/**
	 * @param code
	 * @return le libelle du "obtenu" du cursus externe
	 */
	public String getLibelleObtenuCursusByCode(String code){
		if (code == null){
			return null;
		}else if (code.equals(ConstanteUtils.CURSUS_EXTERNE_OBTENU)){
			return applicationContext.getMessage("cursusexterne.obtenu.choix.obtenu.lib", null,  UI.getCurrent().getLocale());
		}else if (code.equals(ConstanteUtils.CURSUS_EXTERNE_NON_OBTENU)){
			return applicationContext.getMessage("cursusexterne.obtenu.choix.nonobtenu.lib", null,  UI.getCurrent().getLocale());
		}else if (code.equals(ConstanteUtils.CURSUS_EXTERNE_EN_COURS)){
			return applicationContext.getMessage("cursusexterne.obtenu.choix.encours.lib", null,  UI.getCurrent().getLocale());
		}
		return null;
	}
}
