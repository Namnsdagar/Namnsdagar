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


import se.sweddit.namnsdagar.R;
import se.sweddit.namnsdagar.contact.Contact;
import se.sweddit.namnsdagar.contact.ContactList;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

class ContactArrayAdapter extends ArrayAdapter<Contact> {
	private LayoutInflater layoutInflater;
	private ContactsPickerActivity parentActivity;
	private ContactList contactsInDB;

	public ContactArrayAdapter(ContactsPickerActivity context, ContactList contactsInDB) {
		super(context, R.layout.contactslistrow, R.id.rowTextView);
		this.parentActivity = context;

		this.deselectAll();

		this.contactsInDB = contactsInDB;
		ContactList cList = getContacts();
		for (Contact contact:cList) {
			this.add(contact);
		}


		layoutInflater = LayoutInflater.from(context) ;
	}

	public ContactList getContacts() {
		ContactList list = new ContactList();
		
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		ContentResolver cr = parentActivity.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, sortOrder);

		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				int contactId = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String contactName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


				if (contactName != null && 
						!list.containsName(contactName) &&
						!list.containsID(contactId) &&
						Misc.isValidName(contactName)) {

					//renames contact if contactName and DBname are different
					boolean isIdInDB = contactsInDB.containsID(contactId);
					if (isIdInDB){
						
						if (contactsInDB.indexOfID(contactId) != -1) {
							Contact dbContact = contactsInDB.get(contactsInDB.indexOfID(contactId));
	
							String dbName = dbContact.getName();
							if (!dbName.equals(contactName)) {
								contactName = dbName;
							}
						}
					}

					list.add(new Contact(contactId,contactName,isIdInDB));
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

	public ContactList getSelectedContacts() {
		ContactList selected = new ContactList();

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
