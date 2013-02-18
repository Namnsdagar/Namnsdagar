package se.sweddit.namnsdagar.contact;
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

public class Contact {
	private String name = "";
	private int id;
	private boolean checked = false;

	public Contact() {}

	public Contact(String name) {
		this.name = name ;
	}

	public Contact(int id, String name, boolean checked) {
		this.id = id;
		this.name = name ;
		this.checked = checked ;
	}
	
	public String toString() {
		return name ; 
	}
	
	public void toggle() {
		checked = !checked ;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean getChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
