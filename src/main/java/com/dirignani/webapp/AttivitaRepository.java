package com.dirignani.webapp;

import java.util.ArrayList;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;

public interface AttivitaRepository extends JpaRepository<Attivita, Integer> {
	
	@Transactional
	@Modifying
	@Query("DELETE FROM attivita WHERE identificatore=?1")
	public void deleteByIdentificatore(int identificatore);
	
	default int getValidIdentificatore() {
		ArrayList<Integer> allActivityIdentifier = new ArrayList<>();
		for(Attivita activity: this.findAll()) {
			allActivityIdentifier.add(activity.getIdentificatore());
		}
		for(int i=1; true; i++) {
			if(!allActivityIdentifier.contains(i)) 
				return i;
		}
	}
	
	default Attivita foundSameActivity(String text) {
		for(Attivita activity: this.findAll()) {
			if(activity.hasSameText(text)) {
				return activity;
			}
		}
		return null;
	}
	
}