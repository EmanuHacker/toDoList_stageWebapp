package com.dirignani.webapp;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;

public class HttpRequests {
	
	public static String getToken(String name, String pswd){
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("http://localhost:"+WebappApplication.port+"/auth/generateToken");

		try {
			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("pswd", pswd));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
	
			//Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
	
			if (entity != null) {
			    try (InputStream instream = entity.getContent()) {
			    	String result = IOUtils.toString(instream, StandardCharsets.UTF_8);
 			        return result;
			    }
			}
		}catch(Exception e) { return e.getMessage(); }
		return "";
	}
	
	public static String getActivityInfo(final int id) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("http://localhost:"+WebappApplication.port+"/authenticated/activityInfo");

		try {
			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("identificatore", String.valueOf(id)));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
	
			//Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
	
			if (entity != null) {
			    try (InputStream instream = entity.getContent()) {
			    	String result = IOUtils.toString(instream, StandardCharsets.UTF_8);
 			        return result;
			    }
			}
		}catch(Exception e) { return e.getMessage(); }
		return "";
	}
	
	
}
