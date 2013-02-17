package se.sweddit.namnsdagar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NamedayNotification
{
	public NamedayNotification(Context context, String names, boolean remind_mode) {
		
		String text = "";
		if (!remind_mode)
			text = "har namnsdag idag!";
		else
			text = "har namnsdag imorgon!";
		
		NotificationCompat.Builder ncB = new NotificationCompat.Builder(context)
												.setAutoCancel(true)
		        								.setSmallIcon(R.drawable.notification)
		        								.setContentTitle(names)
		        								.setContentText(text)
		        								.setLights(Color.rgb(255,192,203), 1000, 500);
		
		// Activity to start when notification is clicked...
		Intent i = new Intent(context, MainActivity.class); // Create result page for notification?

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(i);
		PendingIntent pi = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		ncB.setContentIntent(pi);
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(1, ncB.build());
	}
}