package com.computomovil.labIV;

// Bibliotecas necesarias.
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleCursorAdapter;
import com.androidnatic.maps.model.HeatPoint;
import com.androidnatic.maps.model.LatLon;
import com.google.android.maps.GeoPoint;

public class DBHelper extends SQLiteOpenHelper 
{
	// Datos miembro.
    private SQLiteDatabase baseDeDatos;
	private final static String NOMBREBD = "BDUbicaciones";
	
	// Constructor.
	public DBHelper(Context context)
	{	
		super(context,NOMBREBD,null,1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		// Tablas establecidas.
		String sql = "CREATE TABLE IF NOT EXISTS ubicaciones (" +
					 "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					 "usr_sim VARCHAR(25), " +
					 "dia DATE, " +
					 "hora INTEGER, " +
					 "latitud DOUBLE, " +
					 "longitud DOUBLE);";
	
        String sql2 = "CREATE TABLE IF NOT EXISTS usuarios (" +
					  "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					  "usr_sim VARCHAR(25), " +
					  "usr_nombre TEXT, " +
					  "usr_password TEXT);";

        // Se ejecutan las sentencias sql.
		baseDeDatos = db;
		baseDeDatos.execSQL(sql);
		baseDeDatos.execSQL(sql2);
		cargarDatos(db);
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2)
	{
		baseDeDatos.execSQL("DROP TABLE IF EXISTS ubicaciones");
		baseDeDatos.execSQL("DROP TABLE IF EXISTS usuarios");
		onCreate(baseDeDatos);
	}

	// Metodo que carga los datos en la base.
	private void cargarDatos(SQLiteDatabase db)
	{
		ContentValues values = new ContentValues();
		ContentValues values1 = new ContentValues();

		// Usuario 1
		values.put("usr_sim", "8952020011596052685");
		values.put("latitud", 42.365879617);
		values.put("longitud", -71.440118567);
		values.put("dia", formatoFecha("2012-05-07"));
		values.put("hora", horaEnSegundos("13:24:35"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020011596052685");
		values.put("latitud", 42.365814867);
		values.put("longitud", -71.440098667);
		values.put("dia", formatoFecha("2012-05-07"));
		values.put("hora", horaEnSegundos("13:24:37"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020011596052685");
		values.put("latitud", 42.365732267);
		values.put("longitud", -71.439988267);
		values.put("dia", formatoFecha("2012-05-07"));
		values.put("hora", horaEnSegundos("13:24:38"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020011596052685");
		values.put("latitud", 42.365694217);
		values.put("longitud", -71.439910250);
		values.put("dia", formatoFecha("2012-05-07"));
		values.put("hora", horaEnSegundos("13:24:39"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020011596052685");
		values.put("latitud", 42.365660350);
		values.put("longitud", -71.439808433);
		values.put("dia", formatoFecha("2012-05-07"));
		values.put("hora", horaEnSegundos("13:24:40"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020011596052685");
		values.put("latitud", 42.365660350);
		values.put("longitud", -71.439651383);
		values.put("dia", formatoFecha("2012-05-07"));
		values.put("hora", horaEnSegundos("13:24:41"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020011596052685");
		values.put("latitud", 42.365879617);
		values.put("longitud", -71.439501800);
		values.put("dia", formatoFecha("2012-05-07"));
		values.put("hora", horaEnSegundos("13:24:42"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020011596052685");
		values.put("latitud", 42.365600550);
		values.put("longitud", -71.439359783);
		values.put("dia", formatoFecha("2012-05-07"));
		values.put("hora", horaEnSegundos("13:28:43"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020011596052685");
		values.put("latitud", 42.365584850);
		values.put("longitud", -71.439217700);
		values.put("dia", formatoFecha("2012-05-07"));
		values.put("hora", horaEnSegundos("13:30:44"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020011596052685");
		values.put("latitud", 94.365563483);
		values.put("longitud", -37.439082633);
		values.put("dia", formatoFecha("2012-05-07"));
		values.put("hora", horaEnSegundos("13:31:45"));
		db.insert("ubicaciones", null,values);
		
		// Usuario 2
		values.put("usr_sim", "8952020004672373570");
		values.put("latitud", -12.43134856223262);
		values.put("longitud", -71.45677053717904);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("14:31:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020004672373570");
		values.put("latitud", -12.4314304288928);
		values.put("longitud", -71.45661561766124);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("14:32:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020004672373570");
		values.put("latitud", -12.4314304288928);
		values.put("longitud", -71.45646372648226);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("14:33:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020004672373570");
		values.put("latitud", -12.43147213858021);
		values.put("longitud", -71.45631077918996);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("14:34:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020004672373570");
		values.put("latitud", -12.431515076904404);
		values.put("longitud", -71.45615413991473);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("14:35:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020004672373570");
		values.put("latitud", -12.43155671046647);
		values.put("longitud", -71.45600276603994);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("14:36:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020004672373570");
		values.put("latitud", -12.431597715740914);
		values.put("longitud", -71.45585533489988);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("14:37:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020004672373570");
		values.put("latitud", -12.431635158823894);
		values.put("longitud", -71.45570973842516);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("14:38:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020004672373570");
		values.put("latitud", -12.43167347181668);
		values.put("longitud", -71.45556726774439);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("14:39:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "8952020004672373570");
		values.put("latitud", -12.4317104342848);
		values.put("longitud", -71.4554328358702);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("14:40:00"));
		db.insert("ubicaciones", null,values);
	    
		// Usuario 3
		values.put("usr_sim", "usuario3");
		values.put("latitud", 42.42974488089331);
		values.put("longitud", -71.45411084881086);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("12:19:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "usuario3");
		values.put("latitud", 42.42967389094455);
		values.put("longitud", -71.4541936476289);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("12:20:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "usuario3");
		values.put("latitud", 42.429606616021196);
		values.put("longitud", -71.45429966920295);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("12:21:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "usuario3");
		values.put("latitud", 42.4295423826605);
		values.put("longitud", -71.45440378510996);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("12:22:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "usuario3");
		values.put("latitud", 42.42947770628986);
		values.put("longitud", -71.45451756125354);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("12:23:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "usuario3");
		values.put("latitud", 42.429411230995846);
		values.put("longitud", -71.454635606903);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("12:24:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "usuario3");
		values.put("latitud", 42.42934393468536);
		values.put("longitud", -71.45475546419759);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("12:25:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "usuario3");
		values.put("latitud", 42.42926825783195);
		values.put("longitud", -71.45485917561128);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("12:26:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "usuario3");
		values.put("latitud", 42.42919072038049);
		values.put("longitud", -71.45496006988894);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("12:27:00"));
		db.insert("ubicaciones", null,values);
		
		values.put("usr_sim", "usuario3");
		values.put("latitud", 42.429114125870285);
		values.put("longitud", -71.4550611038169);
		values.put("dia", formatoFecha("2011-05-07"));
		values.put("hora", horaEnSegundos("12:28:00"));
		db.insert("ubicaciones", null,values);
		
		values1.put("usr_sim", "8952020011596052685");
		values1.put("usr_nombre", "coronita");
		db.insert("usuarios", null,values1);
		
		values1.put("usr_sim", "8952020004672373570");
		values1.put("usr_nombre", "ivan");
		db.insert("usuarios", null,values1);
		
		values1.put("usr_sim", "usuario3");
		values1.put("usr_nombre", "eric");
		db.insert("usuarios", null,values1);
	}
	
	// Metodo que obtiene todas las posiciones de todos los usuarios para colocarlos en el mapa de calor.
	public List<HeatPoint> cargarPuntosDeCalor(int[][] bounds, String hi, String hf, String f,double ds)
	{
		// Se utiliza la siguiente sentencia SQL.
		// Bounds toma en cuenta las dimensiones actuales del mapa, para poder trazar solo lo necesario.
		String sql = "SELECT latitud,longitud FROM ubicaciones WHERE latitud >= " + bounds[0][0]/1E6 + " AND latitud <= " + bounds[1][0]/1E6 + " AND longitud >= " + bounds[0][1]/1E6 + " AND longitud <= " + bounds[1][1]/1E6 + " AND dia=? AND hora BETWEEN ? AND ?";
		SQLiteDatabase db = getReadableDatabase();
		// Las cadenas enviadas deben transformarse a los tipos de los atributos propios de la tabla (Enteros, Date, etc.).
		int horaInicial = horaEnSegundos(hi);
		int horaFinal = horaEnSegundos(hf);
			    
		// Se ejecuta la sentencia.
		Cursor cursor = null;
		cursor = db.rawQuery(sql, new String[] {formatoFecha(f),Integer.toString(horaInicial),Integer.toString(horaFinal)});
		List<HeatPoint> ubicaciones = new ArrayList<HeatPoint>(cursor.getCount());
		
		// Se crean variables auxiliares para filtrar los puntos en base a la distancia brindada por el usuario.
		// Nota: LatLon es un tipo de dato especifico de heatmap.jar y esta clase contiene metodos especiales para el calculo de la distancia
		// Entre dos geolocaciones, similar a distanceTo de Location.
		List<HeatPoint> resultados = new ArrayList<HeatPoint>();
		LatLon a, b;
		
		// Se toman todas las latitudes y longitudes de todos los usuarios.
		if(cursor.moveToFirst())
		{
			do
			{
				HeatPoint punto = new HeatPoint();
				punto.lon = (cursor.getFloat(cursor.getColumnIndex("longitud")));
				punto.lat = (cursor.getFloat(cursor.getColumnIndex("latitud")));
				
				ubicaciones.add(punto);
			} while (cursor.moveToNext());
		}
		
		// Si la lista de puntos no esta vacia se procede a filtrar por medio de la distancia brindada por el usuario.
		if((ubicaciones.size() > 0) && (ds > 0))
		{
			// Se guarda la posicion inicial de la lista y se adhiere al resultado.
			a = new LatLon(ubicaciones.get(0).lat,ubicaciones.get(0).lon);
			resultados.add(ubicaciones.get(0));
			
			// Para cada valor posterior al primero se evalua si esta en el rango de distancia.
			for(int i=1; i<ubicaciones.size()-1; i++)
			{
				b = new LatLon(ubicaciones.get(i).lat,ubicaciones.get(i).lon);
				
				// Si la distancia entre las dos geolocaciones esta en el rango dado por el usuario, se agrega a la lista final de 
				// puntos a graficar y se establece como nuevo punto de comparacion.
				if(a.distance(b) <= ds)
				{
					a = b;
					resultados.add(ubicaciones.get(i));
				}
			}
		}
		
		db.close();
		
		// Si la distancia brindada por el usuario es cero, quiere decir que la lista resultado esta vacia y que se debe devolver
		// La lista ubicaciones.
		if(ds == 0)
			return ubicaciones;
		
		// Por defecto se devuelve la lista resultados.
		return resultados;
	}
	
	// Metodo que obtiene todos los puntos de un solo usuario dependiendo los parametros de horas y dia especificados por el usuario.
	public List<GeoPoint> cargarPuntosRuta(String id, String hi, String hf, String f)
	{
		// Sentencia que filtra los puntos (determinado usuario, fecha y tiempo).
		String sql = "SELECT _id,latitud,longitud,hora FROM ubicaciones WHERE usr_sim IN (SELECT usr_sim FROM usuarios WHERE usr_nombre = ?) AND dia=? AND hora BETWEEN ? AND ?";
		SQLiteDatabase db = getReadableDatabase();
		// Las cadenas enviadas deben transformarse a los tipos de los atributos propios de la tabla (Enteros, Date, etc.).
		int horaInicial = horaEnSegundos(hi);
		int horaFinal = horaEnSegundos(hf);
		
	    // Se ejecuta la sentencia.
		Cursor cursor = null;
		cursor = db.rawQuery(sql, new String[] {id,formatoFecha(f),Integer.toString(horaInicial),Integer.toString(horaFinal)});
		List<GeoPoint> puntos = new ArrayList<GeoPoint>(cursor.getCount());
		
		// Ya que ademas de las latitudes y longitudes, se necesita la hora de la primera fila del resultado y de la ultima,
		// Tambien se crea una lista que tomara todas las horas de la consulta.
		List<Integer> horas = new ArrayList<Integer>();
		
		if(cursor.moveToFirst())
		{
			do
			{
				// Se toman los datos.
				double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("latitud")));
		    	double lng = Double.parseDouble(cursor.getString(cursor.getColumnIndex("longitud")));
				GeoPoint ubicacion = new GeoPoint((int)(lat*1E6),(int)(lng*1E6));
				Integer hora = new Integer(cursor.getString(cursor.getColumnIndex("hora")));
				puntos.add(ubicacion);
				horas.add(hora);
			} while (cursor.moveToNext());
		}
		
		// Si la consulta no da null, entonces se adhiere la hora inicial y final de la ruta como un nuevo GeoPoint, debido a que
		// No es posible regresar mas de un elemento en una fucion.
		if(puntos.size() > 0)
			puntos.add(new GeoPoint(horas.get(0),horas.get(horas.size()-1)));
		
		db.close();
		return puntos;
	}
	
	// Este metodo busca a todos los usuarios encontrados en la base, por su nombre, para poder desplegarlos en un elemento Spinner
	// El cual es una especie de combo box.
	// Para filtrar la busqueda de la ruta por usuario.
	public SimpleCursorAdapter encontrarUsuarios(Context ctx)
	{
		String sql = "SELECT _id,usr_nombre FROM usuarios";
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		cursor = db.rawQuery(sql, null);
		cursor.moveToLast();
		
		// Se crea un SimpleCursorAdapter para el Spinner, el cual se crea a partir de los resultados de la sentencia.
		// Los parametros propios de android.R como layout y text1 son necesarios para poder crear el Spinner con consulta a Base de Datos.
 		SimpleCursorAdapter adaptador = new SimpleCursorAdapter(ctx,android.R.layout.simple_spinner_item, cursor, new String[]{"usr_nombre"},new int[]{android.R.id.text1});
 		adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 		db.close();
 		
 		return adaptador;
	}
	
	// Metodo que transforma la hora en segundos a partir de un String, para poder introducir el tipo de dato en la base, debido a que
	// es de tipo entero.
	public int horaEnSegundos(String h)
	{
		String [] valores = h.split(":");
		int hora, minutos, segundos, tiempoEnSeg;
		
		hora = Integer.parseInt(valores[0]);
		minutos = Integer.parseInt(valores[1]);
		segundos = Integer.parseInt(valores[2]);
		
		hora = hora * 60 * 60;
		minutos = minutos * 60;
		
		tiempoEnSeg = hora + minutos + segundos;
		
		return tiempoEnSeg;
	}
	// Definición de métodos para insertar en la base de datos.
    public void insertarUbicacion(String uSim, String dia, int tiempoEnSeg, double lat, double lng, SQLiteDatabase db)
    {
		ContentValues values = new ContentValues();
		
		values.put("usr_sim", uSim);
		values.put("dia", formatoFecha(dia));
		values.put("hora", tiempoEnSeg);
		values.put("latitud", lat);
		values.put("longitud", lng);
		db.insert("ubicaciones", null,values);
    }
    
    public void insertarUsuario(String uSim,String uNombre,String uPass, SQLiteDatabase db)
    {
		ContentValues values = new ContentValues();
		
		values.put("usr_sim", uSim);
		values.put("usr_nombre", uNombre);
		values.put("usr_password", uPass);	
		
		db.insert("usuarios", null,values);
    }
	
    public static String formatoFecha (String strFecha) {

		// Los tipos Date, pueden ser creados a base de un String.
	    SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
	    Date fecha = null;	
	    try 
	    {

	        fecha = formatoDelTexto.parse(strFecha);

	    } catch (Exception e) {}
	    return formatoDelTexto.format(fecha);
	}
}