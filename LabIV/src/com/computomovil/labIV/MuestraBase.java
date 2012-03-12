package com.computomovil.labIV;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class MuestraBase extends Activity
{
	protected Cursor cursor;
	protected ListAdapter adaptador;
	protected ListView listado;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista);
		
		SQLiteDatabase db = (new DBHelper(this).getWritableDatabase());
		listado = (ListView) findViewById(R.id.ubicaciones);
		
		// Se seleccionan todos los registros de la base de datos ubicaciones
		cursor = db.rawQuery("SELECT * from ubicaciones",null);
		
		// Se inicializa un ListAdapter para posteriormente llenar el ListView con los registros de la Base de Datos.
 		adaptador = new SimpleCursorAdapter(this,R.layout.registro,cursor,
 				new String[]{"usr_sim","dia","hora","latitud","longitud"},
 				new int[]{R.id.usr_Sim,R.id.dia,R.id.tiempoEnSeg,R.id.latitud,R.id.longitud});
		listado.setAdapter(adaptador);
		db.close();
	}
}