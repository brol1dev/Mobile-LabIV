package com.computomovil.labIV.remote;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import com.computomovil.labIV.DBHelper;
import com.computomovil.labIV.JsonUtils;

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
		
		DBConfig.setHeaders(client);
		try {
			DBHelper dbHelper = new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT usr_sim, dia, hora, " +
					"latitud, longitud FROM ubicaciones", null);
			JSONObject json = JsonUtils.constructJSON(cursor);
			cursor.close();
			
			Log.d("test", "hola");
			if (json != null) {
				Log.d("test", json.toString());
				client.setParam(json.toString());
				client.execute(RequestMethod.POST);
				int responseCode = client.getResponseCode();
				Log.d("response code", "code: " + responseCode);
				if (responseCode == HttpStatus.SC_CREATED)
					dbHelper.deleteDataInTable(db, "ubicaciones");
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
