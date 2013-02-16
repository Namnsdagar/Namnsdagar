package se.sweddit.namnsdagar.contactspicker;

public class Misc {
	
	public static String[] ILLEGAL_CHARSEQ = {"@","&",".",";",":","!","/","_","|","~","Google","Android"};
	
	public static boolean isValidName(String name) {
		for (int i = 0; i < ILLEGAL_CHARSEQ.length; i++) {
			if (name.contains(ILLEGAL_CHARSEQ[i]))
				return false;
		}
		
		return name.length() > 2;
	}

}
