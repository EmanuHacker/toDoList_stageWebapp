package com.dirignani.webapp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtentiService{

	@Autowired
    private UtentiRepository repo;
	
    public List<Utenti> getAllUsers(){
        return repo.findAll();
    }
    
}
