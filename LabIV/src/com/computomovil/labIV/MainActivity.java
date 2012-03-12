package com.computomovil.labIV;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity {
	private static final int PREF_MENU = Menu.FIRST+2;
	
	// Variables para el manejo de las preferencias
	SharedPreferences prefs;
	Editor actualiza;
	boolean running, prov, provAux;
	
	// Variables para el manejo de la ubicacion
	private LocationManager locationManager;	
	boolean isGPSactive;
	
	ToggleButton trackingButton ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Se obtiene de las preferencias el estado del servicio y el proveedor de ubicaci�n. True: Para ahorro de energia
		// False: Para mayor presici�n (GPS).
		prefs=PreferenceManager.getDefaultSharedPreferences(this);    
    	running= prefs.getBoolean("running", false);
		prov= prefs.getBoolean("provider", false);
    			
		// Se registra el listener para el bot�n trackingButton
	    trackingButton =(ToggleButton) findViewById(R.id.trackingButton);
	    trackingButton.setOnCheckedChangeListener(trackingButtonListener);
	    
	    // Manejo del estado del bot�n de acuerdo al estado del servicio.
	    if (running) 
	    	trackingButton.setChecked(true);
	    else
	    	trackingButton.setChecked(false);
	}
  
	@Override
	public void onResume() {
		super.onResume();   
		provAux= prefs.getBoolean("providerNew", prov);

		// Verifica si se cambiaron las preferencias, detiene y vuelve a ejecutar el servicio ahora con otro proveedor
		if (provAux != prefs.getBoolean("provider", false)) {
			actualiza = prefs.edit();
			actualiza.putBoolean("providerNew", prefs.getBoolean("provider", false));
			actualiza.commit();
        	stopService(new Intent(MainActivity.this,Servicio.class));
			startService(new Intent(MainActivity.this, Servicio.class));
		}
	}
  	
	// Codigo con el cual se crea el elemento de men�, Preferencias.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, PREF_MENU, Menu.NONE, "Preferencias")
        	.setIcon(R.drawable.pref)
        	.setAlphabeticShortcut('p');
		return(super.onCreateOptionsMenu(menu));
	}

	// Manejo de eventos que se producen al seleccionar elementos de men�.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case PREF_MENU:			// Al seleccionar el elemento "Preferencias" se lanza la Actividad Preferencias.
				startActivity(new Intent(this, Preferencias.class));
				return(true);
		}
    return(super.onOptionsItemSelected(item));
	}
	
// listener para los eventos del bot�n trackingButton
	OnCheckedChangeListener trackingButtonListener = new OnCheckedChangeListener() {
		// Se llama cuando el usuario cambia el estado del bot�n
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {     	 
			// Si el servicio est� corriendo...
	        if (!isChecked) {
	            // Se guarda en las preferencias que el servicio ya no est� corriendo
	            actualiza = prefs.edit();
	    		actualiza.putBoolean("running", false);
	    		actualiza.commit();
	        	// Se detiene el Servicio que captura las ubicaciones.
	            stopService(new Intent(MainActivity.this,Servicio.class));
	            isGPSactive=false;	     
	         } 
	         else {   
	        	// Si el servicio no est� corriendo, entonces se verifica que proveedor se usar�, si la variable
	        	 // provider de las preferencias es false, se usar� el GPS.
	        	 if (!prefs.getBoolean("provider", false)) {
	        		 // Se verifica que el GPS est� activo antes de iniciar el servicio
	        		 if (enableGPS()){
	        			actualiza = prefs.edit();
	            		actualiza.putBoolean("running", true);
	            		actualiza.commit();
	    	    		startService(new Intent(MainActivity.this, Servicio.class));
	            	}
	            	else {
	            		trackingButton.setChecked(false);
	            		actualiza = prefs.edit();
			    		actualiza.putBoolean("running", false);
			    		actualiza.commit();			    			
	            	}
	           	}  // Si la variable provider de las preferencias es true, se usar� network provider.
	            else {
		            // Se inicia el servicio y se guarda en las preferencias que el servicio est� corriendo
		            actualiza = prefs.edit();
		    		actualiza.putBoolean("running", true);
		    		actualiza.commit();
		    		startService(new Intent(MainActivity.this, Servicio.class));	    			
	            }	            	
	         } 
	    } 
	 };  	
	 
	 // M�todo que lanza la actividad donde se muestra el contenido de la base de datos.
	 public void mostrarDatos(View view) {
	 Intent mostrarBase = new Intent(this,MuestraBase.class);
	 startActivity(mostrarBase);
	 }
	 
	 // M�todo que verifica si est� activado el GPS, si no lo est� abre un di�logo donde pregunta si se desea habilitar,
	 // En caso de aceptar, abre los ajustes donde se activa el GPS. Devuelve el estado del GPS.
	 public boolean enableGPS () {
    	  locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	  // determine if GPS is enabled or not, if not prompt user to enable it
	      if ( !isGPSenabled ()) {
	         AlertDialog.Builder builder = new AlertDialog.Builder(this);
	         builder.setTitle("GPS no est� habilitado")
	                  .setMessage("Le gustar�a ir a ajustes y habilitar el GPS?").setCancelable(true)
	                  .setPositiveButton("Si", new DialogInterface.OnClickListener() {
	                     public void onClick(DialogInterface dialog, int id) {
	                        startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));	
	                        if 	(isGPSenabled ())
	                        	isGPSactive=true;
	                        else
	                        	isGPSactive=false;
	                     }
	                  }).setNegativeButton("No", new DialogInterface.OnClickListener() {
	                     public void onClick(DialogInterface dialog, int id) {
	                        dialog.cancel();	
	                        isGPSactive=false;
	                     }
	                  });
	         AlertDialog alert = builder.create();
	         alert.show();
	         
	      } else
	    	  isGPSactive=true;
	      
	   return isGPSactive;
	   }
	   
	  // M�todo que devuelve si el GPS est� activado o no.
	   public boolean isGPSenabled (){
		   return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
	   }
	   
	   public void iniciarMapa(View view)
	    {
	    	Intent intento = new Intent(this,Mapas.class);
	    	startActivity(intento);
	    }
}
