package se.sweddit.namnsdagar;

import se.sweddit.namnsdagar.contactspicker.ContactsPickerActivity;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Settings extends Activity {
	private static final String SETTINGS_NAME = "appSettings";
	private TimePickerDialog tDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

        
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
	        	SharedPreferences.Editor editor = settings.edit();
	        	editor.putBoolean("remind_mode", isChecked);
	        	editor.commit();
	        	Log.i("SET_MODE","Mode: "+isChecked);
			}
        });

    	SharedPreferences settings = getSharedPreferences(SETTINGS_NAME, 0);
    	int hourOfDay = settings.getInt("remind_hour", 8);
    	int minute = settings.getInt("remind_minute", 0);
    	boolean mode = settings.getBoolean("remind_mode", false);
    	String displayMinute = minute+"";
    	if (minute<10) displayMinute = "0"+minute;
    	timeLink.setText(getResources().getString(R.string.set_time)+" ("+hourOfDay+":"+displayMinute+")");
    	modeSelect.setChecked(mode);

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
