package com.dirignani.webapp;

import java.util.ArrayList;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface IntersezioneRepository extends JpaRepository<Intersezione, Integer> {
	
	//*
	@Transactional
	@Modifying
	@Query("DELETE FROM intersezione WHERE name=?1 AND identificatore=?2")
	public void deleteByNameAndIdentificatore(String name, int identificatore);
	/**/
	
	@Transactional
	@Modifying
	@Query("DELETE FROM intersezione WHERE identificatore=?1")
	public void deleteByIdentificatore(int identificatore);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM intersezione WHERE name=?1")
	public void deleteByName(String name);
	
	
	default int getValidRiga() {
		ArrayList<Integer> allIntersectionRows = new ArrayList<>();
		for(Intersezione intersection: this.findAll()) {
			allIntersectionRows.add(intersection.getRiga());
		}
		for(int i=1; true; i++) {
			if(!allIntersectionRows.contains(i)) 
				return i;
		}
	}
	
	default ArrayList<Intersezione> getAssignments(final int activityIdentifier) {
		ArrayList<Intersezione> respectiveIntersections = new ArrayList<>();
		for(Intersezione intersection: this.findAll()) {
			if(activityIdentifier == intersection.getIdentificatore())
				respectiveIntersections.add(intersection);
		}
		return respectiveIntersections;
	}
	
	default ArrayList<String> getUsersThatHaveIt(final int activityIdentifier){
		ArrayList<String> users = new ArrayList<>();
		for(Intersezione intersection: getAssignments(activityIdentifier)) {
			users.add(intersection.getName());
		}
		return users;
	}
	
	default ArrayList<Intersezione> getThingsToDoFromUser(final String name){
		ArrayList<Intersezione> intersectiones = new ArrayList<>();
		for(Intersezione intersection: findAll()) {
			if(name.equals(intersection.getName())) {
				intersectiones.add(intersection);
			}
		}
		return intersectiones;
	}
	
}