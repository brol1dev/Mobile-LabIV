package com.computomovil.labIV;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import android.location.Location;

// Clase que crea una capa sobre el mapa para dibujar la ruta de un usuario a una determinada hora en un dia especifico.
public class RutaOverlay extends Overlay
{
	   // Datos miembro.
	   private List <GeoPoint> ubicaciones;
	   private DBHelper auxiliarBase;
	   private Paint ruta;
	   private Paint punto;
	   public Context contexto;
	   private static final double MILISEG_POR_HORA = 1000 * 60 * 60;
	   private double distanciaRecorrida;
	   private double velocidadPromedio;
	   
	   // Constructor
	   public RutaOverlay(DBHelper base, MapView map, String usuarioSeleccionado, String horaInicial, String horaFinal, String fecha) 
	   {
		  // Se inicializan los parametros para la ruta (ruta roja y puntos negros, llenado de circulos, antialiasing, etc.).
	      ruta = new Paint();
	      ruta.setAntiAlias(true);
	      ruta.setColor(Color.RED);
	      ruta.setStyle(Paint.Style.STROKE);
	      ruta.setStrokeWidth(1);
	      auxiliarBase = base;
	      contexto = map.getContext();
	      ubicaciones = new ArrayList<GeoPoint>();
	     
	      punto = new Paint();
	      punto.setAntiAlias(true);
	      punto.setStyle(Paint.Style.FILL);
	      
     	  // Se inicializa la distancia total del recorrido y velocidad promedio.
     	  distanciaRecorrida = 0;
     	  velocidadPromedio = 0;
     	  
     	  // Se manda a llamar a una ventana de dialogo inicial para tomar los parametros.
     	  ubicaciones = auxiliarBase.cargarPuntosRuta(usuarioSeleccionado,horaInicial,horaFinal,fecha);
		  calcularDistancia(ubicaciones);
		  calcularVelocidad();
	   }

	   // Se sobre escribe el metodo draw para poder dibujar las rutas sobre el mapa.
	   @Override
	   public void draw(Canvas canvas, MapView mapView, boolean shadow) 
	   {
		  // Se crean elementos del tipo Path, Point y GeoPoint para poder trazar una ruta.
	      super.draw(canvas, mapView, shadow);
	      Path r = new Path();
	      GeoPoint gp1;
	      GeoPoint gp2;
	      Point p1 = new Point();
	      Point p2 = new Point();
	      
	      // Se toma un arreglo que no es mas que una lista de objetos del tipo GeoPoint para trazar los puntos.
	      // Solo se trazaran rutas en caso de haber al menos dos puntos.
	      // Recuerdese que el ultimo elemento de la lista ubicaciones contiene la hora inicial y final del recorrido.
	      // Por ello los ciclos for van de 0 a size()-2
	      if(ubicaciones.size() > 2)
	      {
	    	  for (int i = 0; i < ubicaciones.size()-2; ++i) 
	    	  {
	    		  // Se toma el GeoPoint actual y siguiente.
	    		  gp1 = ubicaciones.get(i);
	    		  gp2 = ubicaciones.get(i+1);
	    		  
	    		  // Se crean elementos Point para poder trazar sobre el mapa.
	    		  p1 = new Point();
	    		  p2 = new Point();	         
	           
	    		  // Se convierten en pixeles los equivalentes entre ubicaciones (GeoPoint) y puntos de la pantalla (Point). 
	    		  mapView.getProjection().toPixels(gp1, p1);
	    		  mapView.getProjection().toPixels(gp2, p2);
	         
	    		  // Se traza una linea entre cada dos puntos.
	    		  r.moveTo(p2.x,p2.y);
	    		  r.lineTo(p1.x, p1.y);
	         
	    		  // Se dibuja un circulo en el punto actual.
	    		  canvas.drawCircle(p1.x, p1.y, 5, punto);
	    	  }

	    	  // Se dibuja un circulo en el ultimo punto y se traza toda una ruta.
	    	  canvas.drawCircle(p2.x, p2.y, 5, punto);
	    	  canvas.drawPath(r, ruta);
	      }
	   }
	   
	   // Metodo que se sobre escribe para que cuando se toque la pantalla y se haya pintado una ruta, se despliegue la distancia
	   // recorrida y velocidad promedio.
	   @Override
	   public boolean onTap(GeoPoint p, MapView map)
	   {
		   // Se crea una ventana de dialogo para mostrar los resultados de los calculos.
		   AlertDialog.Builder ventana = new AlertDialog.Builder(contexto);
		   ventana.setTitle("Resultados");
		   String resultado = "Distancia recorrida:\t" + distanciaRecorrida + " km.\n" + "Velocidad Promedio:\t" + velocidadPromedio + " kph.";
	       ventana.setMessage(resultado);
	       ventana.show();
	       return true;
	   }
	   
	   // Metodo que calcula la distancia total de la ruta.
	   public void calcularDistancia(List<GeoPoint> u)
	   {
		   GeoPoint gp1, gp2;
		   Location l1, l2;

		   l1 = new Location("Location1");
		   l2 = new Location("Location1"); 
		   
		   // Similar al dibujado, se recorre la lista de ubicaciones de la consulta y se suma la distancia total.
		   // Tomando en cuenta el metodo de calcular la distancia entre dos geolocaciones.
		   if(ubicaciones.size() > 2)
		   {
			   for (int i = 0; i < u.size()-2; ++i) 
			   {
				   gp1 = u.get(i);
				   gp2 = u.get(i+1);	
				   l1.setLatitude(gp1.getLatitudeE6()/1E6);
				   l1.setLongitude(gp1.getLongitudeE6()/1E6);
				   
				   l2.setLatitude(gp2.getLatitudeE6()/1E6);
				   l2.setLongitude(gp2.getLongitudeE6()/1E6);

		           distanciaRecorrida += l2.distanceTo(l1);
			   }
		   }
	   }
	   
	   // Metodo que calcula la velocidad promedio en base a la distancia total del recorrido.
	   public void calcularVelocidad()
	   {
		   // Si la distancia es cero, no se hace nada, pues quiere decir que no hay ruta y se toman en cuenta los valores de
		   // inicializacion para distancia y velocidad.
		   if(distanciaRecorrida == 0)
		   {
			   return;
		   }
		   else
		   {
			   // Se toma el ultimo GeoPoint de la lista de ubicaciones donde se guardo intencionalmente a manera de GeoPoint
			   // La hora del punto inicial y final del recorrido.
			   // Pues si se tomaran en cuenta la hora inicial y final establecidas por el usuario podria ser que uno de los tiempos
			   // este muy distante del ultimo punto del recorrido y se realizarian mal los calculos porque se consideraria que
			   // el usuario estaba moviendose a la hora final o inicial establecida por el usuario.
			   // Ahora bien, para el calculo, se transforman a milisegundos las horas inicial y final, se obtiene la diferencia.
			   // Y se divide entre un factor de milisegundos por hora para determinar el numero total de horas de recorrido.
			   // Posteriormente, la distancia se obtiene en kilometros y la velocidad se obtiene a partir de la distancia total del recorrido
			   // entre el numero de horas totales del mismo.
			   GeoPoint auxiliar = ubicaciones.get(ubicaciones.size()-1);
			   int horaInicialEnMiliSeg = auxiliar.getLatitudeE6()*1000;
			   int horaFinalEnMiliSeg = auxiliar.getLongitudeE6()*1000;
			   long milisegundos = horaFinalEnMiliSeg - horaInicialEnMiliSeg;
			   double totalHoras = milisegundos / MILISEG_POR_HORA;
			   distanciaRecorrida = distanciaRecorrida / 1000;
        	   velocidadPromedio = distanciaRecorrida / totalHoras;
           }
	   }
}