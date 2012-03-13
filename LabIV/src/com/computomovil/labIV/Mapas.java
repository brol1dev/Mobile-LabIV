package com.computomovil.labIV;

import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import com.androidnatic.maps.HeatMapOverlay;
import com.androidnatic.maps.SimpleMapView;
import com.androidnatic.maps.events.PanChangeListener;
import com.androidnatic.maps.model.HeatPoint;
import com.computomovil.labIV.remote.DBSender;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

public class Mapas extends MapActivity
{
	// Datos Miembro.
	// Debe notarse que el mapa no es del tipo comun MapView sino del tipo SimpleMapView que se encuentra en la biblioteca heatmap.jar
	// Para poder realizar el HeatMap.
	private DBHelper auxiliarBD;
	private HeatMapOverlay overlayHeatmap;
	private static RutaOverlay overlayRuta;
	private static String usuarioSeleccionado;
	private static int hora, minuto, annio, mes, dia, seleccion;
	private static double distanciaSeparacion;
	private static String fecha, horaInicial, horaFinal;
	private static SimpleMapView mapview;
	
	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onCreate(Bundle bundle) 
	{
		// Se crean instancias de la base de datos y del mapa.
		super.onCreate(bundle);
		this.auxiliarBD = new DBHelper(this);
		setContentView(R.layout.mapa);
		mapview = (SimpleMapView)findViewById(R.id.mapview);
		// Se establecen los valores de fecha y hora para los objetos TimePicker y DatePicker en base a la fecha y hora actuales.
	    Calendar c = Calendar.getInstance();
   	  	horaInicial = reacomodar(c.get(Calendar.HOUR_OF_DAY)) + ":" + reacomodar(c.get(Calendar.MINUTE)) + ":00";
   	  	horaFinal = reacomodar(c.get(Calendar.HOUR_OF_DAY)) + ":" + reacomodar(c.get(Calendar.MINUTE)) + ":00";
   	  	fecha = reacomodar(c.get(Calendar.YEAR)) + "-" + reacomodar(c.get(Calendar.MONTH)) + "-" + reacomodar(c.get(Calendar.DAY_OF_MONTH));
   	  	DBSender.sendLocations(this);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
	{
		  // Se crea el menu.
	      super.onCreateOptionsMenu(menu);
	      MenuInflater inflater = getMenuInflater();
	      inflater.inflate(R.menu.menumapa, menu);
	      return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		  // Este metodo realiza un switch a las opciones del menu. (Vista satelital, vista de mapa, heatmap y ruta).
	      switch (item.getItemId()) 
	      {
	         case R.id.opcionMap:
	            mapview.setSatellite(false);
	            return true;
	         case R.id.opcionSatellite:
	            mapview.setSatellite(true);
	            return true;
	         case R.id.opcionRuta:
		            trazarRuta(mapview);
		            return true;
	         case R.id.opcionHeatMap:
		            iniciarHeatMap(mapview);
		            return true;
	         default:
	            return super.onOptionsItemSelected(item);
	      }
	}
	
	public void iniciarHeatMap(SimpleMapView map)
	{
		// Se limpia primero el mapa y se abre la ventana de dialogo para establecer parametros de filtrado.
		map.getOverlays().clear();
		abrirDialogoHeatMap();
	}
	
	// Este metodo es llamado si se presiona el boton Heatmap del  menu.
	public void trazarHeatMap()
	{
		// Se instancia un objeto de la clase HeatMapOverlay, propia de la biblioteca heatmap.jar
		this.overlayHeatmap = new HeatMapOverlay(20000, mapview);
		mapview.getOverlays().add(overlayHeatmap);
		
		// Si se mueve el mapa, se hace zoom, etc., los puntos del heatmap y su forma deben refrescarse.
		mapview.addPanChangeListener(new PanChangeListener() 
		{	
			@Override
			public void onPan(GeoPoint anterior, GeoPoint actual) 
			{
				// Se toman los puntos de la base de datos relativos a todos los usuarios, para ser pintados en un Overlay sobre el mapa.
				List<HeatPoint> puntos = auxiliarBD.cargarPuntosDeCalor(mapview.getBounds(),horaInicial,horaFinal,fecha,distanciaSeparacion);
				
				if(puntos.size() > 0)
				{
					// Si hay mas de un punto, se llama al metodo update de la biblioteca heatmap.jar
					overlayHeatmap.update(puntos);
				}		
			}
		});
	}
	
	// Este metodo es llamado si se presiona el boton ruta del  menu.
	public void trazarRuta(SimpleMapView map)
	{
		// Se limpia el mapa y abre una ventana para seleccionar parametros y posteriormente se crea una instancia de la clase RutaOverlay.
		map.getOverlays().clear();
		abrirVentanaDialogo();
	}
	
	// Metodo que describe una ventana de dialogo para establecer parametros de filtrado.
	public void abrirVentanaDialogo()
	{
		// Se toman los elementos de un layout (combo.xml) que contiene un objeto del tipo Spinner (similar a un combo box)
		// Y tres botones, uno para filtrar la busqueda de la ruta (hora inicial, final y fecha).
		AlertDialog ventana = new AlertDialog.Builder(this).create();
		ventana.setTitle("Seleccionar valores");
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.combo, null);
		final Spinner spinnerUsuario = (Spinner)layout.findViewById(R.id.miSpinner);
		final Button boton1 = (Button) layout.findViewById(R.id.HoraInicial);
		final Button boton2 = (Button) layout.findViewById(R.id.HoraFinal);
		final Button boton3 = (Button) layout.findViewById(R.id.Fecha);
		   
		// De la base de datos se toman todos los usuarios y se introducen en el Spinner.
		SimpleCursorAdapter sca = auxiliarBD.encontrarUsuarios(this);
		spinnerUsuario.setAdapter(sca);
		
		// Se establece que hacer en caso de haber seleccionado un Usuario especifico. Se guarda en una variable global.
		spinnerUsuario.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) 
			{
			     Cursor c = (Cursor)parent.getItemAtPosition(pos);
			     usuarioSeleccionado = c.getString(c.getColumnIndexOrThrow("usr_nombre"));
			}
			    
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		// Se establece que hacer para el boton "Hora inicial".
		// Se abre una nueva ventana de dialogo para mostrar un TimePicker y seleccionar la hora inicial.
	    boton1.setOnClickListener(new View.OnClickListener()
	    {
	        public void onClick(View v)
	        {
	         	 seleccion = 1;
	         	 abrirDialogoTiempo();
	        }
	    });
		   
	    // Se establece que hacer para el boton "Hora final".
		// Se abre una nueva ventana de dialogo para mostrar un TimePicker y seleccionar la hora final.
	    boton2.setOnClickListener(new View.OnClickListener()
	    {
	         public void onClick(View v)
	         {
	           	 seleccion = 0;
	           	 abrirDialogoTiempo();
	         }
	    });
	       
	    // Se establece que hacer para el boton "Fecha".
		// Se abre una nueva ventana de dialogo para mostrar un DatePicker y seleccionar la fecha.
	    boton3.setOnClickListener(new View.OnClickListener()
	    {
	         public void onClick(View v)
	         {
	          	 abrirDialogoFecha();
	         }
	    });
	       
	    // Se crea un boton propio de la ventana de Dialogo actual con el texto "Aceptar" para en base a los parametros
	    // establecidos, comenzar la busqueda y calcular la distancia y velocidad promedio.
	    // Notese que los puntos se obtienen de la base de datos tomando en cuenta el usuario seleccionado, la hora y fecha.
	    // Los cuales fueron tomados previamente gracias a lo establecido en los metodos abrirDialogoTiempo y abrirDialogoFecha.
		ventana.setButton("Aceptar", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int a) 
			{
				// Se despliega el usuario seleccionado, se crea el objeto RutaOverlay y se adhiere al mapa la ruta graficamente.
				// RutaOverlay calcula la distancia total y velocidad promedio y se despliegan cuando se da "Tap".
				Toast.makeText(getBaseContext(),"Usuario: " + usuarioSeleccionado,Toast.LENGTH_SHORT).show();
				overlayRuta = new RutaOverlay(auxiliarBD,mapview,usuarioSeleccionado,horaInicial,horaFinal,fecha);
				mapview.getOverlays().add(overlayRuta);
			}
	   	});
		   
		// Se toma una vista para la ventana de dialogo y se muestra.
		ventana.setView(layout);
	    ventana.show();
	}
	
	// Metodo que describe una ventana de dialogo para establecer parametros de filtrado para el HeatMap.
	public void abrirDialogoHeatMap()
	{
		// Se toman los elementos de un layout (comboheatmap.xml) que contiene un EditText para la distancia de separacion entre puntos a filtrar
		// Y tres botones, uno para filtrar la busqueda de los lugares frecuentes (hora inicial, final y fecha).
		AlertDialog ventana = new AlertDialog.Builder(this).create();
		ventana.setTitle("Seleccionar valores");
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.comboheatmap, null);
		final Button boton1 = (Button) layout.findViewById(R.id.HoraInicial);
		final Button boton2 = (Button) layout.findViewById(R.id.HoraFinal);
		final Button boton3 = (Button) layout.findViewById(R.id.Fecha);
		final EditText distancia = (EditText)layout.findViewById(R.id.cajaDistancia);
		
		// Se establece que hacer para el boton "Hora inicial".
		// Se abre una nueva ventana de dialogo para mostrar un TimePicker y seleccionar la hora inicial.
	    boton1.setOnClickListener(new View.OnClickListener()
	    {
	        public void onClick(View v)
	        {
	         	 seleccion = 1;
	         	 abrirDialogoTiempo();
	        }
	    });
		   
	    // Se establece que hacer para el boton "Hora final".
		// Se abre una nueva ventana de dialogo para mostrar un TimePicker y seleccionar la hora final.
	    boton2.setOnClickListener(new View.OnClickListener()
	    {
	         public void onClick(View v)
	         {
	           	 seleccion = 0;
	           	 abrirDialogoTiempo();
	         }
	    });
	       
	    // Se establece que hacer para el boton "Fecha".
		// Se abre una nueva ventana de dialogo para mostrar un DatePicker y seleccionar la fecha.
	    boton3.setOnClickListener(new View.OnClickListener()
	    {
	         public void onClick(View v)
	         {
	          	 abrirDialogoFecha();
	         }
	    });
	       
	    // Se crea un boton propio de la ventana de Dialogo actual con el texto "Aceptar" para en base a los parametros
	    // establecidos, comenzar la busqueda y se pinten los puntos del heatmap.
	    // Notese que los puntos se obtienen de la base de datos tomando en cuenta el usuario seleccionado, la hora, fecha y distancia de separacion entre puntos.
	    // Los cuales fueron tomados previamente gracias a lo establecido en los metodos abrirDialogoTiempo y abrirDialogoFecha.
		ventana.setButton("Aceptar", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int a) 
			{
				try
				{
					// Se guarda la distancia de separacion y se despliega
					distanciaSeparacion = Double.parseDouble(distancia.getText().toString());
					Toast.makeText(getBaseContext(),"Distancia: " + distanciaSeparacion,Toast.LENGTH_SHORT).show();
				}
				catch(Exception e)
				{
					distanciaSeparacion = 0;
				}
				trazarHeatMap();
			}
	   	});
		   
		// Se toma una vista para la ventana de dialogo y se muestra.
		ventana.setView(layout);
	    ventana.show();
	}
		
	// Metodo que despliega un TimePicker para seleccionar la hora inicial y final del filtro.
	public void abrirDialogoTiempo()
	{
	    // Se crea una ventana de dialogo tomando en cuenta un layout que contiene un TimePicker y tres botones (Aceptar, Reset y Cancelar).
		final Dialog dialogoHora = new Dialog(this);

		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		final RelativeLayout vistaDialogoHora = (RelativeLayout) inflater.inflate(R.layout.hora, null);
		final Button boton1 = (Button) vistaDialogoHora.findViewById(R.id.Aceptar);
		final Button boton2 = (Button) vistaDialogoHora.findViewById(R.id.Reset);
		final Button boton3 = (Button) vistaDialogoHora.findViewById(R.id.Cancelar);
		final TimePicker timePicker = (TimePicker) vistaDialogoHora.findViewById(R.id.TimePicker);
		
		// Se establece que el TimePicker estara en el formato de 24 hrs.
		timePicker.setIs24HourView(true);
		
		// Para el boton "Aceptar", se toma la hora establecida por el usuario a partir del TimePicker y 
		// Se guarda el valor de la hora en la variable hora Inicial y hora Final segun sea el caso.
		boton1.setOnClickListener(new View.OnClickListener()
	    {		
			public void onClick(View v)
			{
				timePicker.clearFocus();
				hora   = timePicker.getCurrentHour();
				minuto = timePicker.getCurrentMinute();
				dialogoHora.dismiss();
				
				if(seleccion == 1)
				{
					Toast.makeText(getBaseContext(),"Hora Inicial: " + reacomodar(hora) + ":" + reacomodar(minuto) + ":00",Toast.LENGTH_SHORT).show();
					horaInicial = reacomodar(hora) + ":" + reacomodar(minuto) + ":00";
				}
				else
				{
					Toast.makeText(getBaseContext(),"Hora Final: " + reacomodar(hora) + ":" + reacomodar(minuto) + ":00",Toast.LENGTH_SHORT).show();
					horaFinal = reacomodar(hora) + ":" + reacomodar(minuto) + ":00";
				}
			}
		});
		
		// Para el boton "Reset", se reestablecen los valores del TimePicker.
		boton2.setOnClickListener(new View.OnClickListener()
	    {
	            public void onClick(View v)
	            {
	            	 final Calendar c = Calendar.getInstance();
	            	 timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
	         		 timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
	            }
	    });
			
		// Para el boton "Cancelar", se desaparece la ventana de Dialogo.
		boton3.setOnClickListener(new View.OnClickListener()
	    {
	            public void onClick(View v)
	            {
	            	 dialogoHora.cancel();
	            }
	    });
			
		// Se establece la vista para la ventana y se muestra.
		dialogoHora.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogoHora.setContentView(vistaDialogoHora);
		dialogoHora.show();
	}
	   
	// Metodo que despliega un DatePicker para seleccionar la fecha del filtro.
	public void abrirDialogoFecha()
	{
	    // Se crea una ventana de dialogo tomando en cuenta un layout que contiene un DatePicker y tres botones (Aceptar, Reset y Cancelar).
		final Dialog dialogoFecha = new Dialog(this);

		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		final RelativeLayout vistaDialogoFecha = (RelativeLayout) inflater.inflate(R.layout.fecha, null);
		final Button boton1 = (Button) vistaDialogoFecha.findViewById(R.id.Aceptar);
		final Button boton2 = (Button) vistaDialogoFecha.findViewById(R.id.Reset);
		final Button boton3 = (Button) vistaDialogoFecha.findViewById(R.id.Cancelar);
		final DatePicker datePicker = (DatePicker) vistaDialogoFecha.findViewById(R.id.DatePicker);
		
		// Para el boton "Aceptar", se toma la fecha establecida por el usuario a partir del DatePicker y 
		// Se guarda el valor de la fecha en la variable fecha.
		boton1.setOnClickListener(new View.OnClickListener()
	    {		
			public void onClick(View v)
			{
				datePicker.clearFocus();
				annio   = datePicker.getYear();
				mes = datePicker.getMonth()+1;
				dia = datePicker.getDayOfMonth();
				dialogoFecha.dismiss();
				
				Toast.makeText(getBaseContext(),"Fecha: " + reacomodar(annio) + "-" + reacomodar(mes) + "-" + reacomodar(dia),Toast.LENGTH_SHORT).show();
		     	fecha = reacomodar(annio) + "-" + reacomodar(mes) + "-" + reacomodar(dia);
			}
		});
		
		// Para el boton "Reset", se reestablecen los valores del DatePicker.
		boton2.setOnClickListener(new View.OnClickListener()
	    {
	        public void onClick(View v)
	        {
	         	 final Calendar c = Calendar.getInstance();
	           	 datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	        }
	    });
			
		// Para el boton "Cancelar", se desaparece la ventana de Dialogo.
		boton3.setOnClickListener(new View.OnClickListener()
		{
		    public void onClick(View v)
		    {
		       	 dialogoFecha.cancel();
		    }
		});
			
		// Se establece la vista para la ventana y se muestra.
		dialogoFecha.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogoFecha.setContentView(vistaDialogoFecha);
		dialogoFecha.show();
	}
	
	// Metodo que colocar un "0" a la cadena de la hora y fecha si el valor esta entre 0 y 9.
	private static String reacomodar(int c)
	{
		if (c >= 10)
		    return String.valueOf(c);
		else
		    return "0" + String.valueOf(c);
	}
}