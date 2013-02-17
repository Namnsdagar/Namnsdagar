package se.sweddit.namnsdagar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.util.Log;

public class NamedayService extends IntentService {

	private SQLiteDatabase db;
	private static PowerManager.WakeLock wakeLock = null;
	public static final String LOCK = "se.sweddit.namnsdagar.NamedayService";
	private static final String SETTINGS_NAME = "appSettings";

	public NamedayService() {
		super("NamedayService");
	}

	protected void worker(Intent intent) {
		// Check namedays here...
		Log.d("NamedayService", "NamedayService worker method running...");
		checkContactsNameday();
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


	public void checkContactsNameday() {

		// Notify day before actual name day?
		int dayOffset = 0;
		SharedPreferences settings = this.getSharedPreferences(SETTINGS_NAME, 0);
		boolean remind_mode = settings.getBoolean("remind_mode", false);
		if (remind_mode)
			dayOffset = 1;

		// Today it is...
		Calendar calNow = new GregorianCalendar();
		calNow.setTimeInMillis(System.currentTimeMillis());

		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR));
		cal.roll(Calendar.DAY_OF_YEAR, dayOffset);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		DBHelper dbh = new DBHelper(this);
    	db = dbh.getReadableDatabase();		

		// Välj de kontakter som matchar namnen för dagens namnsdag
		String query = "SELECT * FROM selectedcontacts WHERE month='" + month + "' AND day='" + day + "'";
		Cursor cursor = db.rawQuery(query, null);
		cursor = db.rawQuery(query, null);
		String names = "";
		if (cursor.getCount() > 1) {
			cursor.moveToFirst();
			do {
				names += cursor.getString(1) + " & ";
				cursor.moveToNext();
			} while (!cursor.isLast());
			names += cursor.getString(1);
			new NamedayNotification((Context)this, names, remind_mode);
			Log.d("NamedayService", "More than one contacts name day matched selected day ("+names+"). Notification sent.");
		}
		else if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			names = cursor.getString(1);
			new NamedayNotification((Context)this, names, remind_mode);
			Log.d("NamedayService", "One contact's name day matched day ("+names+"). Notification sent.");
		}
		else
			Log.d("NamedayService", "No contacts name day.");
		db.close();		
	}
}