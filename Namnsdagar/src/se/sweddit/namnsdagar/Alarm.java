package se.sweddit.namnsdagar;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

public class Alarm extends BroadcastReceiver {
	private static final String SETTINGS_NAME = "appSettings";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// Get wakelock for the service's work
		Log.d("ALARM", "Alarm for NamedayService fired!");
		((PowerManager.WakeLock)NamedayService.getWakeLock(context)).acquire();
		context.startService(new Intent(context, NamedayService.class));
	}
	
	public void SetAlarm(Context context) {
		int hour = 8;
		int minute = 0;
		SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
		settings.getInt("remind_hour", hour);
		settings.getInt("remind_minute", minute);
		
		
		// Time now for settings event time
		Calendar calNow = new GregorianCalendar();
		calNow.setTimeInMillis(System.currentTimeMillis());

		// We want the event 08:00 every day
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR) + 1);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, Alarm.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 101, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
		Log.d("ALARM_SET", "Alarm is now set");
	}
	
	public void RemoveAlarm(Context context) {
		Intent i = new Intent(context, Alarm.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pi);
	}
}
