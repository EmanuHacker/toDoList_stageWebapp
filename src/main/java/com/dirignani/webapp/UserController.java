package com.dirignani.webapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.FormParam;
  
@RestController
@RequestMapping("/auth") 
public class UserController { 
	
	@Autowired
    private UserInfoService userService; 
	@Autowired
    private ActivityInfoService activityService;
	@Autowired
    private IntersectionInfoService intersectionService;
	@Autowired
    private JwtService jwtService;
	@Autowired
    private AuthenticationManager authenticationManager; 
    @Autowired
    private IntersezioneRepository intersectionRepository;
    @Autowired
    private AttivitaRepository activityRepository;
    @Autowired
    private UtentiRepository usersRepository;
    @Autowired
	private MessaggioRepository messageRepository;
    @Autowired
    private CorrispondenzaRepository postalRepository;
    
    private static String eh_volevi = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQnxy0t6KqWimh3Khp46JyARktflPkiXASQQg&s";
    
    
    @PostMapping("/register") 
    public String addNewUser(@FormParam("mail") String mail,  @FormParam("name") String name, @FormParam("pswd") String pswd, @FormParam("pswdConfirm") String pswdConfirm) { 
    	String response = "";
    	name = name.trim();
    	if(name.contains(":") || pswd.contains(":") || mail.contains(":")) {
    		response = "Errore, non puoi usare il carattere \":\"";
    	}
    	else if(!pswd.equals(pswdConfirm)) {
    		response = "Errore, le password non coincidono";
    	}
    	else if(usersRepository.exist(name)) {
    		response = ("Errore, esiste già un utente con quel nome");
    	}
    	else if(mail.length() == 0 || name.length() == 0 || pswd.length() == 0) {
    		response = "Errore, alcuni campi sono vuoti";
    	}
    	else {
	    	Utenti userInfo = new Utenti(mail, name, pswd);
	        userService.addUser(userInfo);
	        response = "Utente aggiunto con successo";
	        addMessage(name+" si è registrato");
    	}
        return """
        		<script>
        			alert("{}");
        			window.location.replace("/");
        		</script>
        		""".replace("{}", response); 
    }
    
    @PostMapping("/changePassword")
    public String changePassword(@FormParam("oldPswd") String oldPswd, @FormParam("newPswd1") String newPswd1, @FormParam("newPswd2") String newPswd2) {
    	if(!CurrentUser.isLogged()) {
    		return "<script>window.location.replace(\"/\")</script>";
    	}
    	Utenti actualUser = usersRepository.findByName(CurrentUser.getActualUsername()).get();
    	boolean success = userService.changePassword(actualUser, oldPswd, newPswd1, newPswd2);
    	if(success && newPswd1.length() > 0) {
    		return """
    				<script>
	        			alert("Password cambiata con successo");
	        			window.location.replace("/auth/logout");
	        		</script>
    				""";
    	}
    	else {
    		return """
    				<script>
	        			alert("{}");
	        			window.location.replace("/");
	        		</script>
    				""".replace("{}", ( newPswd1.equals(newPswd2) ? "La vecchia password non è corretta" : ( newPswd1.length()>0 ? "Le due password non coincidono" : "Il campo relativo alla nuova password non può essere vuoto" )));
    	}
    }
    
    @RequestMapping("/logout")
    public RedirectView logout() {
    	CurrentUser.invalidateToken();
    	return new RedirectView("/");
    }
    
    @PostMapping("/addActivity")
    public RedirectView addNewActivity(@FormParam("testo") String testo, @FormParam("priorita") String priorita, @FormParam("name") String name, @FormParam("descrizione") String descrizione) {
    	if(CurrentUser.isLogged()) {
	    	if(name.equals(":tutti:")) {
	    		for(Utenti user: usersRepository.findAll()) {
	    			hiddenAddNewActivity(testo, priorita, user.getName(), descrizione);
	    		}
	    		addMessage("Una nuova attività è stata assegnata a tutti");
	    	}else {
	    		hiddenAddNewActivity(testo, priorita, name, descrizione);
	        	addMessage("Una nuova attività è stata assegnata a "+name);
	    	}
    	}
    	return new RedirectView("/");
    }
    
    @PostMapping("/addActivityForAdmin")
    public RedirectView addNewActivityForAdmin(@FormParam("testo") String testo, @FormParam("priorita") String priorita, @FormParam("name") String name, @FormParam("descrizione") String descrizione) {
    	addNewActivity(testo, priorita, name, descrizione);
    	return new RedirectView("/authenticated/settings");
    }
    
    private void hiddenAddNewActivity(final String testo, final String priorita, final String name, final String descrizione) {
    	int validActivityIdentifier = activityRepository.getValidIdentificatore();
    	Attivita foundActivity = activityRepository.foundSameActivity(testo); // , Attivita.Priorita.valueOf(priorita)
    	Utenti current = usersRepository.findByName(CurrentUser.getActualUsername()).get();
    	boolean alreadyExist = (foundActivity != null);
    	Attivita activityInfo = !alreadyExist ? new Attivita(validActivityIdentifier, testo, Attivita.Priorita.valueOf(priorita), current.getName(), descrizione) : foundActivity;
    	if(current.getRole()==Utenti.Roles.ADMIN || current.getRole()==Utenti.Roles.OWNER || activityInfo.getOwner().equals(current.getName()) || ( (!name.equals(current.getName()) && (intersectionRepository.getUsersThatHaveIt(activityInfo.getIdentificatore()).contains(current.getName())) ) )){
    		addNewIntersection(name, activityInfo.getIdentificatore());
    		if(!alreadyExist) {
        		activityService.addActivity(activityInfo);
        		addMessage(activityInfo.getOwner()+" ha creato una nuova attività");
        	}
    	} 
    }
    
    @PostMapping("/setActivity")
    public RedirectView setActivity(@FormParam("identificatore") int identificatore, @FormParam("testo") String testo, @FormParam("priorita") String priorita, @FormParam("descrizione") String descrizione) {
    	activityService.setActivity(identificatore, testo, Attivita.Priorita.valueOf(priorita), descrizione); //Già controllato che l'utente sia il proprietario nella funzione qui chiamata
    	return new RedirectView("/");
    }
    
    //*
    //@PostMapping("/addIntersection") //TODO: mettere disponibile solo agli autenticati (dopo il debug)
    private void addNewIntersection(@FormParam("name") String name, @FormParam("identificatore") int identificatore) {
    	for(Intersezione intersection: intersectionRepository.findAll()) {
    		if(name.equals(intersection.getName()) && identificatore==intersection.getIdentificatore()) {
    			return;
    		}
    	}
    	Intersezione intersectionInfo = new Intersezione(intersectionRepository.getValidRiga(), name, identificatore);
    	intersectionService.addIntersection(intersectionInfo);
    }
    /**/
    
    @PostMapping("/nextActivityStatus")
    public RedirectView nextActivityStatus(@FormParam("identificatore") int identificatore) {
    	if(!CurrentUser.isLogged()) {
    		return new RedirectView("/");
    	}
    	Attivita activity = activityRepository.findById(identificatore).get();
    	Utenti currentUser = usersRepository.findByName(CurrentUser.getActualUsername()).get();
    	if(!intersectionRepository.getUsersThatHaveIt(activity.getIdentificatore()).contains(currentUser.getName())) {
    		return new RedirectView(eh_volevi);
    	}
    	activity.nextStatus();
    	activityRepository.save(activity);
    	if(activity.getStato() == Attivita.Stato.CONCLUSA) {
    		return removeIntersection(identificatore);
    	}
    	else {
    		addMessage("L'attività "+activity.getTesto()+" è passata allo stato di " + ( activity.getStato()!=Attivita.Stato.IN_CORSO ? String.valueOf(activity.getStato()) : "IN CORSO" ));
    	}
    	return new RedirectView("/");
    }
    
    @PostMapping("/refuseActivity")
    public RedirectView refuseActivity(@FormParam("identificatore") int identificatore) {
    	if(!CurrentUser.isLogged()) {
    		return new RedirectView("/");
    	}
    	Attivita activity = activityRepository.findById(identificatore).get();
    	Utenti currentUser = usersRepository.findByName(CurrentUser.getActualUsername()).get();
    	if(!intersectionRepository.getUsersThatHaveIt(activity.getIdentificatore()).contains(currentUser.getName())) {
    		return new RedirectView(eh_volevi);
    	}
    	activity.refuse();
    	activityRepository.save(activity);
    	//removeOtherIntersection(activity.getIdentificatore(), currentUser.getName());
    	intersectionRepository.deleteByIdentificatore(identificatore);
    	addMessage("L' attività "+activity.getTesto()+" è stata rifiutata");
    	return new RedirectView("/");
    }
    
    //@PostMapping("/removeIntersection")
    public RedirectView removeIntersection(@FormParam("identificatore") int identificatore) {
    	if(CurrentUser.isLogged()) {
    		intersectionRepository.deleteByIdentificatore(identificatore);
    		Attivita completed = activityRepository.findById(identificatore).get();
    		addMessage("Attività \""+completed.getTesto()+"\" completata con successo");
    	}
    	return new RedirectView("/");
    }
    
    @PostMapping("/removeOtherIntersection")
    public RedirectView removeOtherIntersection(@FormParam("identificatore") int identificatore, @FormParam("name") String name) {
    	if(activityRepository.findById(identificatore).get().getOwner().equals(CurrentUser.getActualUsername())) {
    		if(name.equals(":tutti:"))
    			removeActivity(identificatore);
    		else
    			intersectionRepository.deleteByNameAndIdentificatore(name, identificatore);
    	}
    	return new RedirectView("/");
    }
    
    @PostMapping("/expelUser")
    public RedirectView expelUser(@FormParam("name") String name) {
    	if(!CurrentUser.isLogged()) {
    		return new RedirectView("/authenticated/settings");
    	}
    	Utenti expelled = usersRepository.findByName(name).get();
    	Utenti actual = usersRepository.findByName(CurrentUser.getActualUsername()).get();
    	if(!actual.isSuperior(expelled)) {
    		return new RedirectView(eh_volevi);
    	}
    	usersRepository.deleteByName(name);
    	activityService.collectActivities(expelled.getName(), actual.getName());
    	intersectionRepository.deleteByName(expelled.getName());
    	addMessage(actual.getName()+" ha espulso "+expelled.getName());
    	return new RedirectView("/authenticated/settings");
    }
    
    @PostMapping("/changeUserRole")
    public RedirectView changeUserRole(@FormParam("name") String name, @FormParam("role") String role) {
    	if(!CurrentUser.isLogged()) {
    		return new RedirectView("/authenticated/settings");
    	}
    	Utenti actual = usersRepository.findByName(CurrentUser.getActualUsername()).get();
    	if(actual.getName().equals(name) || actual.getRole() != Utenti.Roles.OWNER) {
    		return new RedirectView(eh_volevi);
    	}
    	userService.setUserRole(name, role);
    	addMessage(name+" è stato reso "+role);
    	return new RedirectView("/authenticated/settings");
    }
    
    //@PostMapping("/removeActivity") //TODO: mettere disponibile solo accertandosi che l'utente sia il proprietario (dopo il debug)
    public RedirectView removeActivity(@FormParam("identificatore") int identificatore) {
    	if(activityRepository.findById(identificatore).get().getOwner().equals(CurrentUser.getActualUsername())) {
	    	intersectionRepository.deleteByIdentificatore(identificatore);
	    	activityRepository.deleteByIdentificatore(identificatore);
	    	postalRepository.deleteByAttivita(identificatore);
    	}
    	return new RedirectView("/");
    }
    
    @PostMapping("/login")
    public RedirectView login(@RequestParam("name") String name, @RequestParam("pswd") String pswd, HttpServletRequest request, HttpServletResponse response) throws IOException{
    	final String header = "Basic "+CurrentUser.generateBasicToken(name, pswd);
    	CurrentUser.extractUser(header);
    	if(CurrentUser.isLogged()) {
    		RedirectView connection = new RedirectView("/auth/pre-authentication");
    		connection.addStaticAttribute("Authorization", header);
    		//messageRepository.deleteByLetto(true);
    		return connection;
    	}
    	else {
    		RedirectView invalidLoginConnection = new RedirectView("/");
    		return invalidLoginConnection;
    	}
    }
    
    @RequestMapping("/pre-authentication")
    public RedirectView preAuthentication() {
    	return new RedirectView("/authenticated/");
    }
  
    @PostMapping("/generateToken") 
    public ResponseEntity<String> authenticateAndGetToken(@FormParam("name") String name, @FormParam("pswd") String pswd) { 
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, pswd)); 
        if (authentication.isAuthenticated()) { 
            String token = jwtService.generateToken(name);
        	//String token = HttpRequests.getToken(name, pswd);
        	//System.out.println(token); //For debug purpose
            return ResponseEntity.ok(token); 
        } else { 
            throw new UsernameNotFoundException("Invalid user request!"); 
        } 
    } 
    
    @GetMapping("/getActualUsername")
    public String getDashboard() {
    	return CurrentUser.getActualUsername();
    }
    
    @PostMapping("/signLikeRead")
    public RedirectView signLikeRead(@FormParam("riga") int riga) {
    	if(!CurrentUser.isLogged()) {
    		return new RedirectView("/");
    	}
    	Utenti current = usersRepository.findByName(CurrentUser.getActualUsername()).get();
    	if(current.getRole()==Utenti.Roles.ADMIN || current.getRole()==Utenti.Roles.OWNER) {
    		Messaggio readedMessage = messageRepository.findByRiga(riga).get();
    		readedMessage.readed();
    		messageRepository.save(readedMessage);
    		if(current.getRole()==Utenti.Roles.OWNER) {
    			messageRepository.deleteByRiga(riga);
    		}
    	}
    	return new RedirectView("/authenticated/settings");
    }
    
    @PostMapping("/newActivityMessage")
    public String newActivityMessage(@FormParam("messaggio") String messaggio, @FormParam("attivita") int attivita) {
    	if(!CurrentUser.isLogged()) {
    		return "<script>window.location.replace(\"/\")</script>";
    	}
    	Utenti currentUser = usersRepository.findByName(CurrentUser.getActualUsername()).get();
    	Attivita currentActivity = activityRepository.findById(attivita).get();
    	if((!intersectionRepository.getUsersThatHaveIt(currentActivity.getIdentificatore()).contains(currentUser.getName())) && (!currentActivity.getOwner().equals(currentUser.getName()))) {
    		return "<script>window.location.replace(\""+eh_volevi+"\")</script>";
    	}
    	if(messaggio.length() >= 1) {
	    	Corrispondenza mail = new Corrispondenza(postalRepository.getValidRiga(), currentActivity.getIdentificatore(), currentUser.getName(), messaggio);
	    	postalRepository.save(mail);
    	}
    	return HttpRequests.getActivityInfo(attivita);
    }
    
    private void addMessage(final String text) {
    	final int validRigaValue = messageRepository.getValidRiga();
    	Messaggio message = new Messaggio(text, validRigaValue);
    	messageRepository.save(message);
    }
    
    /*
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
    
    @GetMapping("/hi") //(value = "/hi", headers = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXJpbyIsImlhdCI6MTcyNTYzNjY1OCwiZXhwIjoxNzI1NjM4NDU4fQ.2_n-_-3pTWblLF9yyu8VptRUqOz-UiJ83fEQjLRfb2U")
    public String hi() {
        return "Hi World!";
    }
    /**/
  
}