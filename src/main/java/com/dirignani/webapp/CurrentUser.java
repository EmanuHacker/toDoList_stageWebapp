package com.dirignani.webapp;

import java.util.Base64;

import org.springframework.security.core.context.SecurityContextHolder;


public class CurrentUser {
	
	private static String currentToken = null; //Questo è un BEARER TOKEN
	
	public static void isNowValid() {
		JwtService jwt = new JwtService();
		if(currentToken != null && jwt.isTokenExpired(currentToken)) {
			invalidateToken();
		}
	}
	
	public static void extractUser(String header) {
		isNowValid();
		header = header.replace("Basic ", "");
		if(currentToken != null) {
			return;
		}
		byte[] base64DecodedBytes = Base64.getDecoder().decode(header);
		String[] decodedHeader = new String(base64DecodedBytes).split(":");
		final String name = decodedHeader[0];
		final String password = decodedHeader[1];
		String maybeToken = HttpRequests.getToken(name, password);
		if(!maybeToken.contains("Unauthorized"))
			currentToken = maybeToken;
	}
	
	public static String generateBasicToken(final String name, final String pswd) {
		if(name.contains(":") || pswd.contains(":")) {
			return "";
		}
		String token = name+":"+pswd;
		token = Base64.getEncoder().withoutPadding().encodeToString(token.getBytes());
		return token;
	}
	
	public static String getActualToken() { 
		isNowValid();
		return currentToken; 
	}
	
	public static String getActualUsername() {
		isNowValid();
		if(currentToken == null)
			return "";
		JwtService jwt = new JwtService();
		return jwt.extractUsername(currentToken);
	}
	
	public static boolean isLogged() {
		isNowValid();
		return currentToken != null;
	}
	
	public static void invalidateToken() {
		currentToken = null;
	}
	
}
