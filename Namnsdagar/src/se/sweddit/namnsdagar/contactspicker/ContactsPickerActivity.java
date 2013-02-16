package se.sweddit.namnsdagar.contactspicker;

import java.util.ArrayList;

import se.sweddit.namnsdagar.DBHelper;
import se.sweddit.namnsdagar.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsPickerActivity extends Activity {

	private ListView listView;
	private ContactArrayAdapter listAdapter ;
	private ArrayList<String> acceptableNames;

	AlertDialog suggestionDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactslist);

		//TODO detta bör givetvis inte göras varje gång 
		acceptableNames = getAllNames();



		listView = (ListView) findViewById( R.id.contactsListView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View item, int position, long id) {
				Contact contact = listAdapter.getItem(position);


				if (!acceptableNames.contains(Misc.getFirstName(contact.getName()))) {
					showSuggestionDialog(contact);
				} else {
					contact.toggle();


					ContactViewHolder viewHolder = (ContactViewHolder) item.getTag();
					viewHolder.getCheckBox().setChecked(contact.getChecked());
				}
			}
		});

		ArrayList<Contact> contactsInDB = getSelected();
		listAdapter = new ContactArrayAdapter(this,contactsInDB);

		listView.setAdapter(listAdapter);      
	}

	public void showSuggestionDialog(final Contact c) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		final AutoCompleteTextView input = new AutoCompleteTextView(this);

		ArrayAdapter<String> suggestionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,acceptableNames);
		input.setAdapter(suggestionAdapter);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getEditableText().toString();

				//vet inte om detta funkar som det skall
				//c.setName(value);
				
				//gör något med det nya namnet
				//släng in i DB kanske?
				
				c.setChecked(true);
			}
		});



		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle("Kunde inte hitta " + Misc.getFirstName(c.getName()));

		alertDialog.setIcon(R.drawable.icon);
		alertDialog.setView(input);
		alertDialog.show();
	}

	private ArrayList<String> getAllNames() {
		ArrayList<String> namelist = new ArrayList<String>();

		DBHelper dbh = new DBHelper(this);
		SQLiteDatabase db = dbh.getReadableDatabase();

		String selectQuery = "SELECT * FROM days;";
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();

		if (!cursor.isAfterLast()) {
			do {
				String s = cursor.getString(2);
				namelist.add(s);
			} while (cursor.moveToNext());
		}
		db.close();

		return namelist;
	}

	private ArrayList<Contact> getSelected() {
		ArrayList<Contact> contactList = new ArrayList<Contact>();

		Contact contactItem;
		DBHelper dbh = new DBHelper(this);
		SQLiteDatabase db = dbh.getReadableDatabase();

		String selectQuery = "SELECT * FROM selectedcontacts;";
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				contactItem = new Contact();
				contactItem.setId(cursor.getInt(0));
				contactItem.setName(cursor.getString(1));
				contactItem.setChecked(true);
				contactList.add(contactItem);
			} while (cursor.moveToNext());
		}
		db.close();

		return contactList;
	}

	@Override
	public void onBackPressed() {
		//selection complete
		ArrayList<Contact> selectedContacts = listAdapter.getSelectedContacts();
		try {
			DBHelper dbh = new DBHelper(this);
			SQLiteDatabase db = dbh.getWritableDatabase();
			db.execSQL("DELETE FROM selectedcontacts;");
			db.execSQL("BEGIN IMMEDIATE TRANSACTION");
			for (Contact c : selectedContacts) {
				int month=1;
				int day=1;
	    		try {
	    			String[] nameArr = c.getName().split(" ");
	    			String selectQuery = "SELECT month,day FROM days WHERE name = '"+nameArr[0]+"';";
	    			Cursor cursor = db.rawQuery(selectQuery, null);
	    			cursor.moveToFirst();
	    			if (!cursor.isAfterLast()) {
	    				month = cursor.getInt(0);
	    				day = cursor.getInt(1);
	    			}
	    		} catch (Exception e) {
	    			Log.e("DB_GET","Unable to get selected contacts, "+e.toString());
	    		}
				db.execSQL("INSERT INTO selectedcontacts (id_contact,name,month,day) VALUES (" + c.getId() + ",'" + c.getName() + "',"+month+","+day+");");
			}
			db.execSQL("COMMIT TRANSACTION");
			db.close();
		} catch (Exception e) {
			Log.e("DB_INSERT","Failed to update selected contacts, "+e.toString());
		}

		//do something with selected contacts...
		Toast toast = Toast.makeText(getApplicationContext(), "Selected " + selectedContacts.size() + " contacts!", Toast.LENGTH_SHORT);
		toast.show();

		listAdapter.deselectAll();
		super.onBackPressed();
	}
}

