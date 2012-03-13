package com.computomovil.labIV;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.androidnatic.maps.model.HeatPoint;
import com.computomovil.labIV.bean.TimePoint;
import com.google.android.maps.GeoPoint;

public class JsonUtils {

	//"locations":[{"Id":0,"Sim":"String","Longitude":0,"Latitude":0,"Day":"String","Time":0}]}
	public static JSONObject constructJSON(Cursor cursor) {
		JSONObject jroot = null;
		try {
			if (!cursor.isBeforeFirst())
				cursor.moveToPosition(-1);
			
			JSONArray jlocations = new JSONArray();
			int i = 0;
			while (cursor.moveToNext()) {
				++i;
				JSONObject jpoint = new JSONObject();
				jpoint.put("Sim", cursor.getString(0));
				jpoint.put("Day", DBHelper.formatoFecha(cursor.getString(1)));
				jpoint.put("Time", cursor.getInt(2));
				jpoint.put("Latitude", cursor.getDouble(3));
				jpoint.put("Longitude", cursor.getDouble(4));
				jlocations.put(jpoint);
			}
			Log.d("test", "total registros: " + i);
			if (i == 0)
				return null;
			jroot = new JSONObject();
			jroot.put("locations", jlocations);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return jroot;
	}
		
	/**
	 * Obtiene una lista de puntos de un objeto json
	 * 
	 * @param jroot formato = {"locations": [{...}, {...}]}
	 * @return
	 */
	public static List<TimePoint> getLocationsFromJson(JSONObject jroot) {
		List<TimePoint> points = new ArrayList<TimePoint>();
		
		JSONArray jlocations;
		try {
			jlocations = jroot.getJSONArray("locations");
			for (int i = 0; i < jlocations.length(); ++i) {
				JSONObject jloc = jlocations.getJSONObject(i);
				TimePoint tPoint = new TimePoint();

				double lat = jloc.getDouble("Latitude");
				double lon = jloc.getDouble("Longitude");
				GeoPoint gPoint = new GeoPoint((int) (lat * 1E6), 
						(int) (lon * 1E6));
				int time = (Integer) jloc.getInt("Time");
				tPoint.setGeoPoint(gPoint);
				tPoint.setHora(time);
				points.add(tPoint);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return points;
	}
	
	public static List<HeatPoint> getHeatLocationsFromJson(JSONObject jroot) {
		List<HeatPoint> points = new ArrayList<HeatPoint>();
		JSONArray jlocations;
		
		try {
			jlocations = jroot.getJSONArray("locations");
			for (int i = 0; i < jlocations.length(); ++i) {
				JSONObject jloc = jlocations.getJSONObject(i);
				HeatPoint hPoint = new HeatPoint();
				hPoint.lat = Float.parseFloat(jloc.getString("Latitude"));
				hPoint.lon = Float.parseFloat(jloc.getString("Longitude"));
				points.add(hPoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return points;
	}
}
