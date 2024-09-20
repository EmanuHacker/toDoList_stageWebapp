package com.dirignani.webapp;

import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;
import java.util.Optional; 

@Service
public class UserInfoService implements UserDetailsService { 
	private final UtentiRepository repository; 
	private final PasswordEncoder encoder;
	
	public UserInfoService(UtentiRepository repository, PasswordEncoder encoder) {
		this.repository = repository;
		this.encoder = encoder;
	}
	
	//Login gestito qui
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 
		Optional<Utenti> userDetail = repository.findByName(username); 
		// Converting userDetail to UserDetails 
		UserDetails data = userDetail.map(UserInfoDetails::new).orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
		return data; 
	} 
	
	public void addUser(Utenti userInfo) { 
		final String encPassword = encoder.encode(userInfo.getPswd());
		userInfo.setPswd(encPassword); 
		repository.save(userInfo); 
	} 
	
	public boolean changePassword(Utenti userInfo, String oldPassword, String newPassword1, String newPassword2) {
		if(encoder.matches(oldPassword, userInfo.getPswd()) && newPassword1.equals(newPassword2)) {
			final String encPassword = encoder.encode(newPassword1);
			userInfo.setPswd(encPassword); 
			repository.save(userInfo);
			return true;
		}
		return false;
	}
	
	public void setUserRole(final String name, final String newRole) {
		if(!CurrentUser.isLogged()){
			return;
		}
		Utenti current = repository.findByName(CurrentUser.getActualUsername()).get();
		if(current.getRole() != Utenti.Roles.OWNER) {
			return;
		}
		Utenti candidate = repository.findByName(name).get();
		if(newRole.equals(String.valueOf(Utenti.Roles.OWNER))) {
			candidate.setRole(Utenti.Roles.OWNER);
			current.setRole(Utenti.Roles.ADMIN);
			repository.save(current);
		}
		else if(newRole.equals(String.valueOf(Utenti.Roles.ADMIN))) {
			candidate.setRole(Utenti.Roles.ADMIN);
		}
		else if(newRole.equals(String.valueOf(Utenti.Roles.USER))) {
			candidate.setRole(Utenti.Roles.USER);
		}
		repository.save(candidate);
	}

}
