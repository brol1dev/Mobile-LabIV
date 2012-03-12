package com.computomovil.labIV;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;
 
public class Servicio extends Service 
{
	// Variables para el manejo de la ubicacion
	private LocationManager locationManager;
	private Location ubicacionPrevia; 
	private float distanciaRecorrida; // distancia recorrida entre 2 ubicaciones.
	private long tiempo;		// variable para cambiar el tiempo entre las actualizaciones
	private boolean cambiar;	// bandera para que solo se cambia una vez el modo cuando la bateria < 15.
	private int cont;			// Contador para el numero de veces que se obtiene una ubicacion repetida.
	
	// Variables para el manejo de las preferencias
	SharedPreferences prefs;
	Editor actualiza;
	boolean ahorro;

	// Variables para el manejo de la Base de datos
	protected DBHelper dBase;
	protected SQLiteDatabase db;
	
	// Variables para el manejo del estado de la bateria.
	int pct;
	boolean bat;
	String batAct;
	   	
   	// Variables para insertar un registro en la Base de datos.
   	int tiempoEnSeg;
    int hora, minutos, segundos;	
	double latitud, longitud;
	String dia="";
    SimpleDateFormat dateFormat;    
   	String providerName;
   	Calendar calendario;
   	
   	// Variables para el manejo de la SIM.
   	TelephonyManager telManager;
   	String SIM;
   	
   	@Override
	public void onCreate() 
	{
        super.onCreate();
        
	    db = (new DBHelper(this)).getWritableDatabase();
        dBase = new DBHelper(this);
	    dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Manejador para el servicio de telefonía
        telManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        // Se recupera la SIM actual
       	SIM= telManager.getSimSerialNumber().toString();
    	prefs=PreferenceManager.getDefaultSharedPreferences(this);    
    	cambiar=true;
       	
	}
	
   	@Override
   	public int onStartCommand(Intent intent, int flags, int startId) {
   		// Se recupera la variable que indica el modo en el que se recibiran las actualizaciones
   		ahorro= prefs.getBoolean("provider", false);

   		// Si esta activido el modo Ahorro, se selecciona un proveedor que consume menos energía (Network Provider)
		if (ahorro)
       		providerName=selectProviderSave();
       	else {
       	// Si no está activado se selecciona un proveedor que provee mayor precisión (GPS).
       		providerName=selectProviderAccuracy();
       		// Se registra un escucha para determinar si obtenemos un fix del GPS.
       		locationManager.addGpsStatusListener(gpsStatusListener);     		
 	   }

		// Con este método se empiezan a solicitar las actualizaciones de acuerdo al proveedor seleccionado.
		locationManager.requestLocationUpdates(providerName, 0, 0, locationListener);
		
		// Se registra el broadcastReceiver que obtiene el porcentaje de batería restante
	    registerReceiver(onBatteryChanged,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

   	    return START_STICKY;
   	}
    
	// Método con el cual se define un criterio para seleccionar el proveedor con mayor precisión. Devuelve el nombre
   	// del proveedor.
   	private String selectProviderAccuracy() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		// Se crea un objeto Criteria para especificar las caracteristicas que se requieren.
	    Criteria criteria = new Criteria(); 
	    criteria.setAccuracy(Criteria.ACCURACY_FINE); 
	    criteria.setBearingRequired(false); 
	    criteria.setCostAllowed(true); 
	    criteria.setPowerRequirement(Criteria.POWER_MEDIUM); 
	    criteria.setAltitudeRequired(false); 
	 
	    // Se selecciona y se devuelve el mejor proveedor en base al criterio
	    return locationManager.getBestProvider(criteria, true);

	}
	
   	// Método con el cual se define un criterio para seleccionar el proveedor con menor consumo de energía.
   	// Devuelve el nombre del proveedor.
	private String selectProviderSave() {
		locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationProvider provider = null;	

		// Se crea un objeto Criteria para especificar las caracteristicas que se requieren.
		Criteria criteria = new Criteria();		
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true); 
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_LOW);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);
		criteria.setSpeedRequired(false);
		criteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
		
		// Con getProviders obtiene solo coincidencias perfectas.
		List<String> names = locationManager.getProviders(criteria, false); 
		// Verifica si se encontro una coincidencia perfecta y asigna a provider.
		if ((names != null) && ! names.isEmpty()) {
			provider = locationManager.getProvider(names.get(0));
		} else {
			// Si no se encuentra una coincidencia perfecta, con getBestProvider se obtiene el proveedor que mas se
			// se aproxima al criterio.
			String name = locationManager.getBestProvider(criteria, false);			
			if (name != null) {
				provider = locationManager.getProvider(name);
			}
		}		   

		return provider.getName();
	}
	
	// Responde a los eventos del LocationManager
	private final LocationListener locationListener =  new LocationListener()  {
	    // Cuando la ubicación cambió
	    public void onLocationChanged(Location location) {        
	        // Si no es la primera ubicacion que se obtuvo, se saca la distancia entre la ubicación actual y la 
	    	// ubicación anterior y se llama a checkDistance donde se elige si se toma en cuenta o no la actualización-
	    	if (ubicacionPrevia != null) {
	              distanciaRecorrida = location.distanceTo(ubicacionPrevia);
	              checkDistance(distanciaRecorrida, location);
	          }
	    	// Si es la primera ubicación que se obtuvo se registra en la BD.
	          else { 
	        	  actualizarConNuevaUbicacion(location); 
	          }
        	  ubicacionPrevia = location;
	      } 

	   public void onProviderDisabled(String provider) 
	   {	      } 

	   public void onProviderEnabled(String provider) 
	   {	      } 

	   public void onStatusChanged(String provider, int status, Bundle extras) 
	   {	      } 
	}; 
	
	// Método que escucha cuando el GPS obtuvo un fix. 
	GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener()  {
	    public void onGpsStatusChanged(int event)   {
	         if (event == GpsStatus.GPS_EVENT_FIRST_FIX)  {
	            Toast.makeText(Servicio.this,"Señal Adquirida",Toast.LENGTH_SHORT).show();
	         } 
	      } 
	};

	// Método que maneja la frecuencia de las actualizaciones que se reciben de un proveedor. Recibe la distancia
	// entre la ubicación actual y la ubicación anterior.
    private void checkDistance (float distance, Location location) {
	   // Si la distancia es mayor o igual a 10, tomaremos en cuenta la actualización.
    	if (distance >= 10) {
    		// Si el tiempo es mayor o a 0, quiere decir que permaneció detenido en algun lugar por un tiempo y ahora
    		// el usuario comenzó a moverse nuevamente, entonces reiniciamos el tiempo entre actualizaciones
    		if (tiempo > 0) {
			   actualizarConNuevaUbicacion(location); // Guardamos los datos en un registro nuevo en la base			   
			   tiempo=0;		
			   cont=0;
			    // Quitamos las actualizaciones actuales y solicitamos actualizaciones con el tiempo reiniciado.
			    locationManager.removeUpdates(locationListener);	
				locationManager.requestLocationUpdates(providerName, tiempo, 0, locationListener);			   
			   }
			   else {
				   // Si no a permanecido mucho tiempo en un lugar, solo reiniciamos el contador.
				   actualizarConNuevaUbicacion(location); // Guardamos los datos en un registro nuevo en la base
				   cont=0;
			   }
	   } // Si la distancia no es menor a 10, entonces quiere decir que el usuario no se está moviendo, aumentamos el contador.
	   else {
		   // Si la distancia que recorrió el usuario es menor a 10 m, y ya se recibieron 10 actualizaciones en un radio de 
		   // 10 m, entonces comenzamos a pedir actualizaciones en un intervalo de tiempo más grande (5 minutos en este caso).
		   if (cont==10){
			   tiempo += 300000;
			   cont=0;
			   locationManager.removeUpdates(locationListener);
			   locationManager.requestLocationUpdates(providerName, tiempo, 0, locationListener);				   
		   }
		   else 
			   cont++;
	   }	   
}
	
	// Método que guarda en la base de datos el registro de la nueva ubicación.
	private void actualizarConNuevaUbicacion(Location location) {
		    if (location != null) { 
		       latitud = location.getLatitude();
		       longitud = location.getLongitude();	       
		       
		       dia= dateFormat.format(new Date());
		       // Se obtiene la hora, minutos y segundos actuales.
		       calendario = Calendar.getInstance();	      	       
		       hora =calendario.get(Calendar.HOUR_OF_DAY);
		       minutos = calendario.get(Calendar.MINUTE);
		       segundos = calendario.get(Calendar.SECOND);
		       
		       // Convierte la hora en segundos
		       hora = hora * 60 * 60;
		       minutos= minutos * 60;		       
		       tiempoEnSeg= hora + minutos + segundos;
		       // Inserta los valores en la base de datos.
		       dBase.insertarUbicacion(SIM, dia, tiempoEnSeg, latitud, longitud, db);	
		    } 
	}

	BroadcastReceiver onBatteryChanged=new BroadcastReceiver() {
		// Este método se ejecuta cada vez que cambia el estado o nivel de la bateria.    
		public void onReceive(Context context, Intent intent) {		      
		    // Sacamos el porcentaje de la bateria. 
			pct=100*intent.getIntExtra("level", 1)/intent.getIntExtra("scale", 1);
		    // Se obtienen las variables de las preferencias
			bat = prefs.getBoolean("battery", false);
		    batAct= prefs.getString("batteryAction", "DESACTIVAR");

		    // Si está activada la opcion para cuando el porcentaje de batería es menor a 15 .  
		    if (bat) {
		    	  // Si está marcada la opción desactivar, detiene el servicio.
		    	  if (pct < 15 && batAct.equals("DESACTIVAR")) {
		    		 stopSelf();
		    	  }
		    	  // Si está activada la opción ahorro, cambia el modo a AHORRO de batería ( selecciona Network Provider)
		    	  else if (pct < 15 && batAct.equals("AHORRO") && cambiar==true){
		    		  // Actualiza las preferencias
		    		  actualiza = prefs.edit();
		    		  actualiza.putBoolean("provider", true);
		    		  actualiza.putBoolean("providerNew", true);
		    		  actualiza.commit();
		    		  // Remueve las actualizaciones actuales y solicita otras con el nuevo proveedor.
		    		  locationManager.removeUpdates(locationListener);
		    		  if (gpsStatusListener!=null)
		    				locationManager.removeGpsStatusListener(gpsStatusListener);		
		    			locationManager.requestLocationUpdates(selectProviderSave(), 0, 0, locationListener);
		    			cambiar=false;
		    	  }
		    	  }
		      }		    	  		      
	};
	
	// Cuando se destruye el servicio dejamos de recibir actualizaciones y quitamos el Receiver de la bateria.
	// Se cambia la variable de las preferencias que indica que el servicio ya no está corriendo
    @Override
	public void onDestroy()
	{
		super.onDestroy();
		locationManager.removeUpdates(locationListener);
		locationManager.removeGpsStatusListener(gpsStatusListener);		
	    unregisterReceiver(onBatteryChanged);
	    actualiza = prefs.edit();
		actualiza.putBoolean("running", false);
		actualiza.commit();
	}	
    
	@Override
	public IBinder onBind(Intent arg0)
	{		return null;	}
}