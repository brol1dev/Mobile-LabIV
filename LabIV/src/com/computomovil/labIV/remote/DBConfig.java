package com.computomovil.labIV.remote;

import java.util.HashMap;
import java.util.Set;

public class DBConfig {

	public static final String URL = "http://172.16.214.179:8080";
	public static final String GET_POST_LOCATIONS = "/locations";
	
	private static HashMap<String, String> headers = new 
			HashMap<String, String>();
	
	static {
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
	}
	
	public static Set<String> getHeaderKeys() {
		return headers.keySet();
	}
	
	public static String getHeaderValue(String key) {
		return headers.get(key);
	}
}
