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
	
    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
	
	public static int getLevenshtein(String n1,String n2) {
		//From http://en.wikibooks.org/wiki/Algorithm_implementation/Strings/Levenshtein_distance#Java
		
		String str1 = n1.toLowerCase();
		String str2 = n2.toLowerCase();
		

        int[][] distance = new int[str1.length() + 1][str2.length() + 1];
        
        

        for (int i = 0; i <= str1.length(); i++)
                distance[i][0] = i;
        
        for (int j = 1; j <= str2.length(); j++)
                distance[0][j] = j;

        for (int i = 1; i <= str1.length(); i++)  {
                for (int j = 1; j <= str2.length(); j++) {
                        distance[i][j] = minimum(
                                        distance[i - 1][j] + 1,
                                        distance[i][j - 1] + 1,
                                        distance[i - 1][j - 1]
                                                        + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                                                        : 1));
                }
        }

        return distance[str1.length()][str2.length()];
	}
	
	public static String[] getSimilarNames(String name, ArrayList<String> names, int numSuggestions) {
		TreeMap<Integer, String> map = new TreeMap<Integer, String>();

		for (String n : names) {
			int i = getLevenshtein(name, n);
			map.put(i, n);
		}
		
		String[] arr = new String[numSuggestions];
		
		
		for (int i = 0; i < arr.length; i++) {
			
			if ( map.firstEntry() != null) {
				Entry<Integer,String> s = map.pollFirstEntry();
				
				arr[i] = s.getValue();
			}
		}

		return arr;
	}
}
