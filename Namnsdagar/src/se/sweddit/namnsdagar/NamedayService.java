package se.sweddit.namnsdagar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.util.Log;

public class NamedayService extends IntentService {

	private SQLiteDatabase db;
	private static PowerManager.WakeLock wakeLock = null;
	public static final String LOCK = "se.sweddit.namnsdagar.NamedayService";

	public NamedayService() {
		super("NamedayService");
	}
	
	protected void worker(Intent intent) {
		// Check namedays here...
		Log.d("NamedayService", "NamedayService worker method running...");
		checkContactsNameday(0); // TODO: Dayoffset for notifications earlier. 
	}
	
	synchronized static PowerManager.WakeLock getWakeLock(Context context) {
		if (wakeLock == null) {
			// Requires android.permission.WAKE_LOCK permission
			PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK);
		}
		return wakeLock;
	}
	
	@Override
	final protected void onHandleIntent(Intent intent) {
		try {
			Log.d("NamedayService", "Recieved intent");
			worker(intent);
		}
		finally {
			if (db.isOpen())
				db.close();
		}
		
		// Release wake lock when we're done.
		try {
			getWakeLock(this).release();
		} catch (Exception e) {}
	}
	
	
	public void checkContactsNameday(int dayOffset) {
		
		// Today it is...
		Calendar calNow = new GregorianCalendar();
		calNow.setTimeInMillis(System.currentTimeMillis());
		
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR) + dayOffset); // Vad händer om det blir mer än antal dagar på ett år?
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
				
		DBHelper dbh = new DBHelper(this);
    	db = dbh.getReadableDatabase();

    	// Hämta namnsdagar för den valda dagen
		String query = "SELECT name FROM days WHERE month='" + month + "' AND day='" + day + "'";
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		List<String> namedayNames = new ArrayList<String>();
		if (cursor.getCount() > 0) {
			do {
				namedayNames.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		
		// Välj de kontakter som matchar namnen för dagens namnsdag
		query = "SELECT * FROM selectedcontacts"; //WHERE "+ names;
		cursor = db.rawQuery(query, null);
		HashSet<String> names = new HashSet<String>();
		if (cursor.getCount() > 0) {
			// Get first name
			names.add(((String[])cursor.getString(1).split(" ", 1))[0]);
			
			for (String s : names) {
				Log.d("NAME", s);
			}
		}
		
		for (String s : namedayNames) {
			if (names.contains(s))
				; // Send notification intent for this contact
		}
				
		
		db.close();
	}
}
