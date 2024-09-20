package com.dirignani.webapp;

import org.springframework.data.relational.core.mapping.Table;

import jakarta.persistence.Entity;
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
@Table(name = "corrispondenza")
public class Corrispondenza implements Comparable<Corrispondenza>{
	
	@Id
	private int riga;
	private int attivita;
	private String mittente;
	private String testo;
	private long time;
	
	public Corrispondenza() {}

	public Corrispondenza(int riga, int attivita, String mittente, String testo) {
		//super();
		this.riga = riga;
		this.attivita = attivita;
		this.mittente = mittente;
		this.testo = testo;
		this.time = System.currentTimeMillis();
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getRiga() {
		return riga;
	}

	public void setRiga(int riga) {
		this.riga = riga;
	}

	public int getAttivita() {
		return attivita;
	}

	public void setAttivita(int attivita) {
		this.attivita = attivita;
	}

	public String getMittente() {
		return mittente;
	}

	public void setMittente(String mittente) {
		this.mittente = mittente;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	@Override
	public int compareTo(Corrispondenza o) {
		return Long.valueOf(o.time).compareTo(this.time);
	}

}
