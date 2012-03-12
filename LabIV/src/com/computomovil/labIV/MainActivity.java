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

		// Se obtiene de las preferencias el estado del servicio y el proveedor de ubicación. True: Para ahorro de energia
		// False: Para mayor presición (GPS).
		prefs=PreferenceManager.getDefaultSharedPreferences(this);    
    	running= prefs.getBoolean("running", false);
		prov= prefs.getBoolean("provider", false);
    			
		// Se registra el listener para el botón trackingButton
	    trackingButton =(ToggleButton) findViewById(R.id.trackingButton);
	    trackingButton.setOnCheckedChangeListener(trackingButtonListener);
	    
	    // Manejo del estado del botón de acuerdo al estado del servicio.
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
  	
	// Codigo con el cual se crea el elemento de menú, Preferencias.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, PREF_MENU, Menu.NONE, "Preferencias")
        	.setIcon(R.drawable.pref)
        	.setAlphabeticShortcut('p');
		return(super.onCreateOptionsMenu(menu));
	}

	// Manejo de eventos que se producen al seleccionar elementos de menú.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case PREF_MENU:			// Al seleccionar el elemento "Preferencias" se lanza la Actividad Preferencias.
				startActivity(new Intent(this, Preferencias.class));
				return(true);
		}
    return(super.onOptionsItemSelected(item));
	}
	
// listener para los eventos del botón trackingButton
	OnCheckedChangeListener trackingButtonListener = new OnCheckedChangeListener() {
		// Se llama cuando el usuario cambia el estado del botón
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {     	 
			// Si el servicio está corriendo...
	        if (!isChecked) {
	            // Se guarda en las preferencias que el servicio ya no está corriendo
	            actualiza = prefs.edit();
	    		actualiza.putBoolean("running", false);
	    		actualiza.commit();
	        	// Se detiene el Servicio que captura las ubicaciones.
	            stopService(new Intent(MainActivity.this,Servicio.class));
	            isGPSactive=false;	     
	         } 
	         else {   
	        	// Si el servicio no está corriendo, entonces se verifica que proveedor se usará, si la variable
	        	 // provider de las preferencias es false, se usará el GPS.
	        	 if (!prefs.getBoolean("provider", false)) {
	        		 // Se verifica que el GPS esté activo antes de iniciar el servicio
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
	           	}  // Si la variable provider de las preferencias es true, se usará network provider.
	            else {
		            // Se inicia el servicio y se guarda en las preferencias que el servicio está corriendo
		            actualiza = prefs.edit();
		    		actualiza.putBoolean("running", true);
		    		actualiza.commit();
		    		startService(new Intent(MainActivity.this, Servicio.class));	    			
	            }	            	
	         } 
	    } 
	 };  	
	 
	 // Método que lanza la actividad donde se muestra el contenido de la base de datos.
	 public void mostrarDatos(View view) {
	 Intent mostrarBase = new Intent(this,MuestraBase.class);
	 startActivity(mostrarBase);
	 }
	 
	 // Método que verifica si está activado el GPS, si no lo está abre un diálogo donde pregunta si se desea habilitar,
	 // En caso de aceptar, abre los ajustes donde se activa el GPS. Devuelve el estado del GPS.
	 public boolean enableGPS () {
    	  locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	  // determine if GPS is enabled or not, if not prompt user to enable it
	      if ( !isGPSenabled ()) {
	         AlertDialog.Builder builder = new AlertDialog.Builder(this);
	         builder.setTitle("GPS no está habilitado")
	                  .setMessage("Le gustaría ir a ajustes y habilitar el GPS?").setCancelable(true)
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
	   
	  // Método que devuelve si el GPS está activado o no.
	   public boolean isGPSenabled (){
		   return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
	   }
	   
	   public void iniciarMapa(View view)
	    {
	    	Intent intento = new Intent(this,Mapas.class);
	    	startActivity(intento);
	    }
}
