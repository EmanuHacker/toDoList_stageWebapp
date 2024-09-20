package com.dirignani.webapp;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;

public interface MessaggioRepository extends JpaRepository<Messaggio, Integer>{
	
	Optional<Messaggio> findByRiga(int riga);
	
	/*
	@Transactional
	@Modifying
	@Query("DELETE FROM messaggio WHERE letto=?1")
	public void deleteByLetto(boolean letto);
	/**/
	
	@Transactional
	@Modifying
	@Query("DELETE FROM messaggio WHERE riga=?1 and letto=TRUE")
	public void deleteByRiga(int riga);
	
	default int getValidRiga() {
		ArrayList<Integer> allMessageIdentifier = new ArrayList<>();
		for(Messaggio message: this.findAll()) {
			allMessageIdentifier.add(message.getRiga());
		}
		for(int i=1; true; i++) {
			if(!allMessageIdentifier.contains(i)) 
				return i;
		}
	}
	
}
