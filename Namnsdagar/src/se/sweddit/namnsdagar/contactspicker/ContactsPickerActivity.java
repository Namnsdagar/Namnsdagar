package se.sweddit.namnsdagar.contactspicker;

import java.util.ArrayList;

import se.sweddit.namnsdagar.R;

import android.app.Activity;
import android.os.Bundle;
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
				viewHolder.getCheckBox().setChecked(contact.isChecked());
			}
		});

		
		listAdapter = new ContactArrayAdapter(this);
		listView.setAdapter(listAdapter);      
	}
	
	
	
	@Override
	public void onBackPressed() {
		//selection complete
		ArrayList<Contact> selectedContacts = listAdapter.getSelectedContacts();
		
		//do something with selected contacts...
		Toast toast = Toast.makeText(getApplicationContext(), "Selected " + selectedContacts.size() + " contacts!", Toast.LENGTH_LONG);
		toast.show();
		
		super.onBackPressed();
	}
}