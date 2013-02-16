package se.sweddit.namnsdagar.contactspicker;

class Contact {
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
