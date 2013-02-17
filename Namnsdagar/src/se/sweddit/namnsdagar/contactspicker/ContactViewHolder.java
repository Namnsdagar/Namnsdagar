package  se.sweddit.namnsdagar.contactspicker;
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