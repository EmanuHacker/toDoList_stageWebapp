package com.dirignani.webapp;

import org.springframework.data.relational.core.mapping.Table;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // Generates getters, setters, toString, equals, and hashCode methods.
@NoArgsConstructor // Generates a no-args constructor.
@AllArgsConstructor // Generates a constructor with all arguments.
@Builder
@Table(name = "utenti")
public class Utenti{
	
	public static enum Roles{USER, ADMIN, OWNER};
	
	@Id
	private String name;
	private String mail;
	private String pswd;
	@Enumerated(EnumType.STRING)
	private Roles role;
	
	public Utenti() {}
	
	public Utenti(String mail, String name, String password, Roles role) {
		this.mail = mail.trim();
		this.name = name.trim();
		this.pswd = password.trim();
		this.role = role;
	}
	
	public Utenti(String mail, String name, String password) {
		this(mail, name, password, Roles.USER);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPswd() {
		return pswd;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}
	
	public boolean isSuperior(Utenti other) {
		if(other.equals(this)) {
			return false;
		}
		else if(this.role == Roles.OWNER) {
			return true;
		}
		else if(this.role == Roles.ADMIN && other.role == Roles.USER) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		return name+" ("+mail+")";
	}
	
	public boolean isThis(String s) {
		return this.mail.equals(s) || this.name.equals(s);
	}
	
}
