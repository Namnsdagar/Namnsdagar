package se.sweddit.namnsdagar.contact;

import java.util.ArrayList;

public class ContactList extends ArrayList<Contact> {

	private static final long serialVersionUID = 1L;

	public ContactList() {
		super();
	}

	public boolean containsName(String name) {
		for (Contact contact : this) {
			if (contact.getName().equalsIgnoreCase(name))
				return true;
		}

		return false;
	}

	public boolean containsID(int id) {
		for (Contact contact : this) {
			if (contact.getId() == id)
				return true;
		}

		return false;
	}

	public int indexOfID(int id) {
		int i = 0;

		for (Contact contact : this) {
			if (contact.getId() == id)
				return i;
			i++;
		}

		return -1;
	}
}
