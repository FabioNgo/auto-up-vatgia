/**
 * 
 */
package com.example.autoupvatgia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Fabio Ngo
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	static String DB_NAME = "vatgia.sqlite";

	public SQLiteDatabase myDatabase;
	Context myContext;

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 * @throws IOException
	 */

	public DatabaseHelper(Context context) throws IOException {

		super(context, context.getExternalFilesDir(
				context.ACCESSIBILITY_SERVICE).toString(), null, 1);

		this.myContext = context;
		myDatabase = SQLiteDatabase.openDatabase(
				context.getExternalFilesDir(context.ACCESSIBILITY_SERVICE)
						.toString() + "/" + DB_NAME, null,
				SQLiteDatabase.OPEN_READONLY);
		// TODO Auto-generated constructor stub
	}

	
	
	/**
	 * 
	 * @return the array of all characters in database
	 */
	public ArrayList<RaoVatItem> getAllItems() {
		ArrayList<RaoVatItem> items = new ArrayList<RaoVatItem>();
		String selectQuery = "SELECT  * FROM " + "RaoVatID;";
		Cursor c = myDatabase.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			int a = 0;
			do {
				RaoVatItem item = new RaoVatItem(
						c.getInt(c.getColumnIndex("ID")),
						c.getString(c.getColumnIndex("Name")));
				// adding to tags list
				items.add(item);
				//Log.d("character", chars.get(chars.size()-1).getText());
			} while (c.moveToNext());
			c.close();
		}
		return items;
	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	

}
