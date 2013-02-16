package se.sweddit.namnsdagar.contactspicker;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

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
