package se.sweddit.namnsdagar;
/*
This file is part of Svenska Namnsdagar

Svenska Namnsdagar is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Svenska Namnsdagar is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Svenska Namnsdagar.  If not, see <http://www.gnu.org/licenses/>.
 */

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