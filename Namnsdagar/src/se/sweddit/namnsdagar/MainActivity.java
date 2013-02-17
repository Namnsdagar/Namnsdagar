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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MainActivity extends Activity {
	private static final String SETTINGS_NAME = "appSettings";
	private static String todayNames = "";
	
	private ProgressDialog progress;

    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (android.os.Build.VERSION.SDK_INT>10) {
        	ActionBar actionBar = getActionBar();
        	actionBar.hide();
        }
        
        Date todayDate = new Date();
        TextView dateView = (TextView)findViewById(R.id.textView2);
        dateView.setText(DateFormat.getDateInstance(DateFormat.LONG).format(todayDate)+":");
        
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Intent myIntent = new Intent(v.getContext(), Settings.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
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

            Alarm alarm = new Alarm();
            alarm.SetAlarm(this);
        } else {
        	getTodayNames(this);
        }
    }
    
    private void getTodayNames(Context context) {
        DBHelper dbh = new DBHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Calendar rightNow = Calendar.getInstance();
        String namesToday = "";
		try {
			String selectQuery = "SELECT name FROM days WHERE month = " + (rightNow.get(Calendar.MONTH)+1) + " AND day = " + rightNow.get(Calendar.DAY_OF_MONTH) +";";
			Cursor cursor = db.rawQuery(selectQuery, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				do {
					if (namesToday.length()<2) {
						namesToday = cursor.getString(0);
					} else {
						namesToday += ", " + cursor.getString(0);
					}
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("DB_GET","Unable to get selected contacts, "+e.toString());
		}
		todayNames = namesToday;
		MainActivity.this.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	    		TextView nameText = (TextView)findViewById(R.id.textView3);
	    		nameText.setText(todayNames);
	        }
		});
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
		getTodayNames(mContext);
		
   		progress.dismiss();
	   }
	}
}
