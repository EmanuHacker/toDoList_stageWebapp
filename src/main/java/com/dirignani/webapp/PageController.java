package com.dirignani.webapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.ws.rs.FormParam;

@RestController
public class PageController {
	
	@Autowired
    private UtentiService usersService;
	@Autowired
    private AttivitaService activityService;
	@Autowired
    private IntersezioneService intersectionService;
	@Autowired
	private IntersezioneRepository intersectionRepository;
	@Autowired
	private UtentiRepository usersRepository;
	@Autowired
	private MessaggioRepository messageRepository;
	@Autowired
	private AttivitaRepository activityRepository;
	@Autowired
	private CorrispondenzaRepository postalRepository;
	

	//*
	@RequestMapping("/")
    public RedirectView redirectToLogin() {
		if(!CurrentUser.isLogged())
			return new RedirectView("/registration.html");
		return new RedirectView("/authenticated/");
    }/**/
	
	/*
	@RequestMapping("/authenticated")
	public String welcome() {
		return "/authenticated/before.html";
	}
	/**/
	
	/*
	@RequestMapping("/authenticated")
    public String redirectToAuthenticatedPage() {
	    return "authenticated/principal_page.html";
    }/**/
	
	//*
	@GetMapping("/auth")
    public RedirectView dashboard() {
    	return new RedirectView("/authenticated/");
    	//return null;
    }
	/**/
	
	//*
    @RequestMapping("/authenticated/")
	public String principalPage() {
    	if(CurrentUser.isLogged()) {
    		Utenti current = usersRepository.findByName(CurrentUser.getActualUsername()).get();
			HtmlParser pars = new HtmlParser("./src/main/resources/static/authenticated/principal_page.html"); //TODO: sistemare questo percorso
			pars.addStringToMap("name", CurrentUser.getActualUsername());
			pars.addStringToMap("activity", HtmlParser.convertInTable(getConnections().get(CurrentUser.getActualUsername())));
			pars.addStringToMap("my_activity", HtmlParser.convertInTable(activityService.getAttivitaFromSpecificOwner(CurrentUser.getActualUsername()), true, intersectionRepository)); 
			ArrayList<String> allUsersList = new ArrayList<>();
			for(Utenti user: usersService.getAllUsers()) {
				allUsersList.add(user.getName());
			}
			pars.addStringToMap("all_users", HtmlParser.convertInSelect(allUsersList));
			pars.addStringToMap("assign_button_style", current.getRole()==Utenti.Roles.USER ? " style=\"display: none\"" : "");
			pars.addStringToMap("no_priority", String.valueOf(Attivita.Priorita.NESSUNA));
			pars.addStringToMap("low_priority", String.valueOf(Attivita.Priorita.BASSA));
			pars.addStringToMap("medium_priority", String.valueOf(Attivita.Priorita.MEDIA));
			pars.addStringToMap("high_priority", String.valueOf(Attivita.Priorita.ALTA));
			pars.addStringToMap("max_priority", String.valueOf(Attivita.Priorita.URGENTE));
			return pars.reloadHtml();
    	} else {
    		return """
    				<script>
    					alert("Prima devi fare il login");
    					window.location.replace("/");
    				</script>
    				""";
    	}
	}
    /**/
    
    @RequestMapping("/authenticated/settings")
    public String settings(){
    	if(!CurrentUser.isLogged()) {
    		return """
    				<script>
    					alert("Prima devi fare il login");
    					window.location.replace("/");
    				</script>
    				""";
    	}
    	Utenti current = usersRepository.findByName(CurrentUser.getActualUsername()).get();
    	if(!(current.getRole()==Utenti.Roles.ADMIN || current.getRole()==Utenti.Roles.OWNER)) {
    		return """
    				<script>
    					alert("Devi essere un admin o il proprietario per accedere qui");
    					window.location.replace("/");
    				</script>
    				""";
    	}
    	HtmlParser pars = new HtmlParser("./src/main/resources/static/authenticated/settings_page.html");
    	
    	pars.addStringToMap("name", current.getName());
    	pars.addStringToMap("role", String.valueOf(current.getRole()));
    	pars.addStringToMap("users", HtmlParser.convertInTableUsers(new ArrayList<Utenti>(getDataUtenti()), intersectionRepository));
    	pars.addStringToMap("change_owner", current.getRole()==Utenti.Roles.OWNER ? "" : "style=\"display: none\""); 
    	pars.addStringToMap("owner_default_string", String.valueOf(Utenti.Roles.OWNER));
    	pars.addStringToMap("show_navbar", "");
    	
    	ArrayList<String> allUsersList = new ArrayList<>();
		for(Utenti user: usersService.getAllUsers()) {
			allUsersList.add(user.getName());
		}
    	pars.addStringToMap("all_users", HtmlParser.convertInSelect(allUsersList));
    	
    	ArrayList<Attivita> allActivitiesList = new ArrayList<Attivita>(getDataAttivita());
    	Collections.sort(allActivitiesList, (a, b) -> a.getTesto().compareTo(b.getTesto()));
    	pars.addStringToMap("activities", HtmlParser.convertInTable(allActivitiesList, true, intersectionRepository, true));
    	
    	ArrayList<Messaggio> messagesList = new ArrayList<>();
    	for(Messaggio message: messageRepository.findAll()) {
    		messagesList.add(message);
    	}
    	Collections.sort(messagesList, (a, b) -> -1 * Long.valueOf(a.getTime()).compareTo(b.getTime()) );
    	pars.addStringToMap("all_messages", HtmlParser.convertInPointedList(messagesList));
    	
    	return pars.reloadHtml();
    }
    
    @RequestMapping("/authenticated/profile")
    public String profile() {
    	if(!CurrentUser.isLogged()) {
    		return "<script>window.location.replace(\"/\");</script>";
    	}
    	HtmlParser pars = new HtmlParser("./src/main/resources/static/authenticated/profile_page.html");
    	Utenti currentUser = usersRepository.findByName(CurrentUser.getActualUsername()).get();
    	pars.addStringToMap("name", currentUser.getName());
    	pars.addStringToMap("mail", currentUser.getMail());
    	return pars.reloadHtml();
    }
    
    @PostMapping("/authenticated/activityInfo")
    public String activityInfo(@FormParam("identificatore") int identificatore) {
    	if(!CurrentUser.isLogged()) {
    		return """
    				<script>
    					alert("Prima devi fare il login");
    					window.location.replace("/");
    				</script>
    				""";
    	}
    	Attivita activity = activityRepository.findById(identificatore).get();
    	Utenti currentUser = usersRepository.findByName(CurrentUser.getActualUsername()).get();
    	HtmlParser pars = new HtmlParser("./src/main/resources/static/authenticated/activityInfo_page.html");
    	
    	pars.addStringToMap("activity_text", activity.getTesto());
    	pars.addStringToMap("activity_description", activity.getDescrizione());
    	pars.addStringToMap("activity_owner", activity.getOwner());
    	pars.addStringToMap("activity_priority", String.valueOf(activity.getPriorita()));
    	pars.addStringToMap("activity_state", String.valueOf(activity.getStato()));
    	
    	ArrayList<Intersezione> listOfPeopleThatHaveIt = intersectionRepository.getAssignments(activity.getIdentificatore());
    	final int numberOfPeopleThatHaveIt = listOfPeopleThatHaveIt.size();
    	String activityWorkers = "<a data-toggle=\"popover\" data-trigger=\"hover\" title=\"Assegnato a:\" data-content=\""+Intersezione.getAllNames(listOfPeopleThatHaveIt)+"\" style=\"text-decoration: underline;\">" + (numberOfPeopleThatHaveIt>0 ? (String.valueOf(numberOfPeopleThatHaveIt) + (numberOfPeopleThatHaveIt==1 ? " persona" : " persone") + "</a>") : "Nessuno"); 
    	pars.addStringToMap("activity_workers", activityWorkers);
    	
    	pars.addStringToMap("activity_id", String.valueOf(activity.getIdentificatore()));
    	pars.addStringToMap("sender_visible", (!intersectionRepository.getUsersThatHaveIt(activity.getIdentificatore()).contains(currentUser.getName())) && (!activity.getOwner().equals(currentUser.getName())) ? " style=\"display: none;\"" : "");
    	
    	String messagesString = "";
    	for(Corrispondenza mail: postalRepository.getAllActivityMessages(identificatore)) {
    		messagesString += "<div class=\"message_div\""+ ( mail.getMittente().equals(currentUser.getName()) ? " style=\"text-align: right;\"" : "" ) +"><b>"+mail.getMittente()+":</b> "+mail.getTesto()+"</div><hr>\n";
    	}
    	pars.addStringToMap("activity_messages", messagesString);
    	
    	return pars.reloadHtml();
    }
	
   //@RequestMapping("/utenti")
    public List<Utenti> getDataUtenti(){
    	List<Utenti> listaUtenti = usersService.getAllUsers();
        return listaUtenti;
    }
    
    //@RequestMapping("/attivita")
    public List<Attivita> getDataAttivita(){
    	List<Attivita> listaAttivita = activityService.getAllAttivita();
        return listaAttivita;
    }
    
    //@RequestMapping("/intersezione")
    public List<Intersezione> getDataIntersection(){
    	List<Intersezione> listaIntersezione = intersectionService.getAllIntersezione();
        return listaIntersezione;
    }
    
    //NB: non sicuro se resta la funzione di RequestMapping
    //@RequestMapping("/connections")
    public Map<String, ArrayList<Attivita>> getConnections(){
    	Map<String, ArrayList<Attivita>> map = new HashMap<String, ArrayList<Attivita>>();
    	List<Utenti> allUsers = getDataUtenti();
    	List<Attivita> allActivity = getDataAttivita();
    	List<Intersezione> allIntersections = getDataIntersection();
    	for(Utenti user: allUsers) {
    		String username = user.getName();
    		ArrayList<Attivita> connectedActivity = new ArrayList<>();
    		for(Intersezione intersection: allIntersections) {
    			if(user.isThis(intersection.getName())) {
    				for(Attivita activity: allActivity) {
    					if(activity.isThis(intersection.getIdentificatore())) {
    						connectedActivity.add(activity);
    					}
    				}
    			}
    		}
    		map.put(username, connectedActivity);
    	}
    	return map;
    }
    
}
