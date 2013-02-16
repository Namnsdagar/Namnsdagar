package se.sweddit.namnsdagar.contactspicker;

public class Misc {
	
	public static String[] ILLEGAL_CHARS = {"@","&",".",";",":","!","/"};
	
	public static boolean isValidName(String name) {
		for (int i = 0; i < ILLEGAL_CHARS.length; i++) {
			if (name.contains(ILLEGAL_CHARS[i]))
				return false;
		}
		
		return name.length() > 2;
	}

}
