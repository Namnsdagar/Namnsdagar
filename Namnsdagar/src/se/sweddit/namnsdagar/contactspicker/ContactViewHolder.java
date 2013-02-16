package  se.sweddit.namnsdagar.contactspicker;

import android.widget.CheckBox;
import android.widget.TextView;

class ContactViewHolder {
	private CheckBox checkBox;
	private TextView textView;

	public ContactViewHolder() {}

	public ContactViewHolder( TextView textView, CheckBox checkBox ) {
		this.checkBox = checkBox;
		this.textView = textView;
	}
	
	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}
	
	public TextView getTextView() {
		return textView;
	}
	
	public void setTextView(TextView textView) {
		this.textView = textView;
	}    
}