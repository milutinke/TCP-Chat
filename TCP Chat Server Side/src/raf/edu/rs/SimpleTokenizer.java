package raf.edu.rs;

public class SimpleTokenizer {
	public static String getToken(String input) {
		StringBuilder sb = new StringBuilder();
	
		for(int characterPosition = 0; characterPosition < input.length(); characterPosition ++ ) {
			if(input.charAt(characterPosition) == ' ')
				break;
		
			sb.append(input.charAt(characterPosition));
		}
			
		return sb.toString();
	}

	public static String getValue(String token, String input) {
		input = input.replaceAll(token, "");
		input = input.trim();

		return input;
	}
	
	public static boolean hasToken(String input, String token) {
		return input.contains(token);
	}
}
