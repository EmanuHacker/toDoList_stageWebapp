package com.dirignani.webapp;

import java.util.ArrayList;
import java.util.Collections;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;

public interface CorrispondenzaRepository extends JpaRepository<Corrispondenza, Integer>{
	
	@Transactional
	@Modifying
	@Query("DELETE FROM corrispondenza WHERE attivita=?1")
	public void deleteByAttivita(int identificatore);

	default int getValidRiga() {
		ArrayList<Integer> allCorrespondenceRows = new ArrayList<>();
		for(Corrispondenza correspondence: this.findAll()) {
			allCorrespondenceRows.add(correspondence.getRiga());
		}
		for(int i=1; true; i++) {
			if(!allCorrespondenceRows.contains(i)) 
				return i;
		}
	}
	
	default ArrayList<Corrispondenza> getAllActivityMessages(final int attivita){
		ArrayList<Corrispondenza> allMessagesOfThatActivity = new ArrayList<>();
		for(Corrispondenza mail: this.findAll()) {
			if(mail.getAttivita() == attivita) {
				allMessagesOfThatActivity.add(mail);
			}
		}
		Collections.sort(allMessagesOfThatActivity);
		return allMessagesOfThatActivity;
	}
	
}
