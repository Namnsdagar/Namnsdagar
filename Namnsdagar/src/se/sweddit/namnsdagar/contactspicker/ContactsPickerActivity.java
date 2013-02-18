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
import se.sweddit.namnsdagar.contact.Contact;
import se.sweddit.namnsdagar.contact.ContactList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
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

	DBHelper dbHelper;
	AlertDialog suggestionDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactslist);
	
		dbHelper = new DBHelper(this);
		
		//TODO detta bör givetvis inte göras varje gång 
		acceptableNames = dbHelper.getAllNames();

		listView = (ListView) findViewById( R.id.contactsListView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View item, int position, long id) {
				Contact contact = listAdapter.getItem(position);

				ContactViewHolder viewHolder = (ContactViewHolder) item.getTag();
				checkContactName(contact,viewHolder.getCheckBox());
			}
		});

		ContactList contactsInDB = dbHelper.getContactsInDatabase();
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
	
	public void saveContactsToDB() {
		ContactList selectedContacts = listAdapter.getSelectedContacts();
		dbHelper.insertContacts(selectedContacts);
	}

	@Override
	public void onBackPressed() {
		saveContactsToDB();
		listAdapter.deselectAll();
		super.onBackPressed();
	}
}

