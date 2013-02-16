package se.sweddit.namnsdagar.contactspicker;

import java.util.ArrayList;

import se.sweddit.namnsdagar.DBHelper;
import se.sweddit.namnsdagar.R;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsPickerActivity extends Activity {

	private ListView listView;
	private ContactArrayAdapter listAdapter ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactslist);

		
		listView = (ListView) findViewById( R.id.contactsListView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick( AdapterView<?> parent, View item, int position, long id) {
				Contact contact = listAdapter.getItem( position );
				contact.toggle();
				
				ContactViewHolder viewHolder = (ContactViewHolder) item.getTag();
				viewHolder.getCheckBox().setChecked(contact.getChecked());
			}
		});

		ArrayList<Contact> contactsInDB = getSelected();
		listAdapter = new ContactArrayAdapter(this,contactsInDB);
		
		listView.setAdapter(listAdapter);      
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
				db.execSQL("INSERT INTO selectedcontacts (id_contact,name) VALUES (" + c.getId() + ",'" + c.getName() + "');");
			}
			db.execSQL("COMMIT TRANSACTION");
		} catch (Exception e) {
			Log.e("DB_INSERT","Failed to update selected contacts, "+e.toString());
		}
		
		//do something with selected contacts...
		Toast toast = Toast.makeText(getApplicationContext(), "Selected " + selectedContacts.size() + " contacts!", Toast.LENGTH_LONG);
		toast.show();
		
		listAdapter.deselectAll();
		super.onBackPressed();
	}
}