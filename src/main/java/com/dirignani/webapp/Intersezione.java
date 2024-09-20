package com.dirignani.webapp;

import java.util.ArrayList;
import java.util.Collections;

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
@Table(name = "intersezione")
public class Intersezione {
	
	@Id
	private int riga;
	private String name;
	private int identificatore;
	
	public Intersezione(int riga, String name, int identificatore) {
		this.riga = riga;
		this.name = name;
		this.identificatore = identificatore;
	}
	
	public Intersezione() {
		this(0, "", 0);
	}

	public int getRiga() {
		return riga;
	}

	public void setRiga(int riga) {
		this.riga = riga;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIdentificatore() {
		return identificatore;
	}

	public void setIdentificatore(int identificatore) {
		this.identificatore = identificatore;
	}

	@Override
	public String toString() {
		return riga+" - "+name+" - "+identificatore;
	}
	
	public static String getAllNames(ArrayList<Intersezione> intersections) {
		Collections.sort(intersections, (a, b) -> a.name.compareTo(b.name) );
		String totalString = "";
		for(Intersezione intersection: intersections) {
			totalString += intersection.name+", ";
		}
		totalString = totalString.substring(0, (totalString.length()-2));
		return totalString;
	}
	
}
