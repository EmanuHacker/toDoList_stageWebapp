package com.dirignani.webapp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class IntersezioneService {

	@Autowired
    private IntersezioneRepository repo;
	
    public List<Intersezione> getAllIntersezione(){
        return repo.findAll();
    }
	
}
