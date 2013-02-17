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
		Log.d("ALARM", "Recieved intent: " + intent.toString());
		// Set alarm on boot
		if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.d("ALARM", "Alarm for ACTION_BOOT_COMPLETED, setting alarm.");
			SetAlarm(context);
			return;
		}
		// Get wakelock for the service's work
		Log.d("ALARM", "Alarm for NamedayService fired!");
		((PowerManager.WakeLock)NamedayService.getWakeLock(context)).acquire();
		context.startService(new Intent(context, NamedayService.class));
	}
	
	public void SetAlarm(Context context) {
		SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
		int hour = settings.getInt("remind_hour", 8);
		int minute = settings.getInt("remind_minute", 0);
		
		
		// Time now for settings event time
		Calendar calNow = new GregorianCalendar();
		calNow.setTimeInMillis(System.currentTimeMillis());

		// We want the event 08:00 every day
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR));
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		// Set the alarm to next day if desired time have passed
		if (calNow.getTime().after(cal.getTime())) {
			Log.d("ALARM_SET", "Setting alarm to fire tomorrow...");
			cal.roll(Calendar.DAY_OF_YEAR, 1);
		}

		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent("se.sweddit.namnsdagar.ALARM");
		PendingIntent pi = PendingIntent.getBroadcast(context, 101, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
		Log.d("ALARM_SET", "Alarm set at Hour:" + hour + " Minute: " + minute);
	}
	
	public void RemoveAlarm(Context context) {
		Intent i = new Intent(context, Alarm.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 101, i, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pi);
		Log.d("ALARM_SET", "Alarm stopped!");
	}
}
