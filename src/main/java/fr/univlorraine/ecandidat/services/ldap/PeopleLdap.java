package fr.univlorraine.ecandidat.services.ldap;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entité Ldap : People
 */
@Data @EqualsAndHashCode(of="uid")
public class PeopleLdap implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 430282979875502022L;

	private String[] objectClass;
	private String displayName;
	private String sn;
	private String cn;
	private String uid;
	private String mail;
	private String supannEtuId;
	private String supannCivilite;
	private String givenName;

	public PeopleLdap(){}

	public PeopleLdap(String uid, String displayName, String sn, String cn,
			String mail, String supannEtuId, String supannCivilite,
			String givenName) {
		super();
		this.uid = uid;
		this.displayName = displayName;
		this.sn = sn;
		this.cn = cn;		
		this.mail = mail;
		this.supannEtuId = supannEtuId;
		this.supannCivilite = supannCivilite;
		this.givenName = givenName;
	}
	
	

}
