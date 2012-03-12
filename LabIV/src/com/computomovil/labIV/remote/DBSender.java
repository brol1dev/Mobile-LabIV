package com.computomovil.labIV.remote;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.computomovil.labIV.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//import android.util.Log;

public class DBSender {
	
	/**
	 * Envia puntos de localizacion en la base de datos local
	 * al servidor.
	 * Ruta usada es http://serveraddress:8080/locations
	 */
	public static void sendLocations(Context context) {
		String url = DBConfig.URL + DBConfig.GET_POST_LOCATIONS;
		RestClient client = new RestClient(url);
		
		DBSender.setHeaders(client);
		try {
			SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT usr_sim, dia, hora, " +
					"latitud, longitud FROM ubicaciones", null);
			
			Log.i("test", constructJSON(cursor));
			cursor.close();
			db.close();
//			client.setParam(constructJSON(cursor));
//			client.execute(RequestMethod.POST);
//			String response = client.getResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setHeaders(RestClient client) {
		Set<String> hKeys = DBConfig.getHeaderKeys();
		for (String key : hKeys)
			client.addHeader(key, DBConfig.getHeaderValue(key));
	}
	
	//"locations":[{"Id":0,"Sim":"String","Longitude":0,"Latitude":0,"Day":"String","Time":0}]}
	public static String constructJSON(Cursor cursor) {
		JSONObject jroot = new JSONObject();
		try {
			JSONArray jlocations = new JSONArray();
			if (!cursor.isBeforeFirst())
				cursor.moveToPosition(-1);
			
			while (cursor.moveToNext()) {
				JSONObject jpoint = new JSONObject();
				jpoint.put("Sim", cursor.getString(0));
				jpoint.put("Day", DBHelper.formatoFecha(cursor.getString(1)));
				jpoint.put("Time", cursor.getInt(2));
				jpoint.put("Latitude", cursor.getDouble(3));
				jpoint.put("Longitude", cursor.getDouble(4));
				jlocations.put(jpoint);
			}
			jroot.put("locations", jlocations);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jroot.toString();
	}
}
