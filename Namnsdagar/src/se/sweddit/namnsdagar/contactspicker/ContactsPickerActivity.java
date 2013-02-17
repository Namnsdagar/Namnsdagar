package se.sweddit.namnsdagar.contactspicker;
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

import java.util.ArrayList;

import se.sweddit.namnsdagar.DBHelper;
import se.sweddit.namnsdagar.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ListView;

public class ContactsPickerActivity extends Activity {

	private ListView listView;
	private ContactArrayAdapter listAdapter ;
	private ArrayList<String> acceptableNames;
	
	private Cursor cursor;
	private SQLiteDatabase db;

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

				ContactViewHolder viewHolder = (ContactViewHolder) item.getTag();
				checkContactName(contact,viewHolder.getCheckBox());
			}
		});

		ArrayList<Contact> contactsInDB = getSelected();
		listAdapter = new ContactArrayAdapter(this,contactsInDB);

		listView.setAdapter(listAdapter);      
	}

	public void checkContactName(Contact contact,CheckBox cb) {
		if (!contact.getChecked() &&
				!acceptableNames.contains(Misc.getFirstName(contact.getName()))) {
			showSuggestionDialog(contact);
		} else {
			contact.toggle();
			cb.setChecked(contact.getChecked());
		}
	}

	public void showSuggestionDialog(final Contact c) {
		AlertDialog alertDialog = null;
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AutoCompleteTextView input = new AutoCompleteTextView(this);

		ArrayAdapter<String> suggestionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,acceptableNames);
		input.setAdapter(suggestionAdapter);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setThreshold(1);
		input.setDropDownHeight(LayoutParams.WRAP_CONTENT);
		input.setCompletionHint("Välj ett namn ur listan");

		alertDialog = builder.create();
		alertDialog.setTitle("Kunde inte hitta " + Misc.getFirstName(c.getName()));

		alertDialog.setIcon(R.drawable.icon);
		alertDialog.setView(input);
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getEditableText().toString();

				if (acceptableNames.contains(value)) {
					c.setName(value);
					c.setChecked(true);
				}
			}
		});
		
		alertDialog.show();
		alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	private ArrayList<String> getAllNames() {
		ArrayList<String> namelist = new ArrayList<String>();

		DBHelper dbh = new DBHelper(this);
		db = dbh.getReadableDatabase();

		String selectQuery = "SELECT * FROM days;";
		cursor = db.rawQuery(selectQuery, null);
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
		db = dbh.getReadableDatabase();

		String selectQuery = "SELECT * FROM selectedcontacts;";
		cursor = db.rawQuery(selectQuery, null);
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
	
	public void saveContactsToDB() {
		ArrayList<Contact> selectedContacts = listAdapter.getSelectedContacts();

		try {
			DBHelper dbh = new DBHelper(this);
			db = dbh.getWritableDatabase();
			db.execSQL("DELETE FROM selectedcontacts;");
			db.execSQL("BEGIN IMMEDIATE TRANSACTION");
			for (Contact c : selectedContacts) {
				int month=1;
				int day=1;
				try {
					String[] nameArr = c.getName().split(" ");
					String selectQuery = "SELECT month,day FROM days WHERE name = '"+nameArr[0]+"';";
					cursor = db.rawQuery(selectQuery, null);
					
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
	}


	@Override
	public void onBackPressed() {
		saveContactsToDB();
		listAdapter.deselectAll();
		super.onBackPressed();
	}
}

