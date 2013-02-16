package se.sweddit.namnsdagar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends Activity {
	private static final String SETTINGS_NAME = "appSettings";
	
	private ProgressDialog progress;
	private TimePickerDialog tDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView timeLink = (TextView) findViewById(R.id.textView2);
        timeLink.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences settings = getSharedPreferences(SETTINGS_NAME, 0);
				int setHour = settings.getInt("remind_hour", 8);
				int setMinute = settings.getInt("remind_minute", 0);
				tDialog = new TimePickerDialog(MainActivity.this, new TimePickHandler(),
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
    	TextView timeText = (TextView)findViewById(R.id.textView2);
    	timeText.setText(getResources().getString(R.string.set_time)+" ("+hourOfDay+":"+displayMinute+")");
    	CheckBox modeBox = (CheckBox)findViewById(R.id.checkBox1);
    	modeBox.setChecked(mode);
        
        if (isFirstLaunch()) {
        	progress = ProgressDialog.show(this, "",
                    "Förbereder...");
        	new Thread() {
        		public void run() {
                	DBHelper dbh = new DBHelper(MainActivity.this);
                	dbh.insertData(MainActivity.this);
            		progress.dismiss();
        		}
        		
        	}.start();
        }
    }
 
    
    private boolean isFirstLaunch() {
    	SharedPreferences settings = getSharedPreferences(SETTINGS_NAME, 0);
    	boolean isFirst = settings.getBoolean("isFirstLaunch", true);
    	if (isFirst) {
	    	SharedPreferences.Editor editor = settings.edit();
	    	editor.putBoolean("isFirstLaunch", false);
	    	editor.commit();
    	}
    	return isFirst;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
            tDialog.hide();
            Log.i("SET_TIME","Hour: "+hourOfDay+", Minute: "+minute);
        }

    }
}
