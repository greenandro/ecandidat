package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierCandidat implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = -8731423927468997712L;

	private String numeroDossier;
	private String civilite;
	private String nomPatronymique;
	private String nomUsage;
	private String prenom;
	private String dateNaissance;
	private String villeNaissance;
	private String nationalite;
	private String codeEtudiant;
	private String telPort;
	private String telFixe;
	private String mail;
	private String adresse;

	public ExportDossierCandidat() {
		super();
	}

	public ExportDossierCandidat(CompteMinima cptMin, Candidat candidat, String dtNaiss, String adresse) {
		this.setNumeroDossier(MethodUtils.formatToExport(cptMin.getNumDossierOpiCptMin()));
		this.setCivilite(MethodUtils.formatToExport(candidat.getCivilite().getLibCiv()));
		this.setNomPatronymique(MethodUtils.formatToExport(candidat.getNomPatCandidat()));
		this.setNomUsage(MethodUtils.formatToExport(candidat.getNomUsuCandidat()));
		this.setPrenom(MethodUtils.formatToExport(candidat.getPrenomCandidat()));		
		this.setVilleNaissance(MethodUtils.formatToExport(candidat.getLibVilleNaissCandidat()));
		this.setNationalite(MethodUtils.formatToExport(candidat.getSiScolPaysNat().getLibNat()));
		this.setCodeEtudiant(MethodUtils.formatToExport(cptMin.getSupannEtuIdCptMin()));
		this.setTelPort(MethodUtils.formatToExport(candidat.getTelPortCandidat()));
		this.setTelFixe(MethodUtils.formatToExport(candidat.getTelCandidat()));
		this.setMail(MethodUtils.formatToExport(cptMin.getMailPersoCptMin()));
		this.setDateNaissance(MethodUtils.formatToExport(dtNaiss));
		this.setAdresse(MethodUtils.formatToExport(adresse));
	}
}
