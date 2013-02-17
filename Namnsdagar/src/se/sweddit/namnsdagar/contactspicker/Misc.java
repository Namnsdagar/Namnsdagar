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

public class Misc {
	
	public static String[] ILLEGAL_CHARSEQ = {"@","&",".",";",":","!","/","_","|","~","Google","Android"};
	
	public static boolean isValidName(String name) {
		for (int i = 0; i < ILLEGAL_CHARSEQ.length; i++) {
			if (name.contains(ILLEGAL_CHARSEQ[i]))
				return false;
		}
		
		return name.length() > 2;
	}
	
	public static String getFirstName(String name) {
		//förnamn och efternamn
		String[] parts = name.split(" ");
		
		//om bara förnamn testa efter dubbelnamn
		if (parts.length == 1) {
			parts = name.split("-");
		}
		
		
		return parts[0].trim();
	}
}
