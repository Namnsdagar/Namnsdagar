package se.sweddit.namnsdagar;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.util.Log;

public class NamedayService extends IntentService {

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
			worker(intent);
		}
		finally {
			// Release wake lock when we're done.
			getWakeLock(this).release();
		}
	}
	
	
	public void checkContactsNameday(int dayOffset) {
		
		// Today it is...
		Calendar calNow = new GregorianCalendar();
		calNow.setTimeInMillis(System.currentTimeMillis());
		
		Calendar cal = new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.add(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR) + dayOffset); // Vad händer om det blir mer än antal dagar på ett år?
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_WEEK);
				
		DBHelper dbh = new DBHelper(this);
    	SQLiteDatabase db = dbh.getReadableDatabase();

    	// Hämta namnsdagar för den valda dagen
		String query = "SELECT * FROM days WHERE month='" + month + "' AND day='" + day + "'";
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		String names = "";
		while (!cursor.isLast()) {
			names += "name='" + cursor.getString(1) + "' OR";
			cursor.moveToNext();
		}
		names += "name='" + cursor.getString(1) + "'";
		
		// Välj de kontakter som matchar namnen för dagens namnsdag
		query = "SELECT * FROM selectedcontacts WHERE "+ names +" COLLATE Latin1_General_bin";
		cursor = db.rawQuery(query, null);

		if (cursor.getCount() > 0) {
			// Some contact have their nameday for the selected day.
			// Send notification
		}
		
		cursor.moveToFirst();
		do {
			Log.d("NamedayForContact: ", cursor.getString(1));
		} while (cursor.moveToNext());
		db.close();
	}
}
