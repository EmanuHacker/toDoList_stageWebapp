package com.dirignani.webapp;

import org.springframework.stereotype.Service;


@Service
public class ActivityInfoService {
	private final AttivitaRepository repository; 
	
	public ActivityInfoService(AttivitaRepository repository) {
		this.repository = repository;
	}
	
	public void addActivity(Attivita activityInfo) { 
		repository.save(activityInfo);
	} 
	
	public void setActivity(final int id, final String text, final Attivita.Priorita priority, final String descrizione) {
		Attivita activity = repository.findById(id).get();
		if(!activity.getOwner().equals(CurrentUser.getActualUsername())) {
			return;
		}
		activity.setTesto(text);
		activity.setPriorita(priority);
		activity.setDescrizione(descrizione);
		repository.save(activity);
	}
	
	public void collectActivities(final String oldOwner, final String newOwner) {
		for(Attivita activity: repository.findAll()) {
			if(activity.getOwner().equals(oldOwner)) {
				activity.setOwner(newOwner);
				repository.save(activity);
			}
		}
	}
	
}
