package com.dirignani.webapp;

import org.springframework.stereotype.Service;

@Service
public class IntersectionInfoService {
	private final IntersezioneRepository repository; 
	
	public IntersectionInfoService(IntersezioneRepository repository) {
		this.repository = repository;
	}
	
	public void addIntersection(Intersezione intersectionInfo) { 
		repository.save(intersectionInfo);
		return;
	} 
	
}
