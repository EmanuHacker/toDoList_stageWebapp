package com.dirignani.webapp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AttivitaService {

	@Autowired
    private AttivitaRepository repo;
	
    public List<Attivita> getAllAttivita(){
        return repo.findAll();
    }
    
    public ArrayList<Attivita> getAttivitaFromSpecificOwner(String owner){
    	ArrayList<Attivita> activity = new ArrayList<>();
    	for(Attivita a: getAllAttivita()) {
    		if(a.getOwner().equals(owner))
    			activity.add(a);
    	}
    	return activity;
    }
	
}
