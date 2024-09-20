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
@Table(name = "attivita")
public class Attivita implements Comparable<Attivita>{
	
	public static enum Priorita{NESSUNA, BASSA, MEDIA, ALTA, URGENTE};
	public static enum Stato{ASSEGNATA, RIFIUTATA, ACCETTATA, IN_CORSO, CONCLUSA};
	
	@Id
	private int identificatore;
	private String testo;
	@Enumerated(EnumType.STRING)
	private Priorita priorita;
	private String owner;
	private String descrizione;
	@Enumerated(EnumType.STRING)
	private Stato stato;
	
	public Attivita(int identificatore, String testo, Priorita priorita, String owner, String descrizione) {
		this.identificatore = identificatore;
		this.testo = testo.trim();
		this.priorita = priorita;
		this.owner = owner;
		this.descrizione = descrizione;
		this.stato = Stato.ASSEGNATA;
	}
	
	public Attivita(int identificatore, String testo, Priorita priorita, String owner) {
		this(identificatore, testo, priorita, owner, "Nessuna descrizione");
	}
	
	public Attivita() {
		this(0, "", Priorita.NESSUNA, "");
	}

	@Override
	public String toString() {
		return identificatore+" - "+testo+" - "+priorita;
	}
	
	public void nextStatus() {
		switch(stato) {
			case ASSEGNATA:
				stato = Stato.ACCETTATA;
				break;
			case ACCETTATA:
				stato = Stato.IN_CORSO;
				break;
			case IN_CORSO:
				stato = Stato.CONCLUSA;
				break;
		}
	}
	
	public void refuse() {
		stato = stato.RIFIUTATA;
	}

	public Stato getStato() {
		return stato;
	}

	public void setStato(Stato stato) {
		this.stato = stato;
	}

	public int getIdentificatore() {
		return identificatore;
	}

	public void setIdentificatore(int identificatore) {
		this.identificatore = identificatore;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Priorita getPriorita() {
		return priorita;
	}

	public void setPriorita(Priorita priorita) {
		this.priorita = priorita;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public boolean isThis(int number) {
		return this.identificatore == number;
	}
	
	public boolean hasSameText(String text) {
		return this.testo.equals(text);
	}
	
	@Override
	public int compareTo(Attivita other) {
		if(other.priorita == this.priorita) {
			return this.testo.compareTo(other.testo);
		}
		else {
			Priorita[] hierarchy = {Priorita.URGENTE, Priorita.ALTA, Priorita.MEDIA, Priorita.BASSA, Priorita.NESSUNA};
			int thisPriorityValue = getIndex(hierarchy, this.priorita);
			int otherPriorityValue = getIndex(hierarchy, other.priorita);
			return Integer.valueOf(thisPriorityValue).compareTo(otherPriorityValue);
		}
	}
	
	private int getIndex(Priorita[] array, Priorita priorita) {
		for(int i=0; i<array.length; i++) {
			if(array[i] == priorita)
				return i;
		}
		return -1;
	}
	
}
