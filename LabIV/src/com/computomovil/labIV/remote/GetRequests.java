package com.computomovil.labIV.remote;

import java.util.List;

import org.json.JSONObject;

import com.androidnatic.maps.model.HeatPoint;
import com.computomovil.labIV.JsonUtils;
import com.computomovil.labIV.bean.TimePoint;
import com.computomovil.labIV.bean.User;

public class GetRequests {

	public static List<User> getUsers() {
		String url = DBConfig.URL + DBConfig.GET_USERS;
		List<User> users;
		RestClient client = new RestClient(url);
		
		DBConfig.setHeaders(client);
		try {
			client.execute(RequestMethod.GET);
			String response = client.getResponse();
			JSONObject json = new JSONObject(response);
			users = JsonUtils.getUsersFromJson(json);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return users;
	}
	
	/**
	 * Obtiene todos los puntos (TimePoint) que se encuentren en un dia.
	 * Hace la llamada al web service por medio de http://address:port/locations
	 * 
	 * @param day formato YYYY-MM-DD
	 * @return List<TimePoint>
	 */
	public static List<TimePoint> getAllLocationsInDay(String day) {
		String url = DBConfig.URL + DBConfig.GET_POST_LOCATIONS + "/" + day;
		RestClient client = new RestClient(url);
		List<TimePoint> points;
		
		DBConfig.setHeaders(client);
		try {
			client.execute(RequestMethod.GET);
			String response = client.getResponse();
			JSONObject json = new JSONObject(response);
			points = JsonUtils.getLocationsFromJson(json);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return points;
	}
	
	/**
	 * Obtiene los puntos (TimePoint) para un SIM, que se encuentren en el rango
	 * de tiempo especificado para el dia dado.
	 * Hace la llamada al web service por medio de
	 * http://address:port/locations/{sim}/{day}/{start}/{end}
	 * 
	 * 
	 * @param sim
	 * @param day formato YYYY-MM-DD
	 * @param start tiempo inicial en segundos
	 * @param end tiempo final en segundos
	 * @return List<TimePoint>
	 */
	public static List<TimePoint> getLocationsForSimInRange(String sim,
			String day, int start, int end) {
		String url = DBConfig.URL + DBConfig.GET_POST_LOCATIONS + "/" + sim +
				"/" + day + "/" + start + "/" + end;
		List<TimePoint> points;
		RestClient client = new RestClient(url);
		
		DBConfig.setHeaders(client);
		try {
			client.execute(RequestMethod.GET);
			String response = client.getResponse();
			JSONObject json = new JSONObject(response);
			points = JsonUtils.getLocationsFromJson(json);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return points;
	}
	
	/**
	 * Obtiene los puntos (HeatPoint) que se encuentren en el rango
	 * de tiempo especificado para el dia dado.
	 * Hace la llamada al web service por medio de
	 * http://address:port/locations/{day}/{start}/{end}
	 * 
	 * @param day formato YYYY-MM-DD
	 * @param start tiempo inicial en segundos
	 * @param end tiempo final en segundos
	 * @return List<HeatPoint>
	 */
	public static List<HeatPoint> getAllHeatLocationsInRange(String day, 
			int start, int end) {
		String url = DBConfig.URL + DBConfig.GET_POST_LOCATIONS + "/" + day +
				"/" + start + "/" + end;
		List<HeatPoint> points;
		RestClient client = new RestClient(url);
		
		DBConfig.setHeaders(client);
		try {
			client.execute(RequestMethod.GET);
			String response = client.getResponse();
			JSONObject json = new JSONObject(response);
			points = JsonUtils.getHeatLocationsFromJson(json);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return points;
	}
}
