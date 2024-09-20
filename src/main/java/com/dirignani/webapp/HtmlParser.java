package com.dirignani.webapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;

public class HtmlParser {
	
	private final String nomeFile;
	private Map<String, String> model;
	private static final String SPECIAL_CHARACTER_START = "${";
	private static final String SPECIAL_CHARACTER_END = "}$";

	
	public HtmlParser(String nomeFile, Map<String, String> model) {
		this.nomeFile = nomeFile;
		this.model = new HashMap<>(model);
	}
	
	public HtmlParser(String nomeFile) {
		this(nomeFile, new HashMap<>());
	}
	
	public HtmlParser() {
		this("");
	}
	
	public void addStringToMap(String key, String value) {
		model.put(key, value);
	}
	
	public String reloadHtml() {
		ArrayList<String> scomposed = htmlScomposition();
		ArrayList<String> corrected = updateArrayList(scomposed);
		String reloadedHtml = "";
		for(String x: corrected) {
			reloadedHtml += x + "\n";
		}
		return reloadedHtml;
	}
	
	private ArrayList<String> htmlScomposition(){
		ArrayList<String> htmlScomposed = new ArrayList<>();
		try(Scanner in = new Scanner(new File(nomeFile))){
			while(in.hasNextLine()) {
				htmlScomposed.add(in.nextLine());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return htmlScomposed;
	}
	
	private String stringCorrector(String s) {
		if(s.contains(SPECIAL_CHARACTER_END) && s.contains(SPECIAL_CHARACTER_START)) {
			for(String key: model.keySet()) {
				String target = SPECIAL_CHARACTER_START + key + SPECIAL_CHARACTER_END; //${name}$
				if(s.contains(target))
					s = s.replace(target, model.get(key));
			}
		}
		return s;
	}
	
	private ArrayList<String> updateArrayList(ArrayList<String> virginArrayList){
		ArrayList<String> correctArrayList = new ArrayList<String>();
		for(String x: virginArrayList) {
			String correct = stringCorrector(x);
			correctArrayList.add(correct);
		}
		return correctArrayList;
	}
	
	public static String convertInTableUsers(ArrayList<Utenti> users, IntersezioneRepository intersectionRepository) {
		Collections.sort(users, (x, b) -> x.getName().compareTo(b.getName()));
		String finalString = "";
		int rowNumber = 1;
		Utenti current = null;
		for(Utenti user: users) {
			if(user.getName().equals(CurrentUser.getActualUsername())) {
				current = user;
				break;
			}
		}
		for(Utenti user: users) {
			final String styleRow;
			if(user.getRole() == Utenti.Roles.OWNER) {
				styleRow = " style=\"text-decoration: underline; background-color: gray;\"";
			}
			else if(user.getRole() == Utenti.Roles.ADMIN) {
				styleRow = " style=\"text-decoration: underline;\"";
			}
			else {
				styleRow = "";
			}
			finalString += "<tr"+styleRow+">\n";
			finalString += "\t<th>"+user.getName()+"</th>\n";
			finalString += "\t<th>"+user.getMail()+"</th>\n";
			finalString += """
						<th>
							<span id="show_role**"><></span>
							<form action="/auth/changeUserRole" method="POST" id="change_user_role**" style="display: none;">
								<input type="hidden" name="name" value="{}" />
								<select name="role">
									<option value="[]"^^>[]</option>
									<option value="##"++>##</option>
								</select>
								<button type="submit" class="btn btn-outline-info btn-sm">Conferma</button>
							</form>
						</th>
						""".replace("<>", String.valueOf(user.getRole())).replace("**", String.valueOf(rowNumber)).replace("{}", user.getName())
						.replace("[]", String.valueOf(Utenti.Roles.USER)).replace("^^", ( user.getRole()==Utenti.Roles.USER ? " selected " : "" ))
						.replace("##", String.valueOf(Utenti.Roles.ADMIN)).replace("++", ( user.getRole()==Utenti.Roles.ADMIN ? " selected " : "" ));
			finalString += "\t<th>" + ( String.valueOf(intersectionRepository.getThingsToDoFromUser(user.getName()).size()) + " attività" ) + "</th>\n";
			finalString += current.isSuperior(user) ? """
					<th>
						<button type="button" onclick=\"modifyUser('**', °°)\" class="btn btn-outline-info" id="modify_user**" style="width: 100%;">Modifica</button>
						<form action="/auth/expelUser" method="POST" id="expel_user**" style="display: none;">
							<input type="hidden" name="name" value="{}" />
							<button type="submit" class="btn btn-outline-danger" style="width: 100%;">Espelli</button>
							<button type="button" class="btn btn-outline-secondary" onclick="window.location.reload()" style="width: 100%;">Annulla</button>
						</form>
					</th>
					""".replace("**", String.valueOf(rowNumber)).replace("{}", user.getName()).replace("°°", String.valueOf(current.getRole() == Utenti.Roles.OWNER))
					: "<th> </th>";
			finalString += "</tr>\n";
			++rowNumber;
		}
		return finalString;
	}
	
	public static String convertInTable(ArrayList<Attivita> activity, boolean isOwnerTable, IntersezioneRepository intersectionRepository, boolean isInAdminPage) {
		Collections.sort(activity);
		String finalString = "";
		int rowNumber = 1;
		for(Attivita a: activity) {
			ArrayList<Intersezione> listOfPeopleThatHaveIt = intersectionRepository!=null ? intersectionRepository.getAssignments(a.getIdentificatore()) : new ArrayList<Intersezione>();
			final int numberOfPeopleThatHaveIt = listOfPeopleThatHaveIt.size();
			finalString += "<tr>\n";
			finalString += "\t<th>"+a.getTesto()+"</th>\n";
			finalString += "\t<th>"+String.valueOf(a.getPriorita())+"</th>\n";
			finalString += "\t<th>" + (!isOwnerTable ? a.getOwner() : ( numberOfPeopleThatHaveIt>0 ? ("<a data-toggle=\"popover\" data-trigger=\"hover\" title=\"Assegnato a:\" data-content=\""+Intersezione.getAllNames(listOfPeopleThatHaveIt)+"\" style=\"text-decoration: underline;\">" + (String.valueOf(numberOfPeopleThatHaveIt) + (numberOfPeopleThatHaveIt==1 ? " persona" : " persone")) + "</a>") : "Nessuno" )) + "</th>\n";
			if(isInAdminPage) {
				finalString += "\t<th>"+a.getOwner()+"</th>\n";
			}
			if(!isOwnerTable) {
				final String nextStatus; //{ASSEGNATA, RIFIUTATA, ACCETTATA, IN_CORSO, CONCLUSA};
				{
					if(a.getStato() == Attivita.Stato.ASSEGNATA)
						nextStatus = "Accetta";
					else if(a.getStato() == Attivita.Stato.ACCETTATA)
						nextStatus = "Inizia";
					else if(a.getStato() == Attivita.Stato.IN_CORSO)
						nextStatus = "Completata";
					else
						nextStatus = "";
				}
				
				final String refuseActivity;
				if(a.getStato() == Attivita.Stato.ASSEGNATA) {
					refuseActivity = """
									<form method="post" action="/auth/refuseActivity">
										<input type="hidden" name="identificatore" value=[] />
										<button type="submit" class="btn btn-outline-danger">Rifiuta</button>
									</form>
									""";
				}
				else {
					refuseActivity = "";
				}
				
				finalString += """
						<th>
							<form method="post" action="/auth/nextActivityStatus">
								<input type="hidden" name="identificatore" value=[] />
								<button type="submit" class="btn btn-outline-success">{}</button>
							</form>
							$$
						</th>
						""".replace("$$", refuseActivity).replace("[]", String.valueOf(a.getIdentificatore())).replace("{}", nextStatus);
			} else {
				if(!isInAdminPage) {
					finalString += """
							<th>
								<button type="button" onclick="ownerRemoveActivity('**')" class="btn btn-outline-danger" id="pre_elimination_button**">Elimina</button>
								<form action="/auth/removeOtherIntersection"  method="POST" id="elimination_form**" style="display: none;">
									<input type="hidden" name="identificatore" value={} />
									Per 
									<select name="name">
										<option value=":tutti:" selected>Tutti</option>
										@@
									</select>
									<button type="submit" class="btn btn-outline-danger" onclick="window.location.reload()">Elimina</button>
								</form>
							</th>
							<th>
								<a href="#set_activity_repository" style="text-decoration: none;" id="modification_button**"><button type="button" class="btn btn-outline-secondary" onclick="startSettingActivity({}, '[]', '<>', '||')">Modifica</button></a>
								<button type="button" class="btn btn-outline-secondary" onclick="window.location.reload()" id="reload_for_elimination**" style="display: none;">Annulla</button>
							</th>
							""".replace("@@", convertInSelect(intersectionRepository.getUsersThatHaveIt(a.getIdentificatore())).replace("selected", "")).replace("{}", String.valueOf(a.getIdentificatore()))
								.replace("**", String.valueOf(rowNumber)).replace("[]", String.valueOf(a.getTesto())).replace("<>", String.valueOf(a.getPriorita())).replace("||", a.getDescrizione());
				}
					finalString += """
						<th>
							<a href="#add_activity_form" style="text-decoration: none;" id="assegnation_button**"><button type="button" class="btn btn-outline-info" onclick="assignOldActivity('[]', '<>', '||')">Assegna</button></a>
						</th>
						""".replace("[]", String.valueOf(a.getTesto())).replace("<>", String.valueOf(a.getPriorita())).replace("**", String.valueOf(rowNumber))
						.replace("||", a.getDescrizione());
				++rowNumber;
			}
			finalString += """
								<th>
									<form method="POST" action="/authenticated/activityInfo">
										<input type="hidden" name="identificatore" value=[] />
										<button type="submit" class="btn btn-outline-secondary"><img src="/info.png" /></button>
									</form>
								</th>
							""".replace("[]", String.valueOf(a.getIdentificatore()));
			finalString += "</tr>\n";
		}
		return finalString;
	}
	
	public static String convertInPointedList(ArrayList<Messaggio> messages) {
		String finalString = "";
		int rowNumber = 1;
		for(Messaggio message: messages) {
			finalString += """
					<li>
						<form action="/auth/signLikeRead" method="POST" id="readMessage_form**">
							<input type="hidden" name="riga" value=°° />
							<a href="javascript:{}" onclick="document.getElementById('readMessage_form**').submit();"++>[]</a>
						</form>
					</li>
					""".replace("°°", String.valueOf(message.getRiga())).replace("[]", message.getTesto()).replace("**", String.valueOf(rowNumber))
						.replace("++", (message.isLetto() ? " style=\"color: #8FC4B7;\"" : "" ) );
			++rowNumber;
		}
		return finalString;
	}
	
	public static String convertInTable(ArrayList<Attivita> activity, boolean isOwnerTable, IntersezioneRepository intersectionRepository) {
		return convertInTable(activity, isOwnerTable, intersectionRepository, false);
	}
	
	public static String convertInTable(ArrayList<Attivita> activity) {
		return convertInTable(activity, false, null);
	}
	
	public static String convertInSelect(ArrayList<String> users) {
		//Collections.sort(users, (a, b) -> a.getName().compareTo(b.getName()));
		Collections.sort(users);
		String finalString = "";
		for(String user: users) {
			finalString += ("<option value=\"{}\"" + (user.equals(CurrentUser.getActualUsername()) ? " selected" : "") + ">{}</option>\n").replace("{}", user);
		}
		return finalString;
	}
	
}
