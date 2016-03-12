package fr.univlorraine.ecandidat.services.siscol;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.axis.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolComBdi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolUtilisateur;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;
import fr.univlorraine.ecandidat.entities.siscol.WSAdresse;
import fr.univlorraine.ecandidat.entities.siscol.AnneeUni;
import fr.univlorraine.ecandidat.entities.siscol.BacOuxEqu;
import fr.univlorraine.ecandidat.entities.siscol.CentreGestion;
import fr.univlorraine.ecandidat.entities.siscol.ComBdi;
import fr.univlorraine.ecandidat.entities.siscol.Commune;
import fr.univlorraine.ecandidat.entities.siscol.WSCursusInterne;
import fr.univlorraine.ecandidat.entities.siscol.Departement;
import fr.univlorraine.ecandidat.entities.siscol.DipAutCur;
import fr.univlorraine.ecandidat.entities.siscol.Etablissement;
import fr.univlorraine.ecandidat.entities.siscol.WSBac;
import fr.univlorraine.ecandidat.entities.siscol.IndOpi;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.entities.siscol.Mention;
import fr.univlorraine.ecandidat.entities.siscol.MentionNivBac;
import fr.univlorraine.ecandidat.entities.siscol.Pays;
import fr.univlorraine.ecandidat.entities.siscol.TypDiplome;
import fr.univlorraine.ecandidat.entities.siscol.TypResultat;
import fr.univlorraine.ecandidat.entities.siscol.Utilisateur;
import fr.univlorraine.ecandidat.entities.siscol.VersionApo;
import fr.univlorraine.ecandidat.entities.siscol.Vet;
import fr.univlorraine.ecandidat.entities.siscol.VoeuxIns;
import fr.univlorraine.ecandidat.repositories.OpiRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import gouv.education.apogee.commun.client.utils.WSUtils;
import gouv.education.apogee.commun.client.ws.etudiantmetier.EtudiantMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.opimetier.OpiMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.pedagogiquemetier.PedagogiqueMetierServiceInterface;
import gouv.education.apogee.commun.transverse.dto.etudiant.AdresseDTO2;
import gouv.education.apogee.commun.transverse.dto.etudiant.CoordonneesDTO2;
import gouv.education.apogee.commun.transverse.dto.etudiant.IdentifiantsEtudiantDTO;
import gouv.education.apogee.commun.transverse.dto.etudiant.IndBacDTO;
import gouv.education.apogee.commun.transverse.dto.etudiant.InfoAdmEtuDTO;
import gouv.education.apogee.commun.transverse.dto.opi.DonneesOpiDTO4;
import gouv.education.apogee.commun.transverse.dto.opi.MAJDonneesNaissanceDTO;
import gouv.education.apogee.commun.transverse.dto.opi.MAJDonneesPersonnellesDTO3;
import gouv.education.apogee.commun.transverse.dto.opi.MAJEtatCivilDTO;
import gouv.education.apogee.commun.transverse.dto.opi.MAJOpiAdresseDTO;
import gouv.education.apogee.commun.transverse.dto.opi.MAJOpiBacDTO;
import gouv.education.apogee.commun.transverse.dto.opi.MAJOpiIndDTO3;
import gouv.education.apogee.commun.transverse.dto.opi.MAJOpiVoeuDTO;
import gouv.education.apogee.commun.transverse.dto.pedagogique.ContratPedagogiqueResultatVdiVetDTO2;
import gouv.education.apogee.commun.transverse.dto.pedagogique.EtapeResVdiVetDTO2;
import gouv.education.apogee.commun.transverse.dto.pedagogique.ResultatVetDTO;
import gouv.education.apogee.commun.transverse.exception.WebBaseException;


/**Gestion du SI Scol Apogee
 * @author Kevin Hergalant
 *
 */
@Component(value="siScolApogeeWSServiceImpl")
@SuppressWarnings("unchecked")
public class SiScolApogeeWSServiceImpl implements SiScolGenericService, Serializable{

	/*** serialVersionUID*/
	private static final long serialVersionUID = 5253471002328427816L;

	private Logger logger = LoggerFactory.getLogger(SiScolApogeeWSServiceImpl.class);

	/**
	 * proxy pour faire appel aux infos sur les résultats du WS .
	 */
	private PedagogiqueMetierServiceInterface monProxyPedagogique;
	
	/**
	 * proxy pour faire appel aux infos concernant un étudiant.
	 */
	private EtudiantMetierServiceInterface monProxyEtu;
	
	/**
	 * proxy pour faire appel aux infos géographique du WS .
	 */
	private OpiMetierServiceInterface monProxyOpi;
	
	@Resource
	private transient ParametreController parametreController;
	
	@Resource
	private transient OpiRepository opiRepository;
	
	@Resource
	private transient DateTimeFormatter formatterDateTimeApo;
	

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#isImplementationApogee()
	 */
	@Override
	public Boolean isImplementationApogee() {
		return true;
	}


	/** Execute la requete et ramene l'ensemble des elements d'une table
	 * @param className la class concernée
	 * @return la liste d'objet
	 * @throws SiScolException
	 */	
	private <T> List<T> executeQueryListEntity(Class<T> className) throws SiScolException{
		try{
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
			EntityManager em = emf.createEntityManager();
			Query query = em.createQuery("Select a from "+className.getName()+" a", className);
			List<T> listeSiScol = query.getResultList();
			em.close();
			return listeSiScol;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on execute query list entity", e.getCause());
		}		
	}
	
	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolBacOuxEqu()
	 */
	@Override
	public List<SiScolBacOuxEqu> getListSiScolBacOuxEqu() throws SiScolException {
		
		try{
			List<SiScolBacOuxEqu> liste = new ArrayList<SiScolBacOuxEqu>();
			executeQueryListEntity(BacOuxEqu.class).forEach(bac->{				
				liste.add(new SiScolBacOuxEqu(bac.getCodBac(), bac.getLibBac(), bac.getLicBac(),
						MethodUtils.getBooleanFromTemoin(bac.getTemEnSveBac()), 
						MethodUtils.getBooleanFromTemoin(bac.getTemNatBac()),
						bac.getDaaDebVldBac(), bac.getDaaFinVldBac()));
			});
			return liste;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolBacOuxEqu", e.getCause());
		}
		
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCentreGestion()
	 */
	@Override
	public List<SiScolCentreGestion> getListSiScolCentreGestion()
			throws SiScolException {
		List<SiScolCentreGestion> liste = new ArrayList<SiScolCentreGestion>();
		try{
			executeQueryListEntity(CentreGestion.class).forEach(centreGestion->{				
				liste.add(new SiScolCentreGestion(centreGestion.getCodCge(), centreGestion.getLibCge(), centreGestion.getLicCge(),
						MethodUtils.getBooleanFromTemoin(centreGestion.getTemEnSveCge())));
			});			
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolCentreGestion", e.getCause());
		}
		return liste;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCommune()
	 */
	@Override
	public List<SiScolCommune> getListSiScolCommune() throws SiScolException {
		List<SiScolCommune> liste = new ArrayList<SiScolCommune>();
		try{
			executeQueryListEntity(Commune.class).forEach(commune->{
				
				SiScolCommune siScolCommune = new SiScolCommune(commune.getCodCom(), commune.getLibCom(), 
						MethodUtils.getBooleanFromTemoin(commune.getTemEnSveCom()));				
				if (commune.getDepartement() != null){
					siScolCommune.setSiScolDepartement(new SiScolDepartement(commune.getDepartement().getCodDep()));
				}				
				liste.add(siScolCommune);
				
			});
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolCommune", e.getCause());
		}
		return liste;
	}
	
	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDepartement()
	 */
	@Override
	public List<SiScolDepartement> getListSiScolDepartement()
			throws SiScolException {
		List<SiScolDepartement> liste = new ArrayList<SiScolDepartement>();
		try{
			executeQueryListEntity(Departement.class).forEach(departement->{				
				liste.add(new SiScolDepartement(departement.getCodDep(), departement.getLibDep(), departement.getLicDep(), 
						MethodUtils.getBooleanFromTemoin(departement.getTemEnSveDep())));
			});
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolDepartement", e.getCause());
		}
		return liste;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDipAutCur()
	 */
	@Override
	public List<SiScolDipAutCur> getListSiScolDipAutCur()
			throws SiScolException {		
		try{
			List<SiScolDipAutCur> liste = new ArrayList<SiScolDipAutCur>();
			executeQueryListEntity(DipAutCur.class).forEach(dipAutCur->{				
				liste.add(new SiScolDipAutCur(dipAutCur.getCodDac(),dipAutCur.getLibDac(),dipAutCur.getLicDac(),
						MethodUtils.getBooleanFromTemoin(dipAutCur.getTemEnSveDac())));
			});		
			return liste;
		}catch(Exception e){
			throw new SiScolException("SiScol database erroron getListSiScolDipAutCur", e.getCause());
		}		
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolEtablissement()
	 */
	@Override
	public List<SiScolEtablissement> getListSiScolEtablissement()
			throws SiScolException {
		try{
			List<SiScolEtablissement> liste = new ArrayList<SiScolEtablissement>();
			executeQueryListEntity(Etablissement.class).forEach(etablissement->{
				SiScolEtablissement siScolEtablissement = new SiScolEtablissement(etablissement.getCodEtb(),etablissement.getLibEtb(),
						etablissement.getLibWebEtb(),etablissement.getLicEtb(),
						MethodUtils.getBooleanFromTemoin(etablissement.getTemEnSveEtb()));
				if (etablissement.getDepartement() != null){
					siScolEtablissement.setSiScolDepartement(new SiScolDepartement(etablissement.getDepartement().getCodDep()));
				}
				if (etablissement.getCommune() != null){
					siScolEtablissement.setSiScolCommune(new SiScolCommune(etablissement.getCommune().getCodCom()));
				}
				liste.add(siScolEtablissement);
			});		
			return liste;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolEtablissement", e.getCause());
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolMention()
	 */
	@Override
	public List<SiScolMention> getListSiScolMention() throws SiScolException {
		try{
			List<SiScolMention> liste = new ArrayList<SiScolMention>();
			executeQueryListEntity(Mention.class).forEach(mention->{				
				liste.add(new SiScolMention(mention.getCodMen(),mention.getLibMen(),
				mention.getLicMen(), MethodUtils.getBooleanFromTemoin(mention.getTemEnSveMen())));
			});		
			return liste;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolMention", e.getCause());
		}
	}
	
	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolTypResultat()
	 */
	@Override
	public List<SiScolTypResultat> getListSiScolTypResultat()	throws SiScolException {
		try{
			List<SiScolTypResultat> liste = new ArrayList<SiScolTypResultat>();
			executeQueryListEntity(TypResultat.class).forEach(typResultat->{				
				liste.add(new SiScolTypResultat(typResultat.getCodTre(),typResultat.getLibTre(),
						typResultat.getLicTre(), MethodUtils.getBooleanFromTemoin(typResultat.getTemEnSveTre())));
			});		
			return liste;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolTypResultat", e.getCause());
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolMentionNivBac()
	 */
	@Override
	public List<SiScolMentionNivBac> getListSiScolMentionNivBac()
			throws SiScolException {
		try{
			List<SiScolMentionNivBac> liste = new ArrayList<SiScolMentionNivBac>();
			executeQueryListEntity(MentionNivBac.class).forEach(mentionNivBac->{				
				liste.add(new SiScolMentionNivBac(mentionNivBac.getCodMnb(), mentionNivBac.getLibMnb(),
				mentionNivBac.getLicMnb(), MethodUtils.getBooleanFromTemoin(mentionNivBac.getTemEnSveMnb())));
			});		
			return liste;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolMentionNivBac", e.getCause());
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolPays()
	 */
	@Override
	public List<SiScolPays> getListSiScolPays() throws SiScolException {
		try{
			List<SiScolPays> liste = new ArrayList<SiScolPays>();
			executeQueryListEntity(Pays.class).forEach(pays->{				
				liste.add(new SiScolPays(pays.getCodPay(), pays.getLibNat(),
				pays.getLibPay(), pays.getLicPay(),
				MethodUtils.getBooleanFromTemoin(pays.getTemEnSvePay())));
			});		
			return liste;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolPays", e.getCause());
		}
	}


	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolTypDiplome()
	 */
	@Override
	public List<SiScolTypDiplome> getListSiScolTypDiplome()
			throws SiScolException {
		try{
			List<SiScolTypDiplome> liste = new ArrayList<SiScolTypDiplome>();
			executeQueryListEntity(TypDiplome.class).forEach(typDiplome->{				
				liste.add(new SiScolTypDiplome(typDiplome.getCodTpdEtb(), typDiplome.getLibTpd(),
				typDiplome.getLicTpd(), MethodUtils.getBooleanFromTemoin(typDiplome.getTemEnSveTpd())));
			});		
			return liste;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolTypDiplome", e.getCause());
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolUtilisateur()
	 */
	@Override
	public List<SiScolUtilisateur> getListSiScolUtilisateur()
			throws SiScolException {
		try{
			List<SiScolUtilisateur> liste = new ArrayList<SiScolUtilisateur>();
			executeQueryListEntity(Utilisateur.class).forEach(utilisateur->{	
				SiScolUtilisateur siScolUtilisateur = new SiScolUtilisateur(utilisateur.getCodUti(), utilisateur.getAdrMailUti(),
					utilisateur.getLibCmtUti(),	MethodUtils.getBooleanFromTemoin(utilisateur.getTemEnSveUti()));
				if (utilisateur.getCentreGestion()!=null){
					siScolUtilisateur.setSiScolCentreGestion(new SiScolCentreGestion(utilisateur.getCentreGestion().getCodCge()));
				}
				liste.add(siScolUtilisateur);				
			});		
			return liste;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolUtilisateur", e.getCause());
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolComBdi()
	 */
	@Override
	public List<SiScolComBdi> getListSiScolComBdi() throws SiScolException {
		try{
			List<SiScolComBdi> liste = new ArrayList<SiScolComBdi>();
			executeQueryListEntity(ComBdi.class).forEach(comBdi->{				
				liste.add(new SiScolComBdi(comBdi.getId().getCodCom(),comBdi.getId().getCodBdi()));
			});		
			return liste;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolComBdi", e.getCause());
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolAnneeUni()
	 */
	@Override
	public List<SiScolAnneeUni> getListSiScolAnneeUni() throws SiScolException {
		try{
			List<SiScolAnneeUni> liste = new ArrayList<SiScolAnneeUni>();
			executeQueryListEntity(AnneeUni.class).forEach(anneeUni->{				
				liste.add(new SiScolAnneeUni(anneeUni.getCodAnu(), anneeUni.getEtaAnuIae(), anneeUni.getLibAnu(),
						anneeUni.getLicAnu()));
			});		
			return liste;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getListSiScolAnneeUni", e.getCause());
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getVersion()
	 */
	@Override
	public Version getVersion() throws SiScolException {
		try{
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
			EntityManager em = emf.createEntityManager();
			Query query = em.createQuery("Select a from VersionApo a order by a.datCre desc", VersionApo.class).setMaxResults(1);
			List<VersionApo> listeVersionApo = query.getResultList();
			em.close();
			if (listeVersionApo!=null && listeVersionApo.size()>0){
				VersionApo versionApo = listeVersionApo.get(0);
				return new Version(versionApo.getId().getCodVer());
			}else{
				return null;
			}			
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getVersion", e.getCause());
		}
	}
	
	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListFormation(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Vet> getListFormation(String codeCge, String search) throws SiScolException{
		try{
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
			EntityManager em = emf.createEntityManager();
			if (search!=null && search.length()>0){
				search = "%"+search.toLowerCase()+"%";
			}
			/*String sqlString = "select "+
					"vdi_fractionner_vet.cod_dip as codDip,"+
					"vdi_fractionner_vet.cod_vrs_vdi as codVrsVdi,"+
					"vdi_fractionner_vet.cod_etp as codEtp, "+
					"vdi_fractionner_vet.cod_vrs_vet as codVrsVet,"+					
					"version_diplome.lic_vdi as licVdi,"+
					"version_etape.cod_cge_min_vet as codCgeMinVet,"+
					"diplome.cod_tpd_etb as codTpdEtb "+
					"from version_etape,vdi_fractionner_vet,version_diplome,diplome "+
					"where "+
					"vdi_fractionner_vet.cod_dip = version_diplome.cod_dip "+
					"and vdi_fractionner_vet.cod_vrs_vdi = version_diplome.cod_vrs_vdi "+
					"and vdi_fractionner_vet.cod_etp = version_etape.cod_etp "+
					"and vdi_fractionner_vet.cod_vrs_vet = version_etape.cod_vrs_vet "+
					"and version_diplome.cod_dip = diplome.cod_dip "+
					"and version_etape.cod_cge_min_vet is not null and lic_vdi like ?1";*/
			
			String sqlString = "select * from (select distinct "+
					"version_etape.cod_etp as codEtpVet, "+
					"version_etape.cod_vrs_vet as codVrsVet, "+
					"version_etape.lib_web_vet as libVet, "+
					"etp_gerer_cge.cod_cge as codCge, "+
					"diplome.cod_tpd_etb as codTpd, "+
					"typ_diplome.lib_tpd as libTypDip "+
					"from version_etape, diplome, vdi_fractionner_vet, etp_gerer_cge, typ_diplome "+
					"where "+
					"vdi_fractionner_vet.cod_dip = diplome.cod_dip "+
					"and diplome.cod_tpd_etb = typ_diplome.cod_tpd_etb "+
					"and vdi_fractionner_vet.cod_etp = version_etape.cod_etp "+
					"and vdi_fractionner_vet.cod_vrs_vet = version_etape.cod_vrs_vet "+
					"and vdi_fractionner_vet.daa_deb_rct_vet<=(select max(cod_anu) from annee_uni where eta_anu_iae in ('O','I')) "+
					"and vdi_fractionner_vet.daa_fin_rct_vet>=(select min(cod_anu) from annee_uni where eta_anu_iae in ('O','I')) "+
					"and etp_gerer_cge.cod_etp = version_etape.cod_etp "+
					"and "+
					"(LOWER(version_etape.lib_web_vet) like ?1 "+
					"or "+
					"LOWER(version_etape.cod_etp||'-'||version_etape.cod_vrs_vet) like ?1)";
			
			if (codeCge != null){
				sqlString += " and etp_gerer_cge.cod_cge =?2";
			}
			sqlString += ") where rownum < "+ConstanteUtils.NB_MAX_RECH_FORM;
			Query query = em.createNativeQuery(sqlString,Vet.class);
			//System.out.println(sqlString);
			query.setParameter(1, search);
			if (codeCge != null){
				query.setParameter(2, codeCge);
			}
			
			return query.getResultList();
		}catch(Exception e){
			logger.error("erreur",e);
			throw new SiScolException("SiScol database error on getListFormationApogee", e.getCause());
		}
		
	}
	

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getIndividu(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public WSIndividu getIndividu(String codEtu, String ine, String cleIne) throws SiScolException {
		if(monProxyEtu==null){
			monProxyEtu = (EtudiantMetierServiceInterface) WSUtils.getService(WSUtils.ETUDIANT_SERVICE_NAME);
		}
		try {
			IdentifiantsEtudiantDTO etudiant = monProxyEtu.recupererIdentifiantsEtudiant(codEtu, null, ine, cleIne, null, null, null, null, null, "N");
			if (etudiant!=null && etudiant.getCodEtu()!=null){
				InfoAdmEtuDTO data = monProxyEtu.recupererInfosAdmEtu(etudiant.getCodEtu().toString());
				if (data != null){
					String civilite = "";
					if (data.getSexe()!=null){
						if (data.getSexe().equals("F")){
							civilite = "2";
						}else{
							civilite = "1";
						}
					}
					/*civilite*/
					WSIndividu individu = new WSIndividu(etudiant.getCodInd(), civilite, new BigDecimal(etudiant.getCodEtu()), etudiant.getNumeroINE(),etudiant.getCleINE(),
							data.getDateNaissance(), data.getNomPatronymique(), data.getNomUsuel(),
							data.getPrenom1(), data.getPrenom2(), data.getLibVilleNaissance());
					
					if (data.getDepartementNaissance()!=null){						
						individu.setCodDepNai(data.getDepartementNaissance().getCodeDept());
					}
					if (data.getPaysNaissance()!=null){
						individu.setCodPayNai(data.getPaysNaissance().getCodPay());
					}else{
						individu.setCodPayNai(ConstanteUtils.PAYS_CODE_FRANCE);
					}
					if (data.getNationaliteDTO()!=null){
						individu.setCodPayNat(data.getNationaliteDTO().getCodeNationalite());
					}else{
						individu.setCodPayNat(ConstanteUtils.PAYS_CODE_FRANCE);
					}
					
					/*Recuperation du bac*/
					if (data.getListeBacs()!=null){
						List<IndBacDTO> liste = Arrays.asList(data.getListeBacs());
						Optional<IndBacDTO> optBac = liste.stream().sorted((e1, e2) -> e2.getAnneeObtentionBac().compareTo(e1.getAnneeObtentionBac())).findFirst();
						if (optBac.isPresent()){
							IndBacDTO bacDTO = optBac.get();
							
							WSBac bac = new WSBac();
							bac.setCodBac(bacDTO.getCodBac());
							bac.setDaaObtBacIba(bacDTO.getAnneeObtentionBac());
							if (bacDTO.getDepartementBac()!=null){
								bac.setCodDep(bacDTO.getDepartementBac().getCodeDept());
							}
							if (bacDTO.getEtbBac()!=null){
								bac.setCodEtb(bacDTO.getEtbBac().getCodeEtb());
							}
							if (bacDTO.getMentionBac()!=null){
								bac.setCodMnb(bacDTO.getMentionBac().getCodMention());
							}
							individu.setBac(bac);
						}
					}
					
					/*Recuperation de l'adresse*/
					individu.setAdresse(getAdresse(etudiant.getCodEtu().toString()));
					
					/*Recuperation du cursus*/
					individu.setListCursusInterne(getCursusInterne(etudiant.getCodEtu().toString()));
					
					return individu;
				}
			}
			return null;
		}
		catch (AxisFault ex) {			
			if (ex.getMessage().equals("technical.data.nullretrieve.etudiant")){
				return null;
			}else if(ex.getMessage().equals("technical.parameter.noncoherentinput.codEtu")){
				return null;
			}else{
				logger.error("erreur",ex);
				throw new SiScolException("Probleme avec le WS lors de la recherche de l'adresse pour etudiant dont codetu est : " + codEtu+" et codIne est : "+ine,ex);
			}
		}	
		catch (Exception ex) {
			logger.error("erreur",ex);
			throw new SiScolException("Probleme avec le WS lors de la recherche de l'adresse pour etudiant dont codetu est : " + codEtu+" et codIne est : "+ine,ex);
		}	
	}


	/** Recupere l'adresse de l'individu par WS
	 * @param codEtu
	 * @return
	 * @throws SiScolException
	 */
	public WSAdresse getAdresse(String codEtu) throws SiScolException {
		if (monProxyEtu == null) {
			monProxyEtu = (EtudiantMetierServiceInterface) WSUtils.getService(WSUtils.ETUDIANT_SERVICE_NAME);
		}
		
		try {
			CoordonneesDTO2 cdto = monProxyEtu.recupererAdressesEtudiant_v2(codEtu, null, "N");
			
			if (cdto == null){
				return null;
			}else{
				WSAdresse adresse = null;
				AdresseDTO2 ada = cdto.getAdresseAnnuelle();
				adresse = transformAdresseWS(ada, cdto.getNumTelPortable());
				if (adresse!=null){
					return adresse;
				}
				AdresseDTO2 adf = cdto.getAdresseFixe();
				return transformAdresseWS(adf, cdto.getNumTelPortable());
			}
		} 
		catch (AxisFault ex) {			
			if (ex.getMessage().equals("technical.data.nullretrieve.findIAA")){
				return null;
			}
			logger.error("erreur",ex);
			throw new SiScolException("Probleme lors de la recherche de l'adresse pour etudiant dont codetu est : " + codEtu,ex);
		}
		catch (WebBaseException ex) {
			//Si on est dans un cas d'erreur non expliqué
			if (ex.getNature().equals("technical.ws.remoteerror.global")){
				logger.error("erreur",ex);
				throw new SiScolException("Probleme avec le WS lors de la recherche de l'adresse pour etudiant dont codetu est : " + codEtu,ex);
			}else{
				return null;
			}
		} catch (Exception ex) {
			logger.error("erreur",ex);
			throw new SiScolException("Probleme lors de la recherche de l'adresse pour etudiant dont codetu est : " + codEtu,ex);
		}	
	}
	
	/** transforme une adresse provenant du WS en adresse provenant d'apogee par requete
	 * @param adrWs
	 * @param numPortable
	 * @return l'adresse formatée
	 */
	private WSAdresse transformAdresseWS(AdresseDTO2 adrWs, String numPortable){
		if (adrWs==null){
			return null;
		}
		WSAdresse adresse = new WSAdresse();
		adresse.setCodAdr(null);
		adresse.setLibAd1(adrWs.getLibAd1());
		adresse.setLibAd2(adrWs.getLibAd2());
		adresse.setLibAd3(adrWs.getLibAd3());
		adresse.setNumTel(adrWs.getNumTel());
		adresse.setNumTelPort(numPortable);
		adresse.setLibAde(adrWs.getLibAde());
		if (adrWs.getCommune()!=null){
			adresse.setCodCom(adrWs.getCommune().getCodeInsee());
			adresse.setCodBdi(adrWs.getCommune().getCodePostal());
		}
		if (adrWs.getPays() != null) {
			adresse.setCodPay(adrWs.getPays().getCodPay());
		}		
		return adresse;
	}


	/** Recupere le cursus interne d'un individu par WS
	 * @param codEtu
	 * @return
	 * @throws SiScolException
	 */
	public List<WSCursusInterne> getCursusInterne(String codEtu)
			throws SiScolException {
		try {			
			if (monProxyPedagogique == null) {
				monProxyPedagogique = (PedagogiqueMetierServiceInterface) WSUtils.getService(WSUtils.PEDAGOGIQUE_SERVICE_NAME);
			}
			List<WSCursusInterne> liste = new ArrayList<WSCursusInterne>();
			ContratPedagogiqueResultatVdiVetDTO2[] resultatVdiVet = monProxyPedagogique
					.recupererContratPedagogiqueResultatVdiVet_v2(codEtu,
							"toutes", "Apogee", "ET", "toutes", "tous","E");
			if (resultatVdiVet != null && resultatVdiVet.length > 0) {
				for (int i = 0; i < resultatVdiVet.length; i++) {
					// information sur le diplome:
					ContratPedagogiqueResultatVdiVetDTO2 rdto = resultatVdiVet[i];
					// information sur les etapes:
					EtapeResVdiVetDTO2[] etapes = rdto.getEtapes();
					if (etapes != null && etapes.length > 0) {

						for (int j = 0; j < etapes.length; j++) {
							EtapeResVdiVetDTO2 etape = etapes[j];

							// résultats de l'étape:
							ResultatVetDTO[] tabresetape = etape.getResultatVet();
							if (tabresetape != null && tabresetape.length > 0) {
								for (int k = 0; k < tabresetape.length; k++) {
									ResultatVetDTO ret = tabresetape[k];

									WSCursusInterne cursus = new WSCursusInterne(
											etape.getEtape().getCodEtp() + "/" + etape.getEtape().getCodVrsVet(),
											etape.getEtape().getLibWebVet() + " - " + ret.getSession().getLibSes(),
											etape.getCodAnu(),
											(ret.getMention() != null) ? ret.getMention().getCodMen() : null,
											(ret.getTypResultat() != null) ? ret.getTypResultat().getCodTre() : null);
									liste.add(cursus);

								}
							}

						}
					}
				}

			}
			return liste;
		}catch (AxisFault ex) {			
			if (ex.getMessage().equals("technical.data.nullretrieve.findIAA")){
				return null;
			}
			logger.error("erreur",ex);
			throw new SiScolException("Probleme lors de la recherche des notes et resultats pour etudiant dont codetu est : " + codEtu,ex);
		}		
		catch (WebBaseException ex) {			
			//Si on est dans un cas d'erreur non expliqué
			if (ex.getNature().equals("technical.ws.remoteerror.global")){
				logger.error("erreur",ex);
				throw new SiScolException("Probleme avec le WS lors de la recherche de l'adresse pour etudiant dont codetu est : " + codEtu,ex);
			}else{
				return null;
			}
		} catch (Exception ex) {
			logger.error("erreur",ex);
			throw new SiScolException("Probleme lors de la recherche des notes et resultats pour etudiant dont codetu est : " + codEtu,ex);
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#creerOpiViaWS(fr.univlorraine.ecandidat.entities.ecandidat.Candidat, java.lang.String)
	 */
	@Override
	public void creerOpiViaWS(Candidat candidat, String user) {
		logger.debug("creerOpiViaWS");
		//Test que l'année d'obtention du bac est correcte.
		
		CandidatBacOuEqu bacOuEqu = candidat.getCandidatBacOuEqu();
		
		if(bacOuEqu!=null && bacOuEqu.getAnneeObtBac()!=null){
			int anneeObtBac = candidat.getCandidatBacOuEqu().getAnneeObtBac();
			int anneeEnCours = (LocalDate.now()).getYear();
			if(anneeObtBac>anneeEnCours){
				return;
			}
		}
		
		// Donnees de l'individu
		String codOpiIntEpo = parametreController.getPrefixeOPI()+candidat.getCompteMinima().getNumDossierOpiCptMin();
		
		//Voeux-->On cherche tout les voeuyx soumis à OPI-->Recherche des OPI du candidat
		List<Opi> listeOpi = opiRepository.findByCandidatureCandidatIdCandidat(candidat.getIdCandidat());
		List<MAJOpiVoeuDTO> listeMAJOpiVoeuDTO = new ArrayList<MAJOpiVoeuDTO>();
		
		/*Au moins 1 opi n'est pas passé pour lancer l'opi*/
		Boolean opiToPass = false;
		for (Opi opi : listeOpi){
			MAJOpiVoeuDTO mAJOpiVoeuDTO = getVoeuByCandidature(opi.getCandidature());
			if (mAJOpiVoeuDTO!=null){
				if (opi.getDatPassageOpi()==null){
					opiToPass = true;
				}
				listeMAJOpiVoeuDTO.add(mAJOpiVoeuDTO);
			}
		};
		
		/*Au moins 1 opi n'est pas passé pour lancer l'opi*/
		if (!opiToPass){
			return;
		}

		/*boolean opiIsNew = (opi.getCodIndOpiApo() == 0);

		if(opiIsNew){
			opi.setDatCreOpi(new Date());
			opi.setUtilisateur(userController.getUserFromLogin(userController.getCurrentUserName()));
		}

		if (opiIsNew && (opi.getCodOpi() == null || opi.getCodOpi().isEmpty())) {
			opi.setCodOpi(getNewCodOpiIntEpo());
		}*/

		/* Creation des objets DTO */
		DonneesOpiDTO4 donneesOPI = new DonneesOpiDTO4();
		MAJOpiIndDTO3 individu = new MAJOpiIndDTO3();
		MAJEtatCivilDTO etatCivil = new MAJEtatCivilDTO();
		MAJDonneesNaissanceDTO donneesNaissance = new MAJDonneesNaissanceDTO();
		MAJDonneesPersonnellesDTO3 donneesPersonnelles = new MAJDonneesPersonnellesDTO3();
		MAJOpiBacDTO bac = new MAJOpiBacDTO();	

		
		individu.setCodOpiIntEpo(codOpiIntEpo);
		individu.setCodEtuOpi(null);
		if (candidat.getCompteMinima()!=null && candidat.getCompteMinima().getSupannEtuIdCptMin()!=null){
			try{
				individu.setCodEtuOpi(Integer.valueOf(candidat.getCompteMinima().getSupannEtuIdCptMin()));
			}catch(Exception e){}
		}
		

		// Etat Civil
		etatCivil.setLibNomPatIndOpi(candidat.getNomPatCandidat());
		etatCivil.setLibNomUsuIndOpi(candidat.getNomUsuCandidat());
		etatCivil.setLibPr1IndOpi(candidat.getPrenomCandidat());
		//separer le clé du code nne
		if(StringUtils.hasText(candidat.getIneCandidat())){
			etatCivil.setCodNneIndOpi(candidat.getIneCandidat());
			etatCivil.setCodCleNneIndOpi(candidat.getCleIneCandidat());
		}		
		
		if (candidat.getCivilite()!=null && candidat.getCivilite().getCodApo()!=null){
			String codSex = "";
			if (candidat.getCivilite().getCodApo().equals("1")){
				codSex = "M";
			}else{
				codSex = "F";
			}
			etatCivil.setCodSexEtuOpi(codSex);
		}

		// Donnees Naissance
		donneesNaissance.setDateNaiIndOpi(formatterDateTimeApo.format(candidat.getDatNaissCandidat()));
		donneesNaissance.setTemDateNaiRelOpi("N");
		if (candidat.getSiScolPaysNat()!=null){
			donneesNaissance.setCodPayNat(candidat.getSiScolPaysNat().getCodPay());
		}
		donneesNaissance.setLibVilNaiEtuOpi(candidat.getLibVilleNaissCandidat());
		
		if(candidat.getSiScolDepartement()==null){
			donneesNaissance.setCodTypDepPayNai("P");
			donneesNaissance.setCodDepPayNai(candidat.getSiScolPaysNaiss().getCodPay());
		}else{
			donneesNaissance.setCodTypDepPayNai("D");
			donneesNaissance.setCodDepPayNai(candidat.getSiScolDepartement().getCodDep());
		}

		// donnees personnelles
		donneesPersonnelles.setAdrMailOpi(candidat.getCompteMinima().getMailPersoCptMin());
		donneesPersonnelles.setNumTelPorOpi(candidat.getTelPortCandidat());
		// BAC
		if(candidat.getCandidatBacOuEqu()!=null && candidat.getCandidatBacOuEqu().getAnneeObtBac()!=null){	
			bac.setCodBac(bacOuEqu.getSiScolBacOuxEqu().getCodBac());
			bac.setDaaObtBacOba(bacOuEqu.getAnneeObtBac().toString());
			bac.setCodDep((bacOuEqu.getSiScolDepartement())!=null?bacOuEqu.getSiScolDepartement().getCodDep():null);
		}

		individu.setEtatCivil(etatCivil);
		individu.setDonneesNaissance(donneesNaissance);
		individu.setDonneesPersonnelles(donneesPersonnelles);
		donneesOPI.setIndividu(individu);
		donneesOPI.setBac(bac);
		
		/*Donnes d'adresse*/
		if (ConstanteUtils.OPI_ADR_MODE != ConstanteUtils.OPI_ADR_NO_RECUP){
			Adresse adresseCandidat = candidat.getAdresse();
			if (adresseCandidat!=null){
				if (ConstanteUtils.OPI_ADR_MODE == ConstanteUtils.OPI_ADR_FIXE){
					donneesOPI.setAdresseFixe(getAdresseOPI(adresseCandidat, candidat));
				}else if (ConstanteUtils.OPI_ADR_MODE == ConstanteUtils.OPI_ADR_ANNEE){
					donneesOPI.setAdresseAnnuelle(getAdresseOPI(adresseCandidat, candidat));
				}else if (ConstanteUtils.OPI_ADR_MODE == ConstanteUtils.OPI_ADR_BOTH){
					donneesOPI.setAdresseFixe(getAdresseOPI(adresseCandidat, candidat));
					donneesOPI.setAdresseAnnuelle(getAdresseOPI(adresseCandidat, candidat));
				}				
			}			
		}
		

		/*Les voeux*/
		logger.debug("listVoeux "+listeMAJOpiVoeuDTO.size());
		if(listeMAJOpiVoeuDTO!=null && listeMAJOpiVoeuDTO.size()>0){
			MAJOpiVoeuDTO[] tabDonneesVoeux = new MAJOpiVoeuDTO[listeMAJOpiVoeuDTO.size()];
			int rang=0;
			for(MAJOpiVoeuDTO v : listeMAJOpiVoeuDTO){
				tabDonneesVoeux[rang] = v;
				rang++;
			}
			donneesOPI.setVoeux(tabDonneesVoeux);
		}else{
			return;
		}		


		boolean actionWSok = false;
		try {
			if(monProxyOpi==null){				
				monProxyOpi = (OpiMetierServiceInterface) WSUtils.getService(WSUtils.OPI_SERVICE_NAME);				
			}
			logger.debug("lancement ws OPI");
			monProxyOpi.mettreajourDonneesOpi_v4(donneesOPI);
			logger.debug("fin ws OPI");
			actionWSok = true;
		} catch (Exception e) {
			logger.error("erreur ws OPI", e);
			return;
		}

		//Si l'appel au WS s'est bien passé
		if(actionWSok){
			//Vérifie si le code NNE est passé : si non, on supprime l'OPI si c'est une nouvelle OPI//
			IndOpi indOpi = findNneIndOpiByCodOpiIntEpo(codOpiIntEpo);

			//Test si on n'a pas réussi a recuprer l'opi qu'on vient de créer/mettre a jour dans apogee
			if(indOpi==null){
				logger.debug("Probleme d'insertion de l'OPI dans Apogée");
				return;
			}

			if ( (candidat.getIneCandidat() != null) && !(candidat.getIneCandidat().equals(indOpi.getCodNneIndOpi()))) {
				logger.debug("Probleme d'insertion du NNE dans Apogée");
				return;
			}
			
			/*Mise a jour de la date de passage de l'opi*/
			try {
				List<VoeuxIns> listeVoeux = getVoeuxApogee(indOpi);
				listeVoeux.forEach(voeu->{
					listeOpi.stream().filter(
							opi->opi.getDatPassageOpi()==null
							&&
							voeu.getId().getCodEtp().equals(opi.getCandidature().getFormation().getCodEtpVetApoForm())
							&&
							String.valueOf(voeu.getId().getCodVrsVet()).equals(opi.getCandidature().getFormation().getCodVrsVetApoForm())
							&&
							voeu.getId().getCodCge().equals(opi.getCandidature().getFormation().getSiScolCentreGestion().getCodCge())
							).collect(Collectors.toList()).forEach(opiFiltre->{
								opiFiltre.setDatPassageOpi(LocalDateTime.now());
								opiRepository.save(opiFiltre);
					});
				});
				
			} catch (SiScolException e) {
				logger.debug("Probleme d'insertion des voeux dans Apogée");
				//Affichage message dans l'interface
				return;
			}

			//Si on a des voeux , vérification voeux bien inséré dans Apogée
			/*if(listeMAJOpiVoeuDTO!=null && listeMAJOpiVoeuDTO.size()>0){
				int nbVoeuxApogee = getNbVoeuxApogee(indOpi);
				if(listeMAJOpiVoeuDTO.size()!=nbVoeuxApogee){
					//On n'a pas le bon nombre de voeux dans Apogée
					logger.debug("Probleme d'insertion des voeux dans Apogée");
					//Affichage message dans l'interface
					return;
				}else{
					listeOpi.forEach(opi->{
						opi.setDatPassageOpi(LocalDateTime.now());
						opiRepository.save(opi);
					});
				}
			}*/
		}
		return;
	}
	
	/** Renvoie les voeux OPI d'un individu
	 * @param indOpi
	 * @return
	 * @throws SiScolException
	 */
	private List<VoeuxIns> getVoeuxApogee(IndOpi indOpi) throws SiScolException{
		try{
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
			EntityManager em = emf.createEntityManager();
			Query query = em.createQuery("Select a from VoeuxIns a where a.id.codIndOpi = "+indOpi.getCodIndOpi(), VoeuxIns.class);
			List<VoeuxIns> listeSiScol = query.getResultList();
			em.close();
			return listeSiScol;
		}catch(Exception e){
			throw new SiScolException("SiScol database error on getVoeuxApogee", e.getCause());
		}
		
	}
	
	/**
	 * @param indOpi
	 * @return le nombre de voeux apogées
	 */
	/*private int getNbVoeuxApogee(IndOpi indOpi){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
		EntityManager em = emf.createEntityManager();
		Integer nb = ((BigDecimal)em.createNativeQuery("select count(*) from VOEUX_INS where COD_IND_OPI="+indOpi.getCodIndOpi()+"").getSingleResult()).intValue();
		em.close();
		return nb;
	}*/
	
	/**
	 * @param codOpiIntEpo
	 * @return l'INE ind opi
	 */
	private IndOpi findNneIndOpiByCodOpiIntEpo(String codOpiIntEpo) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
		EntityManager em = emf.createEntityManager();
		
		Query query = em.createQuery("Select a from IndOpi a where a.codOpiIntEpo='"+codOpiIntEpo+"'", IndOpi.class);
		List<IndOpi> lindopi = query.getResultList();
		em.close();

		if(lindopi!=null && lindopi.size()==1){
			return lindopi.get(0);
		}
		return null;
	}
	
	/** Transforme une candidature en voeuy OPI
	 * @param candidature
	 * @return transforme une candidature en voeu
	 */
	private MAJOpiVoeuDTO getVoeuByCandidature(Candidature candidature){
		Formation formation = candidature.getFormation();
		if (formation.getCodEtpVetApoForm()==null || formation.getCodVrsVetApoForm()==null || formation.getSiScolCentreGestion()==null){
			return null;
		}
		
		MAJOpiVoeuDTO voeu = new MAJOpiVoeuDTO();
		voeu.setNumCls(1);
		voeu.setCodCmp(null);
		voeu.setCodCge(formation.getSiScolCentreGestion().getCodCge());
		voeu.setCodDip(null);
		voeu.setCodVrsVdi(null);
		voeu.setCodEtp(formation.getCodEtpVetApoForm());
		voeu.setCodVrsVet(Integer.parseInt(formation.getCodVrsVetApoForm()));
		voeu.setCodSpe1Opi(null);
		voeu.setCodSpe2Opi(null);
		voeu.setCodSpe3Opi(null);
		voeu.setCodTyd(null);
		voeu.setCodAttDec(null);
		voeu.setCodDecVeu("F");
		voeu.setCodDemDos("C");
		voeu.setCodMfo(null);
		voeu.setTemValPsd("N");
		voeu.setLibCmtJur(null);
		voeu.setTitreAccesExterne(null);
		voeu.setConvocation(null);
		
		//logger.debug("Ajout voeux "+v.getIdVx()+" "+v.getRangVx()+" "+v.getCodCge()+" "+v.getCodEtp()+" "+v.getCodVrsVet()+" opi : "+opi.getCodOpi() +" apo : "+opi.getCodIndOpiApo());
		//logger.debug(" -cge voeux : "+tabDonneesVoeux[rang].getCodCge());
		
		return voeu;
	}
	
	/**
	 * @param adresseCandidat
	 * @param candidat
	 * @return l'adresse transformée
	 */
	private MAJOpiAdresseDTO getAdresseOPI(Adresse adresseCandidat, Candidat candidat){
		MAJOpiAdresseDTO adresse = new MAJOpiAdresseDTO();
		adresse.setCodBdi(adresseCandidat.getCodBdiAdr());
		if (adresseCandidat.getSiScolPays()!=null){
			adresse.setCodPay(adresseCandidat.getSiScolPays().getCodPay());
		}
		if (adresseCandidat.getSiScolCommune()!=null){
			adresse.setCodCom(adresseCandidat.getSiScolCommune().getCodCom());
		}
		
		adresse.setLib1(adresseCandidat.getAdr1Adr());
		adresse.setLib2(adresseCandidat.getAdr2Adr());
		adresse.setLib3(adresseCandidat.getAdr3Adr());
		adresse.setLibAde(adresseCandidat.getLibComEtrAdr());
		if (candidat.getTelCandidat()!=null){
			adresse.setNumTel(candidat.getTelCandidat());
		}else if(candidat.getTelPortCandidat()!=null){
			adresse.setNumTel(candidat.getTelPortCandidat());
		}
		
		return adresse;
	}
}