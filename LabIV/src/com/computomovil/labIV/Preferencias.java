package com.computomovil.labIV;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preferencias extends PreferenceActivity {
	
  private CheckBoxPreference provider, battery;
  private ListPreference batteryAct;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  
  // Método para agregar preferencias desde una layout definido.
  addPreferencesFromResource(R.layout.preferencias);
    
  // El manejador para las preferencias viene de findPreference no de findViewById
  provider = (CheckBoxPreference) getPreferenceScreen().findPreference("provider");
  battery = (CheckBoxPreference) getPreferenceScreen().findPreference("battery");
  batteryAct =  (ListPreference) getPreferenceScreen().findPreference("batteryAction");
 
  // Cuando el usuario cambia las preferencias podemos actualizar el valor de los elementos en pantalla
  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
  prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
     public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {       
    	if (key.equals("provider")) {		// Con el valor de key identificamos que elemento fue el que recibio el evento.
           setCheckBoxSummary(provider);
        }
        else if (key.equals("battery")) {
            setCheckBoxSummary(battery);
         }  	
     }
  });  
}

  // Cuando se activa el método onResume actualizamos los valores del estado de los wigdets de la PreferenceActivity
  @Override 
  protected void onResume() {
	  super.onResume(); 
	  setCheckBoxSummary(provider);
	  setCheckBoxSummary(battery);
  }
  
  // Cambia el estado de los widgets al recibir algun evento provocado por el usuario.
	private void setCheckBoxSummary(CheckBoxPreference pref) {	 
		 if (pref.getKey().equals("provider") ) {		
			 if (pref.isChecked() ) 	// Cambia el texto de resumen del elemento de la preferenceActivity
				 pref.setSummary("Menor presición, menor consumo de energía");
			 else {
				pref.setSummary("Mayor presición, mayor consumo de energía");
			 }
	  } else {
		  	if (pref.isChecked() ) 			// Habilita o deshabilita el elemento de la preferenceActivity
				 batteryAct.setEnabled(true);
		  	else
				 batteryAct.setEnabled(false);
		 }
	  }
		
}
