package se.sweddit.namnsdagar;

import se.sweddit.namnsdagar.contactspicker.ContactsPickerActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
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
        
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
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
    	timeLink.setText(getResources().getString(R.string.set_time)+" ("+hourOfDay+":"+displayMinute+")");
    	modeSelect.setChecked(mode);
        
    	// 1. Instantiate an AlertDialog.Builder with its constructor
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	// 2. Chain together various setter methods to set the dialog characteristics
    	builder.setMessage(R.string.dialog_message)
    	       .setTitle(R.string.dialog_title);

		builder.setPositiveButton(R.string.dialog_official, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               // User clicked OK button
        	   loadData(false);
           }
       });
		builder.setNegativeButton(R.string.dialog_unofficial, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               // User cancelled the dialog
        	   loadData(true);
           }
       });

    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	
    	
    	
        if (isFirstLaunch()) {
        	dialog.show();
        } else {
        	dumpActive(this);
        }
    }
 
    private void loadData(boolean unofficial) {
    	progress = ProgressDialog.show(MainActivity.this,"","Förbereder...");
    	Runnable r = new dataThread(unofficial,MainActivity.this);
    	new Thread(r).start();
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
    
    private void dumpActive(Context context) {
    	DBHelper dbh = new DBHelper(context);
    	SQLiteDatabase db = dbh.getReadableDatabase();

		String selectQuery = "SELECT * FROM selectedcontacts;";
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		do {
			Log.d("DB_DUMP",cursor.getInt(0)+", "+cursor.getString(1));
		} while (cursor.moveToNext());
		db.close();
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
    public class dataThread implements Runnable {
    	private boolean unofficial;
    	private Context mContext;

	   public dataThread(boolean use_Unofficial, Context context) {
	       // store parameter for later user
		   unofficial=use_Unofficial;
		   mContext=context;
	   }

	   public void run() {
       	DBHelper dbh = new DBHelper(mContext);
       	dbh.insertData(mContext, unofficial);
   		progress.dismiss();
	   }
	}
}
