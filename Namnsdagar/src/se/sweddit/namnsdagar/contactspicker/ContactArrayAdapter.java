package se.sweddit.namnsdagar.contactspicker;

import java.util.ArrayList;

import se.sweddit.namnsdagar.R;
import android.content.ContentResolver;
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
	private ContactsPickerActivity parentActivity;
	private ArrayList<Contact> contactsInDB;

	public ContactArrayAdapter(ContactsPickerActivity context, ArrayList<Contact> contactsInDB) {
		super(context, R.layout.contactslistrow, R.id.rowTextView);
		this.parentActivity = context;

		this.deselectAll();

		this.contactsInDB = contactsInDB;
		ArrayList<Contact> cList = getContacts();
		for (Contact contact:cList) {
			this.add(contact);
		}


		layoutInflater = LayoutInflater.from(context) ;
	}

	public ArrayList<Contact> getContacts() {
		// http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/

		ArrayList<Contact> list = new ArrayList<Contact>();
		
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

		ContentResolver cr = parentActivity.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, sortOrder);

		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				int contactId = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String contactName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


				if (contactName != null && !containsName(contactName,list) &&
						!containsID(contactId, list) &&
						Misc.isValidName(contactName)) {

					//renames contact if contactName and DBname are different
					if (containsID(contactId,contactsInDB) ){
						Contact dbContact = contactsInDB.get(indexOfID(contactId, contactsInDB));

						String dbName = dbContact.getName();
						if (!dbName.equals(contactName)) {
							contactName = dbName;
						}
					}

					list.add(new Contact(contactId,contactName, containsID(contactId, contactsInDB)));

				}
			}
		}
		
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

	public int indexOfID(int id, ArrayList<Contact> list) {
		int i = 0;

		for (Contact contact : list) {
			if (contact.getId() == id)
				return i;
			i++;
		}

		return -1;
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
			checkBox.setClickable(false);      
		} else {
			ContactViewHolder viewHolder = (ContactViewHolder) convertView.getTag();
			checkBox = viewHolder.getCheckBox() ;
			checkBox.setClickable(false);   

			textView = viewHolder.getTextView() ;
		}

		checkBox.setTag(contact); 
		checkBox.setChecked(contact.getChecked());
		textView.setText(contact.getName());      

		return convertView;
	}
}
