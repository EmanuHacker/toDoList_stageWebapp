package com.dirignani.webapp;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;


public interface UtentiRepository extends JpaRepository<Utenti, String> {
	Optional<Utenti> findByName(String name);
	
	default boolean exist(final String name) {
		for(Utenti user: findAll()) {
			if(user.getName().equals(name))
				return true;
		}
		return false;
	}
	
	@Transactional
	@Modifying
	@Query("DELETE FROM utenti WHERE name=?1")
	public void deleteByName(String name);
}