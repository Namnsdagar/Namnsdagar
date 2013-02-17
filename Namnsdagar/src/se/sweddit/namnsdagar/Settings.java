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

import se.sweddit.namnsdagar.contactspicker.ContactsPickerActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Settings extends Activity {
	private static final String SETTINGS_NAME = "appSettings";
	private TimePickerDialog tDialog;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
        if (android.os.Build.VERSION.SDK_INT>10) {
        	ActionBar actionBar = getActionBar();
        	actionBar.hide();
        }
        
        TextView contactText = (TextView) findViewById(R.id.textView3);
        contactText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Intent myIntent = new Intent(v.getContext(), ContactsPickerActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        TextView timeLink = (TextView) findViewById(R.id.textView2);
        timeLink.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences settings = getSharedPreferences(SETTINGS_NAME, 0);
				int setHour = settings.getInt("remind_hour", 8);
				int setMinute = settings.getInt("remind_minute", 0);
				tDialog = new TimePickerDialog(Settings.this, new TimePickHandler(),
						setHour,setMinute, true);
				tDialog.show();
			}
		});
        
        CheckBox modeSelect = (CheckBox) findViewById(R.id.checkBox1);
        modeSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				// TODO Auto-generated method stub
	        	SharedPreferences settings = getSharedPreferences(SETTINGS_NAME, 0);
	        	Log.i("SET_MODE","Previous mode: "+settings.getBoolean("remind_mode", false)+", New mode: "+isChecked);
	        	SharedPreferences.Editor editor = settings.edit();
	        	editor.putBoolean("remind_mode", isChecked);
	        	editor.commit();
			}
        });
        
        CheckBox notificationSelect = (CheckBox) findViewById(R.id.checkBoxNotification);
        notificationSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
	        	SharedPreferences settings = getSharedPreferences(SETTINGS_NAME, 0);
	        	Log.i("SET_NOTIFICATION","Previous notify mode: "+settings.getBoolean("notification_mode", true)+", New notify mode: "+isChecked);
	        	SharedPreferences.Editor editor = settings.edit();
	        	editor.putBoolean("notification_mode", isChecked);
	        	editor.commit();
	        	
	        	Alarm alarm = new Alarm();
	        	if (isChecked)
	        		alarm.SetAlarm(getApplicationContext());
	        	else
	        		alarm.RemoveAlarm(getApplicationContext());
			}
        });

    	SharedPreferences settings = getSharedPreferences(SETTINGS_NAME, 0);
    	int hourOfDay = settings.getInt("remind_hour", 8);
    	int minute = settings.getInt("remind_minute", 0);
    	boolean mode = settings.getBoolean("remind_mode", false);
    	boolean notification_mode = settings.getBoolean("notification_mode", true);
    	String displayMinute = minute+"";
    	if (minute<10) displayMinute = "0"+minute;
    	timeLink.setText(getResources().getString(R.string.set_time)+" ("+hourOfDay+":"+displayMinute+")");
    	modeSelect.setChecked(mode);
    	notificationSelect.setChecked(notification_mode);

    	int selCount = getSelectedCount(this);
    	contactText.setText(getResources().getString(R.string.choose_contacts)+" ("+selCount+")");
	}
    
    @Override
    public void onRestart() {
    	int selCount = getSelectedCount(this);
        TextView contactText = (TextView) findViewById(R.id.textView3);
        contactText.setText(getResources().getString(R.string.choose_contacts)+" ("+selCount+")");
    	super.onRestart();
    }
    
    private int getSelectedCount(Context context) {
    	int count = 0;
    	
    	DBHelper dbh = new DBHelper(context);
    	SQLiteDatabase db = dbh.getReadableDatabase();
    	
		try {
			String selectQuery = "SELECT COUNT(id_contact) AS count FROM selectedcontacts;";
			Cursor cursor = db.rawQuery(selectQuery, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				count = cursor.getInt(0);
			}
			db.close();
		} catch (Exception e) {
			Log.e("DB_GET","Unable to get selected contacts, "+e.toString());
		}
    	
    	return count;
    }
    
    private class TimePickHandler implements OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        	SharedPreferences settings = getSharedPreferences(SETTINGS_NAME, 0);
        	SharedPreferences.Editor editor = settings.edit();
        	editor.putInt("remind_hour", hourOfDay);
        	editor.putInt("remind_minute", minute);
        	editor.commit();
        	TextView timeText = (TextView) findViewById(R.id.textView2);
        	String displayMinute = minute+"";
        	if (minute<10) displayMinute = "0"+minute;
        	timeText.setText(getResources().getString(R.string.set_time)+" ("+hourOfDay+":"+displayMinute+")");
            // Start Alarm for nameday checks
            Alarm alarm = new Alarm();
            alarm.SetAlarm(Settings.this);
            tDialog.hide();
            Log.i("SET_TIME","Hour: "+hourOfDay+", Minute: "+minute);
        }
    }

}
