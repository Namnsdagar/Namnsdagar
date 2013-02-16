package se.sweddit.namnsdagar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "namesDB";
	private static final int DB_VERSION = 1;
	private static final String CREATE_STRING = "CREATE TABLE" +
			" days (" +
			" month INT," +
			" day INT," +
			" name TEXT);";
	private static final String CREATE_CONTACTS = "CREATE TABLE" +
			" selectedcontacts (" +
			" id_contact INT," +
			" month INT," +
			" day INT," +
			" name TEXT);";
	//private static final String CLEAR_TABLE = "DELETE FROM days;";

	public DBHelper (Context context) {
		super(context,DB_NAME,null,DB_VERSION);
	}
	
	public void insertData(Context context, boolean unofficial) {
		SQLiteDatabase db = this.getWritableDatabase();
		AssetManager am = context.getAssets();
		try {
			String filePath = "days.txt";
			if (unofficial) {
				filePath = "days_unofficial.txt";
			}
			InputStream is = am.open(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			int currentMonth = 0;
			db.execSQL("BEGIN IMMEDIATE TRANSACTION");
			while ((line=br.readLine())!=null) {
				if (line.length()==2) { // New month
					currentMonth++;
				} else { // Insert name data
					String[] lineData = line.split(";");
					db.execSQL("INSERT INTO days (month,day,name) VALUES (" + currentMonth + "," + lineData[0] + ",'" + lineData[1] +"');");
				}
			}
			db.execSQL("COMMIT TRANSACTION");
		} catch (IOException e) {
			Log.e("DB_INSERT","Insert failed: "+e.toString());
		}
		db.close();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_STRING);
		db.execSQL(CREATE_CONTACTS);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		//db.execSQL(CLEAR_TABLE);
	}
}
