package se.sweddit.namnsdagar.contactspicker;

import java.util.ArrayList;

import se.sweddit.namnsdagar.R;

import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

class ContactArrayAdapter extends ArrayAdapter<Contact> {
	private LayoutInflater layoutInflater;
	private Activity parentActivity;
	private ArrayList<Contact> contactsInDB;

	public ContactArrayAdapter(Activity context, ArrayList<Contact> contactsInDB) {
		super(context, R.layout.contactslistrow, R.id.rowTextView);
		this.parentActivity = context;
		
		this.deselectAll();

		this.contactsInDB = contactsInDB;
		this.addAll(getContacts());
		
		
		layoutInflater = LayoutInflater.from(context) ;
	}

	@SuppressWarnings("deprecation")
	public ArrayList<Contact> getContacts() {
		// http://app-solut.com/blog/2011/03/working-with-the-contactscontract-to-query-contacts-in-android/

		ArrayList<Contact> list = new ArrayList<Contact>();

		final String[] projection = new String[] {
				RawContacts.CONTACT_ID,
				RawContacts.DELETED,
				RawContacts.DISPLAY_NAME_PRIMARY,
				RawContacts.DISPLAY_NAME_SOURCE
		};

		
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		
		final Cursor rawContacts = parentActivity.managedQuery(
				RawContacts.CONTENT_URI,
				projection,null,null,sortOrder);


		final int contactIdColumnIndex = rawContacts.getColumnIndex(RawContacts.CONTACT_ID);
		final int deletedColumnIndex = rawContacts.getColumnIndex(RawContacts.DELETED);
		final int nameColumnIndex = rawContacts.getColumnIndex(RawContacts.DISPLAY_NAME_PRIMARY);
		final int nameSrcColumnIndex = rawContacts.getColumnIndex(RawContacts.DISPLAY_NAME_SOURCE);

		if(rawContacts.moveToFirst()) {
			while(!rawContacts.isAfterLast()) {
				final int contactId = rawContacts.getInt(contactIdColumnIndex);
				final String contactName = rawContacts.getString(nameColumnIndex);
				final int contactNameSrc = rawContacts.getInt(nameSrcColumnIndex);
				final boolean deleted = (rawContacts.getInt(deletedColumnIndex) == 1);

				if(!deleted && 
						contactNameSrc == ContactsContract.DisplayNameSources.STRUCTURED_NAME &&
						!containsName(contactName,list) &&
						!containsID(contactId, list) &&
						Misc.isValidName(contactName)) {
					
					list.add(new Contact(contactId,contactName, containsID(contactId, contactsInDB)));
				}
				
				rawContacts.moveToNext();
			}
		}

		rawContacts.close();
		return list;
	}
	
	public void deselectAll() {
		for (int i = 0; i < getCount(); i++) {
			getItem(i).setChecked(false);
		}
	}

	public boolean containsName(String name, ArrayList<Contact> list) {
		for (Contact contact : list) {
			if (contact.getName().equalsIgnoreCase(name))
				return true;
		}

		return false;
	}
	
	public boolean containsID(int id, ArrayList<Contact> list) {
		for (Contact contact : list) {
			if (contact.getId() == id)
				return true;
		}

		return false;
	}

	public ArrayList<Contact> getSelectedContacts() {
		ArrayList<Contact> selected = new ArrayList<Contact>();

		for (int i = 0; i < getCount(); i++) {
			if (getItem(i).getChecked())
				selected.add(this.getItem(i));
		}

		return selected;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Contact contact = (Contact) this.getItem(position); 
		CheckBox checkBox; 
		TextView textView; 


		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.contactslistrow, null);

			textView = (TextView) convertView.findViewById(R.id.rowTextView);
			checkBox = (CheckBox) convertView.findViewById(R.id.rowCheckBox);

			convertView.setTag( new ContactViewHolder(textView,checkBox));
			checkBox.setOnClickListener( new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;

					Contact c = (Contact) cb.getTag();
					c.setChecked(cb.isChecked());
				}
			});        
		} else {
			ContactViewHolder viewHolder = (ContactViewHolder) convertView.getTag();
			checkBox = viewHolder.getCheckBox() ;
			textView = viewHolder.getTextView() ;
		}

		checkBox.setTag(contact); 
		checkBox.setChecked(contact.getChecked());
		textView.setText(contact.getName());      

		return convertView;
	}
}
