package com.computomovil.labIV.remote;

import java.util.List;

import org.json.JSONObject;

import com.androidnatic.maps.model.HeatPoint;
import com.computomovil.labIV.JsonUtils;
import com.computomovil.labIV.bean.TimePoint;

public class GetRequests {

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
